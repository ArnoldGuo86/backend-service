package com.mguo.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.mguo.entity.User;
import com.mguo.mapper.UserMapper;

public class LoginInterceptor implements HandlerInterceptor {

	private UserMapper userMapper;

	public LoginInterceptor(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean result = false;

		if (request.getSession().getAttribute("loginUser") == null) {
			Cookie[] cookies = request.getCookies();

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("token".equals(cookie.getName())) {
						String token = cookie.getValue();
						User user = userMapper.findUserByToken(token);
						if (user != null) {
							request.getSession().setAttribute("loginUser", user.getEmail());
							result = true;
							break;
						}

					}

				}
			}

		} else {
			result = true;
		}

		if (!result) {
			request.setAttribute("msg", "Not authorized, please login");
			request.getRequestDispatcher("/index.html").forward(request, response);
		}

		return result;
	}
}
