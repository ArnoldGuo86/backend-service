package com.mguo.config;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mguo.interceptor.LoginInterceptor;
import com.mguo.mapper.UserMapper;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

	@Autowired
	private UserMapper userMapper;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("doc");
		registry.addViewController("/doc").setViewName("doc");
		registry.addViewController("/index.html").setViewName("login");
		registry.addViewController("/addUser").setViewName("add");
	}

	@Bean
	public LocaleResolver localeResolver() {
		return new BackendLocaleResolver();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor(userMapper)).addPathPatterns("/**").excludePathPatterns(
				"/index.html", "/", "/user/login", "/webjars/**", "/asserts/**", "/heartbeat", "/swagger-ui.html",
				"/swagger-ui/**","/v3/api-docs/**");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/asserts/**").addResourceLocations("classpath:/static/asserts/");
	}
}

class BackendLocaleResolver implements LocaleResolver {

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		String lang = request.getParameter("l");
		Locale locale = Locale.getDefault();
		if (!StringUtils.isEmpty(lang)) {
			String[] langArr = lang.split("_");
			locale = new Locale(langArr[0], langArr[1]);
		}
		return locale;
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		// TODO Auto-generated method stub

	}

}