package de.uoc.dh.idh.autodone.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


import de.uoc.dh.idh.autodone.entities.MastodonPost;

public interface MastodonPostRepository extends CrudRepository<MastodonPost, Integer> {

    void deleteByIdAndPostgroup(int postId, String groupId);

    MastodonPost findByIdAndMastodonuser(int postId, int userId);
    
    MastodonPost findById(int id);

    MastodonPost findByIdAndPostgroup(int postId, String groupId);

    MastodonPost findByIdAndPostgroupAndMastdonuser(int postId, String groupId, int id);

    List<MastodonPost> findByScheduledAndMastodonuser(boolean scheduled, int userId);

    List<MastodonPost> findByPostedAndMastodonuser(boolean posted, int userId);

    List<MastodonPost> findByEnabledAndMastodonuser(boolean enabled, int userId);

    List<MastodonPost> findByErrorAndMastodonUser(boolean error, int userId);

    List<MastodonPost> findByEnabledAndPostedAndMastodonuser(boolean enabled, boolean posted, int userId);

    List<MastodonPost> findByEnabledAndPostedAndError(boolean enabled, boolean posted, boolean error);
    List<MastodonPost> findByEnabled(boolean enabled);
    List<MastodonPost> findByPosted(boolean enabled);
    List<MastodonPost> findByScheduled(boolean enabled);
    List<MastodonPost> findByError(boolean error);
    List<MastodonPost> findAllByPostgroupAndPosted(String groupId,boolean isPosted);

    List<MastodonPost> findAllByPostgroupAndError(String id, boolean isError);

    List<MastodonPost> findAllByPostgroup(String id);
}
