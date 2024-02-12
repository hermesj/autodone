package de.uoc.dh.idh.autodone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import de.uoc.dh.idh.autodone.services.ImportService;

@Controller()
@RequestMapping("/import")
public class ImportController {

	@Autowired()
	private ImportService importService;

	@PostMapping(produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String post(@RequestBody() MultipartFile file) throws Exception {
		return importService.parse(file.getInputStream()).toString();
	}

}
