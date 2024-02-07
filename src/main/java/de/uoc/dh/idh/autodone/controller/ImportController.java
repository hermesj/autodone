package de.uoc.dh.idh.autodone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller()
@RequestMapping("/import")
public class ImportController {

	@PostMapping()
	public String post(@RequestBody() MultipartFile file) {
		throw new UnsupportedOperationException("Not implemented!");
	}

}
