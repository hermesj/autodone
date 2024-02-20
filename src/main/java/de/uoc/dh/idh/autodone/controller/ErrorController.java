package de.uoc.dh.idh.autodone.controller;

import static org.springframework.boot.web.error.ErrorAttributeOptions.of;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.EXCEPTION;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller()
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

	@Autowired()
	ErrorAttributes errorAttributes;

	//

	@RequestMapping()
	public String http(Model model, HttpServletRequest servletRequest, HttpServletResponse response) {
		var options = of(EXCEPTION, MESSAGE);
		var request = new ServletWebRequest(servletRequest);

		model.addAllAttributes(errorAttributes.getErrorAttributes(request, options));
		response.setStatus((int) model.getAttribute("status"));
		return "elements/error";
	}

}
