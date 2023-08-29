package de.uoc.dh.idh.autodone.repositories;

import org.springframework.data.repository.CrudRepository;

import de.uoc.dh.idh.autodone.entities.MastodonUser;

public interface MastodonUserRepository extends CrudRepository<MastodonUser, Integer> {

    int findIdByMstdId(String mstdnId);
    MastodonUser findMastodonUserById(int Id);
    MastodonUser findMastodonUserByMstdId(String id);


}
