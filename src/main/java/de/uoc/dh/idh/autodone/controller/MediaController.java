package de.uoc.dh.idh.autodone.controller;

import static de.uoc.dh.idh.autodone.utils.ImageUtils.shrink;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.FORCE;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.mapFields;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.pick;
import static de.uoc.dh.idh.autodone.utils.WebUtils.href;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.services.MediaService;
import de.uoc.dh.idh.autodone.services.StatusService;
import de.uoc.dh.idh.autodone.utils.ImageUtils.Image;

@Controller()
@RequestMapping("/media")
public class MediaController {

	@Autowired()
	private MediaService mediaService;

	@Autowired()
	private StatusService statusService;

	//

	@DeleteMapping()
	public String delete(@RequestParam() Map<String, String> params) {
		mediaService.delete(params.get("uuid"));
		return "redirect:" + href();
	}

	//

	@GetMapping()
	public String get(Model model, @RequestParam() Map<String, String> params) {
		if (params.containsKey("uuid")) {
			var media = mediaService.getOne(params.get("uuid"));

			model.addAttribute("media", media);
			return "entity/media";
		} else {
			var media = new MediaEntity();
			var status = statusService.getOwn();
			var page = mediaService.getPage(params.get("page"), params.get("sort"));

			model.addAttribute("media", media);
			model.addAttribute("status", status);
			model.addAttribute("page", page);
			return "entities/media";
		}
	}

	//

	@PostMapping()
	public String post(@RequestBody() MultipartFile file, @RequestParam() Map<String, Object> form) throws Exception {
		var media = new MediaEntity();
		var fields = pick(form, "description");

		if (form.containsKey("uuid")) {
			media = mediaService.getOne((String) form.get("uuid"));
		} else {
			var image = (Image) null;

			try {
				// stored until publishing, so scale it down like downloaded images
				image = shrink(file.getContentType(), file.getBytes());
			} catch (IllegalArgumentException | IOException | NullPointerException exception) {
				throw new ResponseStatusException(BAD_REQUEST, "Not a usable image", exception);
			}

			fields.put("status", statusService.getOne((String) form.get("status.uuid")));
			fields.put("contentType", image.contentType());
			fields.put("file", image.file());
		}

		var save = mediaService.save(mapFields(fields, media, FORCE));
		return "redirect:/media?uuid=" + save.uuid;
	}

}
