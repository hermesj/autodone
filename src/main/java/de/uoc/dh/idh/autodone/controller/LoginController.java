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

import de.uoc.dh.idh.autodone.services.MastodonInstanceService;

@Controller()
@RequestMapping(DEFAULT_LOGIN_PAGE_URL)
public class LoginController {

	@Autowired()
	private MastodonInstanceService instanceService;

	@GetMapping()
	public String get(Model model) {
		model.addAttribute("instances", instanceService.getAll());
		return "login/instance";
	}

	@PostMapping()
	public String post(Model model, @RequestParam() String domain) {
		try {
			model.addAttribute("endpoint", fromUriString(OAUTH_AUTHORIZE).buildAndExpand(domain).toString());
			model.addAttribute("instance", instanceService.getOne(domain));
			return "login/authorize";
		} catch (Exception exception) {
			model.addAttribute("exception", exception.getMessage());
			return get(model);
		}
	}

}
