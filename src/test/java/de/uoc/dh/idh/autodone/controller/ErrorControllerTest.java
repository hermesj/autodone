package de.uoc.dh.idh.autodone.controller;

import static jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;

/**
 * Since Spring Boot 3.3 status, error and path are only part of the error
 * attributes when explicitly included; the error page needs status and error.
 */
class ErrorControllerTest {

	@Test()
	void exposesStatusAndError() {
		var controller = new ErrorController();
		ReflectionTestUtils.setField(controller, "errorAttributes", new DefaultErrorAttributes());

		var model = new ExtendedModelMap();
		var request = new MockHttpServletRequest();
		var response = new MockHttpServletResponse();
		request.setAttribute(ERROR_STATUS_CODE, 404);

		assertEquals("elements/error", controller.http(model, request, response));
		assertEquals(404, response.getStatus());
		assertEquals(404, model.getAttribute("status"));
		assertEquals("Not Found", model.getAttribute("error"));
	}

}
