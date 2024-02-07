package de.uoc.dh.idh.autodone.repositories;

import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.base.BaseRepository;
import de.uoc.dh.idh.autodone.entities.TokenEntity;
import jakarta.transaction.Transactional;

@Repository()
@Transactional()
public interface TokenRepository extends BaseRepository<TokenEntity> {

	public TokenEntity findOneByUsernameAndServerDomain(

			String username,

			String domain

	);

}
