package com.mguo.service.impl;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mguo.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

	@Override
	public String getToken(String email, String password) {

		String token = JWT.create().withAudience(email).sign(Algorithm.HMAC256(password));

		return token;
	}
}
