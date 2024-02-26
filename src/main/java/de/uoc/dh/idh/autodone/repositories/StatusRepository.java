package de.uoc.dh.idh.autodone.repositories;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.base.BaseRepository;
import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import jakarta.transaction.Transactional;

@Repository()
@Transactional()
public interface StatusRepository extends BaseRepository<StatusEntity> {

	public void deleteByUuidAndGroupTokenUsernameAndGroupTokenServerDomain(

			UUID uuid,

			String username,

			String domain

	);

	//

	public Iterable<StatusEntity> findAllByDateAfterAndGroupEnabledTrueAndIdIsNull(

			Instant date

	);

	//

	public Iterable<StatusEntity> findAllByGroupTokenUsernameAndGroupTokenServerDomain(

			String username,

			String domain

	);

	//

	public Page<StatusEntity> findAllByGroupTokenUsernameAndGroupTokenServerDomain(

			Pageable pageable,

			String username,

			String domain

	);

	//

	public StatusEntity findOneByUuidAndGroupTokenUsernameAndGroupTokenServerDomain(

			UUID uuid,

			String username,

			String domain

	);

	//

	public StatusEntity findTopByGroupAndDateAfterOrderByDate(

			GroupEntity group,

			Instant date

	);

}
