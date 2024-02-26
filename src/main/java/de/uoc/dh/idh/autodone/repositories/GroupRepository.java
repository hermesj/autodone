package de.uoc.dh.idh.autodone.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.base.BaseRepository;
import de.uoc.dh.idh.autodone.entities.GroupEntity;
import jakarta.transaction.Transactional;

@Repository()
@Transactional()
public interface GroupRepository extends BaseRepository<GroupEntity> {

	public void deleteByUuidAndTokenUsernameAndTokenServerDomain(

			UUID uuid,

			String username,

			String domain

	);

	//

	public Iterable<GroupEntity> findAllByTokenUsernameAndTokenServerDomain(

			String username,

			String domain

	);

	//

	public Page<GroupEntity> findAllByTokenUsernameAndTokenServerDomain(

			Pageable pageable,

			String username,

			String domain

	);

	//

	public GroupEntity findOneByUuidAndTokenUsernameAndTokenServerDomain(

			UUID uuid,

			String username,

			String domain

	);

}
