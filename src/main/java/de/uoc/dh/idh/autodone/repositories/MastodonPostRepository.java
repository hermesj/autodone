package de.uoc.dh.idh.autodone.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


import de.uoc.dh.idh.autodone.entities.MastodonPost;

public interface MastodonPostRepository extends CrudRepository<MastodonPost, Integer> {

    void deleteByIdAndPostGroupId(int postId, String groupId);

    MastodonPost findByIdAndMastodonUserId(int postId, int userId);
    
    MastodonPost findById(int id);

    MastodonPost findByIdAndPostGroupId(int postId, String groupId);

    MastodonPost findByIdAndPostGroupIdAndMastdonUserId(int postId, String groupId, int id);

    List<MastodonPost> findByScheduledAndMastodonUserId(boolean scheduled, int userId);

    List<MastodonPost> findByPostedAndMastodonUserId(boolean posted, int userId);

    List<MastodonPost> findByEnabledAndMastodonUserId(boolean enabled, int userId);

    List<MastodonPost> findByErrorAndMastodonUserId(boolean error, int userId);

    List<MastodonPost> findByEnabledAndPostedAndMastodonUserId(boolean enabled, boolean posted, int userId);

    List<MastodonPost> findByEnabledAndPostedAndError(boolean enabled, boolean posted, boolean error);
    List<MastodonPost> findByEnabled(boolean enabled);
    List<MastodonPost> findByPosted(boolean enabled);
    List<MastodonPost> findByScheduled(boolean enabled);
    List<MastodonPost> findByError(boolean error);
    List<MastodonPost> findAllByPostGroupIdAndPosted(String groupId,boolean isPosted);

    List<MastodonPost> findAllByPostGroupIdAndError(String id, boolean isError);

    List<MastodonPost> findAllByPostGroupId(String id);
}
