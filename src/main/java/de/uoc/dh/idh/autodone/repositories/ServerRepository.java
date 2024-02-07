package de.uoc.dh.idh.autodone.repositories;

import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.base.BaseRepository;
import de.uoc.dh.idh.autodone.entities.ServerEntity;
import jakarta.transaction.Transactional;

@Repository()
@Transactional()
public interface ServerRepository extends BaseRepository<ServerEntity> {

	public ServerEntity findOneByDomain(

			String domain

	);

}
