package de.uoc.dh.idh.autodone.controller;

import static de.uoc.dh.idh.autodone.config.SecurityConfig.OAUTH_AUTHORIZE;
import static org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter.DEFAULT_LOGIN_PAGE_URL;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.uoc.dh.idh.autodone.services.ServerService;

@Controller()
@RequestMapping(DEFAULT_LOGIN_PAGE_URL)
public class ServerController {

	@Autowired()
	private ServerService serverService;

	//

	@GetMapping()
	public String get(Model model) {
		var servers = serverService.getAll();

		model.addAttribute("servers", servers);
		return "entities/server";
	}

	//

	@PostMapping()
	public String post(Model model, @RequestParam() String domain) {
		var server = serverService.getOne(domain, true);
		var uri = fromUriString(OAUTH_AUTHORIZE).buildAndExpand(domain);

		model.addAttribute("server", server);
		model.addAttribute("uri", uri);
		return "entity/server";
	}

}
