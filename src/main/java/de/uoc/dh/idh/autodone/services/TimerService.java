package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_SCHEDULING;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_THREADPOOL;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;

@Service()
@Transactional()
public class TimerService {

	private static final Logger log = LoggerFactory.getLogger(TimerService.class);

	private static final int MEDIA_PUBLISH_RETRY_DELAY = 10;

	private static final int MEDIA_PUBLISH_MAX_RETRIES = 30;

	//

	private final Map<UUID, ScheduledFuture<?>> scheduledMedia = new ConcurrentHashMap<>();

	private final Map<UUID, ScheduledFuture<?>> scheduledStatus = new ConcurrentHashMap<>();

	private final ScheduledExecutorService scheduler = newScheduledThreadPool(AUTODONE_THREADPOOL);

	//

	@Autowired()
	private MediaService mediaService;

	@Autowired()
	private StatusService statusService;

	@Autowired()
	private PlatformTransactionManager transactionManager;

	//

	public void scheduleGroup(GroupEntity group) {
		if (group.status != null) {
			for (var status : group.status) {
				scheduleStatus(status);
			}
		}
	}

	public void unscheduleGroup(GroupEntity group) {
		if (group.status != null) {
			for (var status : group.status) {
				unscheduleStatus(status);
			}
		}
	}

	//

	public ScheduledFuture<?> scheduleStatus(StatusEntity status) {
		if (status.id != null) {
			return null;
		}

		var delay = between(now(), status.date).getSeconds();

		if (delay > 0) {
			var uuid = status.uuid;

			scheduledStatus.computeIfAbsent(uuid, (key) -> {
				log.info("Scheduling status {} in {} seconds", uuid, delay);

				return scheduler.schedule(() -> {
					scheduledStatus.remove(uuid);

					try {
						new TransactionTemplate(transactionManager).executeWithoutResult((ignored) -> {
							statusService.publish(uuid);
						});
						log.info("Published status {}", uuid);
					} catch (Exception exception) {
						log.error("Failed to publish status {}: {}", uuid, exception.getMessage());
					}

					return null;
				}, delay, SECONDS);
			});
		}

		if (status.media != null) {
			for (var media : status.media) {
				scheduleMedia(media);
			}
		}

		return scheduledStatus.get(status.uuid);
	}

	public boolean unscheduleStatus(StatusEntity status) {
		if (status.media != null) {
			for (var media : status.media) {
				unscheduleMedia(media);
			}
		}

		var future = scheduledStatus.remove(status.uuid);
		return future != null && future.cancel(false);
	}

	//

	public ScheduledFuture<?> scheduleMedia(MediaEntity media) {
		if (media.id != null) {
			return null;
		}

		var delay = between(now().plusSeconds(AUTODONE_SCHEDULING), media.status.date).getSeconds();

		if (delay > 0) {
			var uuid = media.uuid;

			scheduledMedia.computeIfAbsent(uuid, (key) -> {
				log.info("Scheduling media {} in {} seconds", uuid, delay);
				return scheduleMediaPublish(uuid, delay, 0);
			});
		}

		return scheduledMedia.get(media.uuid);
	}

	private ScheduledFuture<?> scheduleMediaPublish(UUID uuid, long delay, int attempt) {
		return scheduler.schedule(() -> {
			scheduledMedia.remove(uuid);

			try {
				var published = new TransactionTemplate(transactionManager).execute((ignored) -> {
					return mediaService.publish(uuid).id != null;
				});

				if (Boolean.TRUE.equals(published)) {
					log.info("Published media {}", uuid);
				} else if (attempt < MEDIA_PUBLISH_MAX_RETRIES) {
					log.info("Media {} not ready, retrying in {}s (attempt {}/{})",
							uuid, MEDIA_PUBLISH_RETRY_DELAY, attempt + 1, MEDIA_PUBLISH_MAX_RETRIES);
					scheduledMedia.put(uuid, scheduleMediaPublish(uuid, MEDIA_PUBLISH_RETRY_DELAY, attempt + 1));
				} else {
					log.error("Media {} not ready after {} attempts, giving up", uuid, MEDIA_PUBLISH_MAX_RETRIES);
				}
			} catch (Exception exception) {
				log.error("Failed to publish media {}: {}", uuid, exception.getMessage());
			}

			return null;
		}, delay, SECONDS);
	}

	public boolean unscheduleMedia(MediaEntity media) {
		var future = scheduledMedia.remove(media.uuid);
		return future != null && future.cancel(false);
	}

	//

	@PostConstruct()
	private void postConstruct() {
		var entities = new TransactionTemplate(transactionManager).execute((ignored) -> {
			return statusService.getAll(now().plusSeconds(AUTODONE_SCHEDULING));
		});

		for (var entity : entities) {
			new TransactionTemplate(transactionManager).executeWithoutResult((ignored) -> {
				scheduleStatus(statusService.getAny(entity.getUuid()));
			});
		}

		log.info("TimerService initialized, {} status tasks scheduled", scheduledStatus.size());
	}

	@PreDestroy()
	private void preDestroy() {
		log.info("Shutting down TimerService, cancelling {} status and {} media tasks",
				scheduledStatus.size(), scheduledMedia.size());

		scheduler.shutdownNow();

		try {
			if (!scheduler.awaitTermination(10, SECONDS)) {
				log.warn("TimerService scheduler did not terminate within 10 seconds");
			}
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
		}
	}

}
