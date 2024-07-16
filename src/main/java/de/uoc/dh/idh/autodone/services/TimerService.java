package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_SCHEDULING;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_THREADPOOL;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service()
@Transactional()
public class TimerService {

	private final static Map<UUID, ScheduledFuture<MediaEntity>> scheduledMedia = new HashMap<>();

	private final static Map<UUID, ScheduledFuture<StatusEntity>> scheduledStatus = new HashMap<>();

	private final static ScheduledExecutorService scheduler = newScheduledThreadPool(AUTODONE_THREADPOOL);

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

	public ScheduledFuture<StatusEntity> scheduleStatus(StatusEntity status) {
		var delay = between(now(), status.date).getSeconds();
		var future = scheduledStatus.get(status.uuid);

		if (status.id == null) {
			if (delay > AUTODONE_SCHEDULING && future == null) {
				var uuid = status.uuid;

				future = scheduledStatus.put(uuid, scheduler.schedule(() -> {
					scheduledStatus.remove(uuid);
					return statusService.publish(uuid);
				}, delay, SECONDS));
			}

			if (status.media != null) {
				for (var media : status.media) {
					scheduleMedia(media);
				}
			}
		}

		return future;
	}

	public boolean unscheduleStatus(StatusEntity status) {
		if (status.media != null) {
			for (var media : status.media) {
				unscheduleMedia(media);
			}
		}

		var future = scheduledStatus.remove(status.uuid);
		return future == null ? false : future.cancel(true);
	}

	//

	public ScheduledFuture<MediaEntity> scheduleMedia(MediaEntity media) {
		var delay = between(now().plusSeconds(AUTODONE_SCHEDULING), media.status.date).getSeconds();
		var future = scheduledMedia.get(media.uuid);

		if (media.id == null) {
			if (delay > AUTODONE_SCHEDULING && future == null) {
				var uuid = media.uuid;

				future = scheduledMedia.put(uuid, scheduler.schedule(() -> {
					scheduledMedia.remove(uuid);
					return mediaService.publish(uuid);
				}, delay, SECONDS));
			}
		}

		return future;
	}

	public boolean unscheduleMedia(MediaEntity media) {
		var future = scheduledMedia.remove(media.uuid);
		return future == null ? false : future.cancel(true);

	}

	//

	@PostConstruct()
	private void postConstruct() {
		for (var entity : new TransactionTemplate(transactionManager).execute((ignored) -> {
			return statusService.getAll(now().plusSeconds(AUTODONE_SCHEDULING));
		})) {
			new TransactionTemplate(transactionManager).executeWithoutResult((ignored) -> {
				scheduleStatus(statusService.getAny(entity.getUuid()));
			});
		}
	}

}
