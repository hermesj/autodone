package de.uoc.dh.idh.autodone.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.entities.MastodonClientEntity;

@Repository()
public interface MastodonClientRepository extends CrudRepository<MastodonClientEntity, UUID> {

	public void deleteOneByInstanceDomainAndUsername(String domain, String username);

	public MastodonClientEntity findOneByInstanceDomainAndUsername(String domain, String username);

}
