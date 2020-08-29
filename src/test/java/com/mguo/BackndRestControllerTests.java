package com.mguo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.mguo.controller.BackendController;
import com.mguo.entity.CommonResult;
import com.mguo.entity.User;
import com.mguo.service.BackendService;
import com.mguo.service.TokenService;

import cn.hutool.json.JSONObject;

@SpringBootTest
class BackndRestControllerTests {

	@Autowired
	private Environment env;

	@Autowired
	private BackendController backendController;

	@Autowired
	private TokenService tokenService;

	@Test
	void testUserLogin_200() throws IOException {
		BackendService mockBackendService = PowerMockito.mock(BackendService.class);

		String mockToken = "mocktoken";
		String mockEmail = "mockemail@email.com";
		String mockPassword = "mockPassword";

		// mock login successfully
		PowerMockito.doReturn(new CommonResult<User>(200, mockToken, new User())).when(mockBackendService)
				.login(mockEmail, mockPassword);
		Whitebox.setInternalState(backendController, "backendService", mockBackendService);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpSession session = PowerMockito.mock(HttpSession.class);
		PowerMockito.doNothing().when(session).setAttribute(Mockito.anyString(), Mockito.any(Object.class));

		HttpServletResponse response = PowerMockito.mock(HttpServletResponse.class);
		List<Cookie> cookies = new ArrayList<Cookie>();
		PowerMockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {

				Cookie cookie = (Cookie) invocation.getArgument(0);
				cookies.add(cookie);
				return null;
			}

		}).when(response).addCookie(Mockito.any(Cookie.class));

		backendController.userLogin(mockEmail, mockPassword, session, response);
		assertEquals(cookies.get(0).getValue(), mockToken);
	}

	@Test
	void testUserLogin_400() throws IOException {
		BackendService mockBackendService = PowerMockito.mock(BackendService.class);

		String mockToken = "mocktoken";
		String mockEmail = "mockemail@email.com";
		String mockPassword = "mockPassword";

		// mock login successfully
		PowerMockito.doReturn(new CommonResult<User>(400, mockToken, null)).when(mockBackendService).login(mockEmail,
				mockPassword);
		Whitebox.setInternalState(backendController, "backendService", mockBackendService);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpSession session = PowerMockito.mock(HttpSession.class);
		PowerMockito.doNothing().when(session).setAttribute(Mockito.anyString(), Mockito.any(Object.class));

		HttpServletResponse response = PowerMockito.mock(HttpServletResponse.class);
		List<Cookie> cookies = new ArrayList<Cookie>();
		PowerMockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {

				Cookie cookie = (Cookie) invocation.getArgument(0);
				cookies.add(cookie);
				return null;
			}

		}).when(response).addCookie(Mockito.any(Cookie.class));

		backendController.userLogin(mockEmail, mockPassword, session, response);
		assertEquals(cookies.size(), 0);
	}

//
	@Test
	public void testHeartBeat() {
		String property = env.getProperty("project.version");
		String json = backendController.getHeartbeat();

		JSONObject jsonObj = new JSONObject(json);
		String version = jsonObj.getStr("version");

		assertEquals(property, version);
	}

//
	@Test
	public void testCreateUser_200() {
		User user = new User();
		String mockEmail = "mockemail@email.com";
		Integer mockId = 999;
		String mockFirst = "mockFirst";
		String mockLast = "mockLast";

		user.setId(mockId);
		user.setEmail(mockEmail);
		user.setFirst(mockFirst);
		user.setLast(mockLast);

		BackendService mockBackendService = PowerMockito.mock(BackendService.class);
		PowerMockito.doReturn(new CommonResult<Integer>(200, "", mockId)).when(mockBackendService).saveUser(user);
		Whitebox.setInternalState(backendController, "backendService", mockBackendService);

		ModelAndView mav = backendController.createUser(user);
		ModelMap modelMap = mav.getModelMap();

		assertEquals(mockId, modelMap.get("id"));
		assertEquals(mockEmail, modelMap.get("email"));
		assertEquals(mockFirst, modelMap.get("first"));
		assertEquals(mockLast, modelMap.get("last"));
	}

}
