package de.uoc.dh.idh.autodone.controller;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.mock.env.MockEnvironment;
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
		var model = render(new MockEnvironment(), 404, null);

		assertEquals(404, model.getAttribute("status"));
		assertEquals("Not Found", model.getAttribute("error"));
	}

	@Test()
	void hidesExceptionDetailsOutsideTestProfile() {
		var model = render(new MockEnvironment(), 500, new IllegalStateException("jdbc:postgresql://db/secret"));

		assertEquals(500, model.getAttribute("status"));
		assertFalse(model.containsAttribute("exception"));
		assertFalse(model.containsAttribute("message"));
	}

	@Test()
	void showsExceptionDetailsInTestProfile() {
		var environment = new MockEnvironment();
		environment.setActiveProfiles("test");

		var model = render(environment, 500, new IllegalStateException("details"));

		assertEquals(IllegalStateException.class.getName(), model.getAttribute("exception"));
		assertEquals("details", model.getAttribute("message"));
	}

	//

	private static ExtendedModelMap render(MockEnvironment environment, int status, Exception exception) {
		var controller = new ErrorController();
		ReflectionTestUtils.setField(controller, "environment", environment);
		ReflectionTestUtils.setField(controller, "errorAttributes", new DefaultErrorAttributes());

		var model = new ExtendedModelMap();
		var request = new MockHttpServletRequest();
		var response = new MockHttpServletResponse();
		request.setAttribute(ERROR_STATUS_CODE, status);
		request.setAttribute(ERROR_EXCEPTION, exception);

		assertEquals("elements/error", controller.http(model, request, response));
		assertEquals(status, response.getStatus());
		return model;
	}

}
