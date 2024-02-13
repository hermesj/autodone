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

	private final static Map<UUID, ScheduledFuture<MediaEntity>> scheduledMedia;

	private final static Map<UUID, ScheduledFuture<StatusEntity>> scheduledStatus;

	private final static ScheduledExecutorService scheduler;

	static {
		scheduledMedia = new HashMap<>();
		scheduledStatus = new HashMap<>();
		scheduler = newScheduledThreadPool(AUTODONE_THREADPOOL);
	}

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

		if (delay > AUTODONE_SCHEDULING && future == null) {
			future = scheduledStatus.put(status.uuid, scheduler.schedule(() -> {
				scheduledStatus.remove(status.uuid);
				return statusService.publish(status.uuid);
			}, delay, SECONDS));
		}

		if (status.media != null) {
			for (var media : status.media) {
				scheduleMedia(media);
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

		if (delay > AUTODONE_SCHEDULING && future == null) {
			future = scheduledMedia.put(media.uuid, scheduler.schedule(() -> {
				scheduledMedia.remove(media.uuid);
				return mediaService.publish(media.uuid);
			}, delay, SECONDS));
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
		new TransactionTemplate(transactionManager).executeWithoutResult((ignored) -> {
			for (var status : statusService.getAll(now().plusSeconds(AUTODONE_SCHEDULING))) {
				scheduleStatus(status);
			}
		});
	}

}
