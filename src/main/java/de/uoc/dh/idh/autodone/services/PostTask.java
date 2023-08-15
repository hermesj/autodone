package de.uoc.dh.idh.autodone.services;


import java.util.TimerTask;

import org.springframework.transaction.annotation.Transactional;

import de.uoc.dh.idh.autodone.entities.MastodonPost;
import de.uoc.dh.idh.autodone.entities.MastodonUser;
import de.uoc.dh.idh.autodone.repositories.MastodonPostRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * The task that is put into the @{@link ScheduleService}
 *
 *
 */
@Log4j2
@ToString
@Getter
@Setter
@NoArgsConstructor
@Transactional
public class PostTask extends TimerTask {

 
    private MastodonUser user;

   
    private MastodonPost post;

    private MastodonPostRepository mastodonPostRepository;
    private MastodonService mastodonService;

    public PostTask(MastodonUser user, MastodonPost post, MastodonService mastodonService, MastodonPostRepository mastodonPostRepository) {
        this.user = user;
        this.post = post;
        this.mastodonService = mastodonService;
        this.mastodonPostRepository = mastodonPostRepository;
    }

    
    @Transactional
    @Override
    public void run() {

        if (post != null && user != null && !post.isPosted() && post.isScheduled() && post.isEnabled()) {
            String id = mastodonService.post(user, post);
            MastodonPost posted = mastodonPostRepository.findByIdAndMastodonUserId(post.getId(), user.getId());
            if(id != null && !id.isEmpty()){
                posted.setPosted(true);
                posted.setEnabled(false);
                posted.setScheduled(false);
                mastodonPostRepository.save(posted);
                //log.info("Post-id: " +id);
            } else {
                //log.error("No id callback");
                posted.setPosted(false);
                posted.setEnabled(false);
                posted.setScheduled(false);
                posted.setError(true);
                mastodonPostRepository.save(posted);
            }


        } else {
            //log.info("Not valid to post: " + post);
        }

    }
}
