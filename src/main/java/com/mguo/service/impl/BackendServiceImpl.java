package com.mguo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mguo.entity.CommonResult;
import com.mguo.entity.User;
import com.mguo.mapper.UserMapper;
import com.mguo.service.BackendService;
import com.mguo.service.TokenService;

import cn.hutool.json.JSONObject;

@Service
public class BackendServiceImpl implements BackendService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private TokenService tokenService;

	@Value("${project.version}")
	private String version;

	@Override
	public CommonResult<User> login(String email, String password) {

		User user = userMapper.findUserByEmailAndPassword(email, password);
		CommonResult<User> result;
		if (user == null) {
			result = new CommonResult<>(400, "Login failed", null);
		} else {
			String token = tokenService.getToken(email, password);
			result = new CommonResult<User>(200, token, user);
			userMapper.deleteUserToken(email);
			userMapper.saveUserToken(email, token);
		}
		return result;
	}

	@Override
	public CommonResult<User> findUserByID(Integer id) {
		User user = userMapper.findUserByID(id);
		if (user != null) {
			return new CommonResult<User>(200, "", user);
		} else {
			return new CommonResult<User>(400, "", null);
		}
	}

	@Override
	public CommonResult<User> findUserByToken(String token) {
		User user = userMapper.findUserByToken(token);

		CommonResult<User> result;
		if (user == null) {
			result = new CommonResult<>(400, "Invalid token", null);
		} else {
			result = new CommonResult<User>(200, token, user);
		}
		return result;

	}

	@Override
	public CommonResult<String> getVersion() {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date date = new Date();
		JSONObject obj = new JSONObject().set("version", version).set("releasedAt", format.format(date));

		return new CommonResult<String>(200, "", obj.toStringPretty());
	}

	@Override
	public CommonResult<Void> logout(String email) {
		userMapper.deleteUserToken(email);

		return new CommonResult<>(200, "");
	}

	@Override
	public CommonResult<Integer> saveUser(User user) {
		Integer id = -1;
		try {
			id = userMapper.saveUser(user);
			return new CommonResult<>(200, "", id);
		} catch (Exception e) {
			return new CommonResult<>(500, "create user faild", id);
		}
	}

	@Override
	public CommonResult<List<User>> getAllUsers() {
		List<User> allUsers = userMapper.getAllUsers();
		return new CommonResult<List<User>>(200, "", allUsers);
	}

	@Override
	public CommonResult<Void> updateUser(User user) {
		userMapper.updateUser(user);
		return new CommonResult<>(200, "");
	}

	@Override
	public CommonResult<Void> deleteUser(Integer id) {
		userMapper.deleteUser(id);
		return new CommonResult<>(200, "");
	}
}
