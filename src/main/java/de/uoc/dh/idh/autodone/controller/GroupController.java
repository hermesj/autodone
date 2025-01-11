package de.uoc.dh.idh.autodone.controller;

import static de.uoc.dh.idh.autodone.utils.ObjectUtils.FORCE;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.mapFields;
import static de.uoc.dh.idh.autodone.utils.WebUtils.href;
import static java.util.Map.of;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import de.uoc.dh.idh.autodone.services.GroupService;
import de.uoc.dh.idh.autodone.services.StatusService;
import jakarta.servlet.http.HttpSession;

@Controller()
@RequestMapping("/group")
public class GroupController {

	@Autowired()
	private GroupService groupService;

	@Autowired()
	private StatusService statusService;

	@Autowired()
	private HttpSession httpSession;

	//

	@DeleteMapping()
	public String delete(@RequestParam() Map<String, String> params) {
		groupService.delete(params.get("uuid"));
		return "redirect:" + href();
	}

	//

	@GetMapping()
	public String get(Model model, @RequestParam() Map<String, String> params) {
		if (params.containsKey("uuid")) {
			var group = groupService.getOne(params.get("uuid"));
			var status = mapFields(of("group", group), new StatusEntity());
			var page = statusService.getPage(params.get("page"), params.get("sort"), group);

			model.addAttribute("group", group);
			model.addAttribute("status", status);
			model.addAttribute("page", page);
			return "entity/group";
		} else {
			var group = new GroupEntity();
			var page = groupService.getPage(params.get("page"), params.get("sort"));

			model.addAttribute("group", group);
			model.addAttribute("page", page);
			return "entities/group";
		}
	}

	//

	@PostMapping("/reschedule")
	public String postReschedule(@RequestParam() Map<String, String> form) {

		GroupEntity group = groupService.getOne((String) form.get("uuid"));

		List<StatusEntity> sortedStatuses = group.getStatus().stream()
				.sorted(Comparator.comparing(StatusEntity::getDate))
				.collect(Collectors.toList());

		StatusEntity firstStatus = sortedStatuses.get(0);

		Instant firstStatusTime = firstStatus.getDate();

		String newTimeString = form.get("newTime");
		LocalDateTime localDateTime = LocalDateTime.parse(newTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		var offset = (ZoneOffset) httpSession.getAttribute("zoneOffset");
		Instant newScheduledTime = localDateTime.toInstant(offset);

		Duration timeDifference = Duration.between(firstStatusTime, newScheduledTime);

		firstStatus.setDate(newScheduledTime);
		statusService.save(firstStatus);

		for (StatusEntity status : sortedStatuses.subList(1, sortedStatuses.size())) {
			if (!status.equals(firstStatus)) {
				Instant newStatusTime = status.getDate().plus(timeDifference);
				status.setDate(newStatusTime);
				statusService.save(status);
			}
		}
		groupService.save(group);

		return "redirect:/group?uuid=" + form.get("uuid");
	}

	//

	@PostMapping()
	public String post(@RequestParam() Map<String, String> form) {
		var group = new GroupEntity();

		if (form.containsKey("uuid")) {
			group = groupService.getOne((String) form.get("uuid"));
		}

		var save = groupService.save(mapFields(form, group, FORCE));
		return "redirect:/group?uuid=" + save.uuid;
	}

}
