package de.uoc.dh.idh.autodone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/about")
public class AboutController {

	@GetMapping()
	public String get() {
		return "page/about";
	}

}
