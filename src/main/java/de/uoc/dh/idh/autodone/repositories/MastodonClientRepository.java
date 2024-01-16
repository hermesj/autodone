package de.uoc.dh.idh.autodone.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.entities.MastodonClient;

@Repository()
public interface MastodonClientRepository extends CrudRepository<MastodonClient, String> {

	public MastodonClient findByClientRegistrationIdAndPrincipalName(String registrationId, String principalName);

}
