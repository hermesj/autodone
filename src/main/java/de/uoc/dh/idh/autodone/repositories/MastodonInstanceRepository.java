package de.uoc.dh.idh.autodone.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.entities.MastodonInstance;

@Repository()
public interface MastodonInstanceRepository extends CrudRepository<MastodonInstance, String> {

	public MastodonInstance findByDomain(String domain);

}
