package de.uoc.dh.idh.autodone.controller;

import static de.uoc.dh.idh.autodone.utils.ObjectUtils.FORCE;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.mapFields;
import static de.uoc.dh.idh.autodone.utils.WebUtils.href;

import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.services.GroupService;
import de.uoc.dh.idh.autodone.services.ImportService;
import de.uoc.dh.idh.autodone.services.StatusService;
import jakarta.servlet.http.HttpSession;

@Controller()
@RequestMapping("/import")
public class ImportController {

	@Autowired()
	private GroupService groupService;

	@Autowired()
	private HttpSession httpSession;

	@Autowired()
	private ImportService importService;

	@Autowired()
	private StatusService statusService;

	//

	@GetMapping()
	public String get(Model model, @RequestParam() Map<String, String> params) {
		var group = (GroupEntity) httpSession.getAttribute("import");
		var page = statusService.getPage(params.get("page"), params.get("sort"), group);
		var exceptions = new LinkedMultiValueMap<Integer, String>();

		for (var exception : group.exceptions) {
			exceptions.add(((ParseException) exception).getErrorOffset(), exception.getMessage());
		}

		for (var exception : group.status.stream().flatMap((s) -> s.exceptions.stream()).toList()) {
			exceptions.add(((ParseException) exception).getErrorOffset(), exception.getMessage());
		}

		model.addAttribute("group", group);
		model.addAttribute("page", page);
		model.addAttribute("exceptions", exceptions);
		return "entity/import";
	}

	//

	@PostMapping()
	public String post(@RequestBody() MultipartFile file, @RequestParam() Map<String, String> form) throws Exception {
		if (file != null) {
			var group = importService.importGroup(file.getInputStream());
			group.name = file.getOriginalFilename();

			httpSession.setAttribute("import", group);
			return "redirect:" + href();
		} else {
			var group = (GroupEntity) httpSession.getAttribute("import");

			var save = groupService.save(mapFields(form, group, FORCE));
			return "redirect:/group?uuid=" + save.uuid;
		}
	}

}
