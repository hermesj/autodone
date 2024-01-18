package de.uoc.dh.idh.autodone.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.entities.MastodonClientEntity;

@Repository()
public interface MastodonClientRepository extends CrudRepository<MastodonClientEntity, String> {

	public MastodonClientEntity findByClientRegistrationIdAndPrincipalName(String registrationId, String principalName);

}
