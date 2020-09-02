package com.mguo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

	/**
	 * Rollback is based on AOP proxy instance: Scenario 1:
	 * 
	 * 1. this.save1(requires_new)
	 * 
	 * 2. this.save2(required)
	 * 
	 * 3. 10/0(ArithmeticException throws)
	 * 
	 * step1 will roollback due to they are using same cglib instance
	 * 
	 * 
	 * 1. anothService.save1(requires_new)
	 * 
	 * 2. this.save2(required)
	 * 
	 * 3. 10/0(ArithmeticException throws)
	 * 
	 * step1 won't roollback
	 * 
	 * Scenario 2:
	 * 
	 * 1. this.save1(requires_new)
	 * 
	 * 2. this.save2(required)
	 * 
	 * 3. 10/0(ArithmeticException throws)
	 * 
	 * step1 won't roollback
	 * 
	 * Scenario 3:
	 * 
	 * a. Adding spring-boot-starter-aop model in pom
	 * b. Adding @EnableAspectJAutoProxy(exposeProxy = true) annotation to main config class
	 * c. Cast AopContext.currentProxy() to current service
	 * 
	 * 1. service.save1(requires_new)
	 * 
	 * 2. this.save2(required)
	 * 
	 * 3. 10/0(ArithmeticException throws)
	 * 
	 * step1 won't roollback
	 *
	 */
	@Transactional
	public void test() {
		User user = new User();
		user.setEmail("999@email.com");
		user.setFirst("999f");
		user.setLast("999l");

		BackendServiceImpl currentProxy = (BackendServiceImpl) AopContext.currentProxy();

		currentProxy.saveUserTx(user);
		currentProxy.saveTokenTx(user.getEmail(), "999999999999999");
		int i = 10 / 0;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveUserTx(User user) {
		userMapper.saveUser(user);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveTokenTx(String email, String token) {
		userMapper.saveUserToken(email, token);
	}
}
