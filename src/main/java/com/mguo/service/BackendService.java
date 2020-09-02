package com.mguo.service;

import java.util.List;

import com.mguo.entity.CommonResult;
import com.mguo.entity.User;

public interface BackendService {

	public CommonResult<User> login(String email, String password);

	public CommonResult<User> findUserByToken(String token);

	public CommonResult<User> findUserByID(Integer id);

	public CommonResult<String> getVersion();

	public CommonResult<Void> logout(String email);

	public CommonResult<Integer> saveUser(User user);

	public CommonResult<List<User>> getAllUsers();

	public CommonResult<Void> updateUser(User user);

	public CommonResult<Void> deleteUser(Integer id);
	
	public void test();
	
	public void saveUserTx(User user);
}
