package com.mguo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mguo.entity.User;
import com.mguo.mapper.UserMapper;
import com.mguo.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public String getToken(String email, String password) {

		String token = JWT.create().withAudience(email).sign(Algorithm.HMAC256(password));

		return token;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveUserTx(User user) {
		userMapper.saveUser(user);
	}
}
