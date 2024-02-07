package de.uoc.dh.idh.autodone.services;

import static org.springframework.data.domain.Sort.by;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.uoc.dh.idh.autodone.base.BaseService;
import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.repositories.GroupRepository;
import jakarta.transaction.Transactional;;

@Service()
@Transactional()
public class GroupService extends BaseService<GroupEntity> {

	@Autowired()
	private GroupRepository groupRepository;

	@Autowired()
	private TimerService timerService;

	@Autowired()
	private TokenService tokenService;

	//

	public Iterable<GroupEntity> getAll() {
		return groupRepository.findAll();
	}

	//

	public GroupEntity save(GroupEntity group) {
		group.token = tokenService.getOne();
		group = groupRepository.save(group);
		timerService.unscheduleGroup(group);

		if (group.enabled) {
			timerService.scheduleGroup(group);
		}

		return group;
	}

	//

	@Override()
	public void delete(UUID uuid, String username, String domain) {
		groupRepository //
				.deleteByUuidAndTokenUsernameAndTokenServerDomain(uuid, username, domain);
	}

	@Override()
	public GroupEntity getOne(UUID uuid, String username, String domain) {
		return groupRepository //
				.findOneByUuidAndTokenUsernameAndTokenServerDomain(uuid, username, domain);
	}

	@Override()
	public Page<GroupEntity> getPage(Pageable request, String username, String domain) {
		return groupRepository //
				.findAllByTokenUsernameAndTokenServerDomain(request, username, domain);
	}

	//

	@Override()
	protected Sort getSort() {
		return by("name");
	}

}
