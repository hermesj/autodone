package de.uoc.dh.idh.autodone.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.uoc.dh.idh.autodone.entities.MastodonPost;
import de.uoc.dh.idh.autodone.repositories.MastodonPostRepository;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@NoArgsConstructor
@Service
@Transactional
public class ScheduleService {

    @Autowired
    MastodonService mastodonService;

    @Autowired
    MastodonPostRepository postRepository;

    @Autowired
    MastodonPostRepository mastodonPostRepository;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    private Map<Integer, ScheduledFuture<?>> tasks = new HashMap<>();

    @Transactional
    public MastodonPost schedulePost(MastodonPost post) {

        int delay = getDelay(post);

        if (delay < 0) {
            post.setError(true);
            //log.info("Delay " + delay + " post not scheduled");
            return post;
        }

        TimerTask task = new PostTask(post.getMastodonuser(), post, mastodonService, postRepository);
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(task, delay, TimeUnit.SECONDS);
        //log.info("Post " + post.getId() + " scheduled");
        if (post.isScheduled()) {
            cancelScheduling(post);
            post.setScheduled(true);
            tasks.put(post.getId(), scheduledFuture);
            return post;
        } else if (post.isEnabled() && !post.isScheduled()) {
            post.setScheduled(true);
            tasks.put(post.getId(), scheduledFuture);
        }

        return post;
    }


    public MastodonPost cancelScheduling(MastodonPost post) {
        try {
            tasks.get(post.getId()).cancel(true);
            tasks.remove(post.getId());
            post.setScheduled(false);
            //log.info("Scheduling of post " + post.getId() + " stopped");
        } catch (NullPointerException ne) {
            //log.info("Post " + post.getId() + " already canceled");
        }

        return post;
    }


    public int getDelay(MastodonPost post) {
        //log.info(getZoneIdFromTimezoneOffset(post.getTimezoneOffset()));
        String dateNow = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now(getZoneIdFromTimezoneOffset(post.getTimezoneOffset())));
        String timeNow = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now(getZoneIdFromTimezoneOffset(post.getTimezoneOffset())));
        //log.info("SERVER-TIME: " + timeNow);
        //log.info("SERVER-DATE: " + dateNow);
        String datePost = post.getDate();
        String timePost = post.getTime() + ":00";


        LocalDateTime d1 = LocalDateTime.parse(datePost + " " + timePost, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime d2 = LocalDateTime.parse(dateNow + " " + timeNow, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Duration diff = Duration.between(d2, d1);
        //log.info("Delay: " + (int) diff.getSeconds() + "s");
        return (int) diff.getSeconds();
    }

    public Map<Integer, ScheduledFuture<?>> getTasks() {
        return tasks;
    }


    public void scheduleAll() {
        List<MastodonPost> toSchedule = postRepository.findByEnabledAndPostedAndError(true, false, false);
        if (toSchedule != null) toSchedule.forEach(post -> schedulePost(post));
    }

    public void killAllTasks() {

        List<Integer> toRemove = new ArrayList<>();

        for (Map.Entry<Integer, ScheduledFuture<?>> entry : tasks.entrySet()) {
            Optional<MastodonPost> postOptional = mastodonPostRepository.findById(entry.getKey());
            MastodonPost post = postOptional.get();
            tasks.get(post.getId()).cancel(true);
            toRemove.add(post.getId());
        }


        toRemove.forEach(id -> tasks.remove(id));
    }



    private ZoneId getZoneIdFromTimezoneOffset(int timeZone){

        return ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(timeZone*-60));

    }
}
