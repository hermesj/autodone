package de.uoc.dh.idh.autodone.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


import de.uoc.dh.idh.autodone.entities.MastodonPost;

public interface MastodonPostRepository extends CrudRepository<MastodonPost, Integer> {

    void deleteByIdAndPostgroupId(int postId, int groupId);

    MastodonPost findByIdAndMastodonuserId(int postId, int userId);
    
    MastodonPost findById(int id);

    MastodonPost findByIdAndPostgroupId(int postId, int groupId);

    MastodonPost findByIdAndPostgroupIdAndMastodonuserId(int postId, int groupId, int id);

    List<MastodonPost> findByScheduledAndMastodonuserId(boolean scheduled, int userId);

    List<MastodonPost> findByPostedAndMastodonuserId(boolean posted, int userId);

    List<MastodonPost> findByEnabledAndMastodonuserId(boolean enabled, int userId);

    List<MastodonPost> findByErrorAndMastodonuserId(boolean error, int userId);

    List<MastodonPost> findByEnabledAndPostedAndMastodonuserId(boolean enabled, boolean posted, int userId);

    List<MastodonPost> findByEnabledAndPostedAndError(boolean enabled, boolean posted, boolean error);
    List<MastodonPost> findByEnabled(boolean enabled);
    List<MastodonPost> findByPosted(boolean enabled);
    List<MastodonPost> findByScheduled(boolean enabled);
    List<MastodonPost> findByError(boolean error);
    List<MastodonPost> findAllByPostgroupIdAndPosted(int groupId,boolean isPosted);

    List<MastodonPost> findAllByPostgroupIdAndError(int id, boolean isError);

    List<MastodonPost> findAllByPostgroupId(int id);
}
