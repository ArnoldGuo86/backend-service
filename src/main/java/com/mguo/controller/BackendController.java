package com.mguo.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.mguo.entity.CommonResult;
import com.mguo.entity.User;
import com.mguo.service.BackendService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BackendController {

	@Autowired
	private BackendService backendService;

	@PostMapping("/user/login")
	public ModelAndView userLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpSession session, HttpServletResponse response) throws IOException {

		CommonResult<User> result = backendService.login(email, password);
		ModelAndView mav = new ModelAndView();

		if (result.getCode() != 200) {
			// login failed

			mav.addObject("msg", "Invalid email or password");
			mav.setViewName("login");
		} else {
			session.setAttribute("loginUser", result.getData().getEmail());
			String token = result.getMessage();
			Cookie cookie = new Cookie("token", token);
			cookie.setPath("/");
			cookie.setMaxAge(60 * 60 * 24 * 1);

			response.addCookie(cookie);

			mav.setViewName("redirect:/success");
		}
		return mav;

	}

	@GetMapping("/success")
	public ModelAndView toSuccess() {
		CommonResult<List<User>> allUserResult = backendService.getAllUsers();
		List<User> allUsers = allUserResult.getData();

		ModelAndView mav = new ModelAndView();
		mav.addObject("users", allUsers);

		mav.setViewName("success");

		return mav;
	}

	@GetMapping("/heartbeat")
	public String getHeartbeat() {
		CommonResult<String> result = backendService.getVersion();
		return result.getData();
	}

	@PostMapping("/user/logout")
	public ModelAndView logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		String email = (String) session.getAttribute("loginUser");
		backendService.logout(email);

		Cookie cookie = new Cookie("token", "");
		cookie.setPath("/");
		cookie.setMaxAge(0);

		response.addCookie(cookie);

		session.removeAttribute("loginUser");

		ModelAndView mav = new ModelAndView("login");
		return mav;
	}

	@PostMapping("/user")
	public ModelAndView createUser(User user) {
		CommonResult<Integer> result = backendService.saveUser(user);
		log.info("User id: " + result.getData() + " created!!");

		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/success");

		mav.addObject("id", user.getId());
		mav.addObject("email", user.getEmail());
		mav.addObject("first", user.getFirst());
		mav.addObject("last", user.getLast());

		return mav;
	}

	@GetMapping("/user/id/{id}")
	public ModelAndView editUser(@PathVariable("id") Integer id) {
		CommonResult<User> result = backendService.findUserByID(id);
		ModelAndView mav = new ModelAndView("add");
		if (result.getCode() == 200) {
			User user = result.getData();
			mav.addObject("user", user);
		}
		return mav;
	}

	@PutMapping("/user")
	public ModelAndView updateUser(User user) {
		backendService.updateUser(user);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/success");
		return mav;
	}

	@DeleteMapping("/user/{id}")
	public ModelAndView deleteUser(@PathVariable("id") Integer id) {
		backendService.deleteUser(id);

		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/success");
		return mav;
	}

	@GetMapping("/user/{token}")
	public User getUserByToken(@PathVariable("token") String token) {
		CommonResult<User> result = backendService.findUserByToken(token);

		User user = null;

		if (result.getCode() == 200) {
			user = result.getData();
		}

		return user;
	}
	
	
	@GetMapping("/test")
	public void test() {
		backendService.test();
	}
}
