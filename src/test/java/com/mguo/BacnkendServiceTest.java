package com.mguo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.mguo.entity.CommonResult;
import com.mguo.entity.User;
import com.mguo.mapper.UserMapper;
import com.mguo.service.BackendService;

import cn.hutool.json.JSONObject;

@SpringBootTest
public class BacnkendServiceTest {

	@Autowired
	BackendService backendService;

	@Autowired
	private Environment env;

	public User getMockUser() {
		User user = new User();
		String mockEmail = "mockemail@email.com";
		Integer mockId = 999;
		String mockFirst = "mockFirst";
		String mockLast = "mockLast";

		user.setId(mockId);
		user.setEmail(mockEmail);
		user.setFirst(mockFirst);
		user.setLast(mockLast);

		return user;
	}

	@Test
	public void testLogin_200() {
		String mockEmail = "mockemail@email.com";
		String mockPassword = "mockPassword";

		UserMapper userMapper = PowerMockito.mock(UserMapper.class);
		PowerMockito.doReturn(getMockUser()).when(userMapper).findUserByEmailAndPassword(mockEmail, mockPassword);
		PowerMockito.doNothing().when(userMapper).deleteUserToken(Mockito.anyString());
		PowerMockito.doReturn(-1).when(userMapper).saveUserToken(Mockito.anyString(), Mockito.anyString());

		Whitebox.setInternalState(backendService, "userMapper", userMapper);

		CommonResult<User> loginResult = backendService.login(mockEmail, mockPassword);
		assertEquals(200, loginResult.getCode());
	}

	@Test
	public void testLogin_400() {

		String mockEmail = "mockemail@email.com";
		String mockPassword = "mockPassword";

		UserMapper userMapper = PowerMockito.mock(UserMapper.class);
		PowerMockito.doReturn(null).when(userMapper).findUserByEmailAndPassword(mockEmail, mockPassword);
		PowerMockito.doNothing().when(userMapper).deleteUserToken(Mockito.anyString());
		PowerMockito.doReturn(-1).when(userMapper).saveUserToken(Mockito.anyString(), Mockito.anyString());

		Whitebox.setInternalState(backendService, "userMapper", userMapper);

		CommonResult<User> loginResult = backendService.login(mockEmail, mockPassword);
		assertEquals(400, loginResult.getCode());
	}

	@Test
	public void testFindUserByID_200() {

		Integer mockId = 999;

		UserMapper userMapper = PowerMockito.mock(UserMapper.class);
		PowerMockito.doReturn(getMockUser()).when(userMapper).findUserByID(mockId);

		Whitebox.setInternalState(backendService, "userMapper", userMapper);

		CommonResult<User> result = backendService.findUserByID(mockId);

		assertEquals(200, result.getCode());
	}

	@Test
	public void testFindUserByID_400() {

		Integer mockId = 999;

		UserMapper userMapper = PowerMockito.mock(UserMapper.class);
		PowerMockito.doReturn(null).when(userMapper).findUserByID(mockId);

		Whitebox.setInternalState(backendService, "userMapper", userMapper);

		CommonResult<User> result = backendService.findUserByID(mockId);

		assertEquals(400, result.getCode());
	}

	@Test
	public void testFindUserByToken_200() {

		String mockToken = "mockToken";
		UserMapper userMapper = PowerMockito.mock(UserMapper.class);
		PowerMockito.doReturn(getMockUser()).when(userMapper).findUserByToken(mockToken);
		Whitebox.setInternalState(backendService, "userMapper", userMapper);

		CommonResult<User> result = backendService.findUserByToken(mockToken);
		assertEquals(200, result.getCode());
	}

	@Test
	public void testFindUserByToken_400() {

		String mockToken = "mockToken";
		UserMapper userMapper = PowerMockito.mock(UserMapper.class);
		PowerMockito.doReturn(null).when(userMapper).findUserByToken(mockToken);
		Whitebox.setInternalState(backendService, "userMapper", userMapper);

		CommonResult<User> result = backendService.findUserByToken(mockToken);
		assertEquals(400, result.getCode());
	}

	@Test
	public void testGetVersion() {
		String property = env.getProperty("project.version");

		CommonResult<String> result = backendService.getVersion();

		JSONObject obj = new JSONObject(result.getData());
		assertEquals(property, obj.get("version"));
	}

	@Test
	public void testSaveUser_200() {
		UserMapper userMapper = PowerMockito.mock(UserMapper.class);
		User mockUser = getMockUser();
		PowerMockito.doReturn(1).when(userMapper).saveUser(mockUser);
		Whitebox.setInternalState(backendService, "userMapper", userMapper);

		CommonResult<Integer> result = backendService.saveUser(mockUser);
		assertEquals(200, result.getCode());
	}

	@Test
	public void testSaveUser_500() {
		UserMapper userMapper = PowerMockito.mock(UserMapper.class);
		User mockUser = getMockUser();
		PowerMockito.doThrow(new RuntimeException()).when(userMapper).saveUser(mockUser);
		Whitebox.setInternalState(backendService, "userMapper", userMapper);

		CommonResult<Integer> result = backendService.saveUser(mockUser);
		assertEquals(500, result.getCode());
	}
}
