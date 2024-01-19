package de.uoc.dh.idh.autodone.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.entities.MastodonInstanceEntity;

@Repository()
public interface MastodonInstanceRepository extends CrudRepository<MastodonInstanceEntity, String> {

	public MastodonInstanceEntity findOneByDomain(String domain);

	public MastodonInstanceEntity getOneByDomain(String domain);

}
