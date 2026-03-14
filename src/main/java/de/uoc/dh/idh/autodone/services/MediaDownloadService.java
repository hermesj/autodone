package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_FORMAT;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_SIZE_X;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_SIZE_Y;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_SCHEDULING;
import static de.uoc.dh.idh.autodone.entities.MediaEntity.LoadStatus.ERROR;
import static de.uoc.dh.idh.autodone.entities.MediaEntity.LoadStatus.LOADING;
import static de.uoc.dh.idh.autodone.entities.MediaEntity.LoadStatus.PENDING;
import static de.uoc.dh.idh.autodone.entities.MediaEntity.LoadStatus.READY;
import static java.awt.Image.SCALE_FAST;
import static java.time.Instant.now;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.repositories.MediaRepository;
import jakarta.annotation.PreDestroy;

@Service()
public class MediaDownloadService {

	private static final Logger log = LoggerFactory.getLogger(MediaDownloadService.class);

	private static final String USER_AGENT = "autodone/1.0 (https://github.com/spinfo/autodone)";

	private static final int CONNECT_TIMEOUT = 10000;

	private static final int READ_TIMEOUT = 30000;

	private static final int DOWNLOAD_DELAY = 2;

	private static final int MAX_RETRIES = 3;

	private static final int MAX_DOWNLOAD_SIZE = 10 * 1024 * 1024; // 10 MB

	private static final int[] RETRY_DELAYS = { 10, 30, 90 };

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private final Map<UUID, Integer> retryCounts = new ConcurrentHashMap<>();

	private final Map<UUID, Integer> publishRetryCounts = new ConcurrentHashMap<>();

	//

	@Autowired()
	private MediaRepository mediaRepository;

	@Autowired()
	private MediaService mediaService;

	@Autowired()
	private PlatformTransactionManager transactionManager;

	//

	public void downloadAllPending(List<UUID> mediaUuids) {
		var sorted = new TransactionTemplate(transactionManager).execute((ignored) -> {
			return mediaUuids.stream()
					.map(uuid -> mediaRepository.findById(uuid).orElse(null))
					.filter(media -> media != null && media.status != null)
					.sorted((a, b) -> a.status.date.compareTo(b.status.date))
					.map(media -> media.uuid)
					.toList();
		});

		var delay = 0;

		for (var uuid : sorted) {
			scheduler.schedule(() -> downloadOne(uuid), delay, SECONDS);
			delay += DOWNLOAD_DELAY;
		}

		log.info("Queued {} media downloads with {}s spacing, prioritized by status date", sorted.size(), DOWNLOAD_DELAY);
	}

	@PreDestroy()
	private void preDestroy() {
		log.info("Shutting down MediaDownloadService, stopping scheduler");

		scheduler.shutdownNow();

		try {
			if (!scheduler.awaitTermination(10, SECONDS)) {
				log.warn("MediaDownloadService scheduler did not terminate within 10 seconds");
			}
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
		}
	}

	//

	private void downloadOne(UUID uuid) {
		var shouldProceed = new TransactionTemplate(transactionManager).execute((ignored) -> {
			var media = mediaRepository.findById(uuid).orElse(null);

			if (media == null || (media.loadStatus != PENDING && media.loadStatus != ERROR)) {
				return false;
			}

			media.loadStatus = LOADING;
			mediaRepository.save(media);
			return true;
		});

		if (!Boolean.TRUE.equals(shouldProceed)) {
			return;
		}

		try {
			var result = fetchAndResize(uuid);

			new TransactionTemplate(transactionManager).executeWithoutResult((ignored) -> {
				var media = mediaRepository.findById(uuid).orElse(null);

				if (media == null) {
					return;
				}

				media.contentType = result.contentType;
				media.file = result.file;
				media.loadStatus = READY;
				mediaRepository.save(media);
			});

			retryCounts.remove(uuid);
			log.info("Downloaded media {} from {}", uuid, result.sourceUrl);

			publishIfOverdue(uuid);
		} catch (Exception exception) {
			var retries = retryCounts.getOrDefault(uuid, 0);

			if (retries < MAX_RETRIES) {
				retryCounts.put(uuid, retries + 1);
				var retryDelay = RETRY_DELAYS[retries];

				new TransactionTemplate(transactionManager).executeWithoutResult((ignored) -> {
					var media = mediaRepository.findById(uuid).orElse(null);

					if (media != null) {
						media.loadStatus = PENDING;
						mediaRepository.save(media);
					}
				});

				scheduler.schedule(() -> downloadOne(uuid), retryDelay, SECONDS);
				log.warn("Download failed for media {}, retry {}/{} in {}s: {}",
						uuid, retries + 1, MAX_RETRIES, retryDelay, exception.getMessage());
			} else {
				new TransactionTemplate(transactionManager).executeWithoutResult((ignored) -> {
					var media = mediaRepository.findById(uuid).orElse(null);

					if (media != null) {
						media.loadStatus = ERROR;
						mediaRepository.save(media);
					}
				});

				retryCounts.remove(uuid);
				log.error("Download permanently failed for media {} after {} retries: {}",
						uuid, MAX_RETRIES, exception.getMessage());
			}
		}
	}

	private void publishIfOverdue(UUID uuid) {
		var shouldPublish = new TransactionTemplate(transactionManager).execute((ignored) -> {
			var media = mediaRepository.findById(uuid).orElse(null);

			if (media == null || media.id != null) {
				return false;
			}

			if (media.status == null || media.status.id != null) {
				return false;
			}

			if (!media.status.group.enabled) {
				return false;
			}

			var publishTime = media.status.date.minusSeconds(AUTODONE_SCHEDULING);
			return now().isAfter(publishTime);
		});

		if (!Boolean.TRUE.equals(shouldPublish)) {
			return;
		}

		try {
			log.info("Download completed after scheduled time, publishing media {} immediately", uuid);
			mediaService.publish(uuid);
			publishRetryCounts.remove(uuid);
		} catch (Exception exception) {
			var retries = publishRetryCounts.getOrDefault(uuid, 0);

			if (retries < MAX_RETRIES) {
				publishRetryCounts.put(uuid, retries + 1);
				var retryDelay = RETRY_DELAYS[retries];

				scheduler.schedule(() -> publishIfOverdue(uuid), retryDelay, SECONDS);
				log.warn("Publish failed for media {}, retry {}/{} in {}s: {}",
						uuid, retries + 1, MAX_RETRIES, retryDelay, exception.getMessage());
			} else {
				publishRetryCounts.remove(uuid);
				log.error("Publish permanently failed for media {} after {} retries: {}",
						uuid, MAX_RETRIES, exception.getMessage());
			}
		}
	}

	//

	private DownloadResult fetchAndResize(UUID uuid) throws Exception {
		var result = new DownloadResult();

		var media = new TransactionTemplate(transactionManager).execute((ignored) -> {
			return mediaRepository.findById(uuid).orElse(null);
		});

		if (media == null || media.sourceUrl == null) {
			throw new IllegalStateException("No source URL for media " + uuid);
		}

		result.sourceUrl = media.sourceUrl;

		var connection = openConnection(media.sourceUrl);

		try {
			result.contentType = connection.getContentType();

			if (result.contentType == null || !result.contentType.startsWith("image/")) {
				throw new IllegalArgumentException("Not an image file: " + result.contentType);
			}

			try (var inputStream = new BoundedInputStream(connection.getInputStream(), MAX_DOWNLOAD_SIZE)) {
				if (connection.getContentLength() < 1024000 && connection.getContentLength() > 0) {
					result.file = inputStream.readAllBytes();
				} else {
					var buffer = new ByteArrayOutputStream();
					var source = read(inputStream);

					if (source == null) {
						throw new IllegalArgumentException("Could not read image from URL");
					}

					var scaleX = source.getWidth();
					var scaleY = source.getHeight();

					if (scaleX > AUTODONE_IMG_SIZE_X) {
						scaleX = AUTODONE_IMG_SIZE_X;
						scaleY = (scaleX * source.getHeight()) / source.getWidth();
					}

					if (scaleY > AUTODONE_IMG_SIZE_Y) {
						scaleY = AUTODONE_IMG_SIZE_Y;
						scaleX = (scaleY * source.getWidth()) / source.getHeight();
					}

					var target = new BufferedImage(scaleX, scaleY, TYPE_INT_ARGB);
					var scaled = source.getScaledInstance(scaleX, scaleY, SCALE_FAST);
					var graphics = target.createGraphics();

					try {
						graphics.drawImage(scaled, 0, 0, null);
					} finally {
						graphics.dispose();
					}

					write(target, AUTODONE_IMG_FORMAT, buffer);

					result.contentType = "image/" + AUTODONE_IMG_FORMAT;
					result.file = buffer.toByteArray();
				}
			}
		} finally {
			connection.disconnect();
		}

		return result;
	}

	private HttpURLConnection openConnection(String url) throws Exception {
		var maxRedirects = 5;

		for (var i = 0; i < maxRedirects; i++) {
			validateUrl(url);

			var connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty("User-Agent", USER_AGENT);

			var status = connection.getResponseCode();

			if (status == HttpURLConnection.HTTP_MOVED_TEMP
					|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER
					|| status == 307 || status == 308) {
				url = connection.getHeaderField("Location");
				connection.disconnect();
				continue;
			}

			return connection;
		}

		throw new IllegalStateException("Too many redirects for URL: " + url);
	}

	private void validateUrl(String url) throws Exception {
		var uri = URI.create(url);

		if (!"https".equals(uri.getScheme())) {
			throw new SecurityException("Only HTTPS URLs allowed: " + url);
		}

		var host = uri.getHost();

		if (host == null || host.isBlank()) {
			throw new SecurityException("Missing host in URL: " + url);
		}

		var address = InetAddress.getByName(host);

		if (address.isLoopbackAddress()
				|| address.isLinkLocalAddress()
				|| address.isSiteLocalAddress()
				|| address.isAnyLocalAddress()) {
			throw new SecurityException("Private/internal host not allowed: " + host);
		}
	}

	//

	private static class DownloadResult {

		String contentType;

		byte[] file;

		String sourceUrl;

	}

	private static class BoundedInputStream extends FilterInputStream {

		private final long maxBytes;

		private long bytesRead = 0;

		BoundedInputStream(InputStream in, long maxBytes) {
			super(in);
			this.maxBytes = maxBytes;
		}

		@Override
		public int read() throws IOException {
			if (bytesRead >= maxBytes) {
				throw new IOException("Download exceeds size limit of " + maxBytes + " bytes");
			}

			var b = super.read();

			if (b != -1) {
				bytesRead++;
			}

			return b;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (bytesRead >= maxBytes) {
				throw new IOException("Download exceeds size limit of " + maxBytes + " bytes");
			}

			var allowed = (int) Math.min(len, maxBytes - bytesRead);
			var n = super.read(b, off, allowed);

			if (n > 0) {
				bytesRead += n;
			}

			return n;
		}
	}

}
