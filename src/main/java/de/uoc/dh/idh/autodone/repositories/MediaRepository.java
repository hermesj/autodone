package de.uoc.dh.idh.autodone.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.base.BaseRepository;
import de.uoc.dh.idh.autodone.entities.MediaEntity;
import jakarta.transaction.Transactional;

@Repository()
@Transactional()
public interface MediaRepository extends BaseRepository<MediaEntity> {

	void deleteByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(

			UUID uuid,

			String username,

			String domain

	);

	//

	Page<MediaEntity> findAllByStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(

			Pageable pageable,

			String username,

			String domain

	);

	//

	MediaEntity findOneByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(

			UUID uuid,

			String username,

			String domain

	);

}
