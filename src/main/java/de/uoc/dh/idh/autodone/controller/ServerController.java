package de.uoc.dh.idh.autodone.controller;

import static de.uoc.dh.idh.autodone.config.SecurityConfig.OAUTH_AUTHORIZE;
import static de.uoc.dh.idh.autodone.utils.WebUtils.localHref;
import static java.time.ZoneOffset.ofTotalSeconds;
import static org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter.DEFAULT_LOGIN_PAGE_URL;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.uoc.dh.idh.autodone.services.ServerService;
import jakarta.servlet.http.HttpSession;

@Controller()
@RequestMapping(DEFAULT_LOGIN_PAGE_URL)
public class ServerController {

	@Autowired()
	private HttpSession httpSession;

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
	public String post(Model model, @RequestParam() Map<String, String> params) {
		var server = serverService.getOne(params.get("domain"), true);
		var uri = localHref(OAUTH_AUTHORIZE).toString(params.get("domain"));

		httpSession.setAttribute("zoneOffset", ofTotalSeconds(Integer.parseInt(params.get("offset")) * 60));

		model.addAttribute("server", server);
		model.addAttribute("uri", uri);
		return "entity/server";
	}

}
