package com.mguo.service;

import com.mguo.entity.User;

public interface TokenService {
	public String getToken(String email, String password);
	
	public void saveUserTx(User user);
}
