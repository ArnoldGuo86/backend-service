package com.mguo.mapper.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mguo.entity.User;
import com.mguo.mapper.UserMapper;

/*remove this when connect to real DB*/
//@Repository
public class UserMapperImpl implements UserMapper {

	private static Map<Integer, User> mockUsers = new HashMap<Integer, User>();

	private static Map<String, String> mockUserToken = new HashMap<String, String>();

	static {
		User user = new User();
		user.setId(1);
		user.setEmail("1001@email.com");
		user.setPassword("1001");
		user.setFirst("1001F");
		user.setLast("1001L");

		mockUsers.put(user.getId(), user);

		user = new User();
		user.setId(2);
		user.setEmail("1002@email.com");
		user.setPassword("1002");
		user.setFirst("1002F");
		user.setLast("1002L");

		mockUsers.put(user.getId(), user);
	}

	@Override
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		users.addAll(mockUsers.values());
		return users;
	}

	@Override
	public User findUserByEmailAndPassword(String email, String password) {

		for (User user : mockUsers.values()) {
			if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public User findUserByEmail(String email) {
		for (User user : mockUsers.values()) {
			if (user.getEmail().equals(email)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public User findUserByID(Integer id) {
		return mockUsers.get(id);
	}

	@Override
	public int saveUser(User user) {
		int size = mockUsers.size();
		user.setId(size + 1);
		mockUsers.put(user.getId(), user);
		return 0;
	}

	@Override
	public void updateUser(User user) {
		mockUsers.put(user.getId(), user);

	}

	@Override
	public void deleteUser(Integer id) {
		mockUsers.remove(id);
	}

	@Override
	public int saveUserToken(String email, String token) {
		mockUserToken.put(email, token);
		return 0;
	}

	@Override
	public User findUserByToken(String token) {
		String emailFind = null;

		for (String email : mockUserToken.keySet()) {
			String savedToken = mockUserToken.get(email);
			if (savedToken.equals(token)) {
				emailFind = email;
			}
		}
		return findUserByEmail(emailFind);
	}

	@Override
	public void deleteUserToken(String email) {
		// TODO Auto-generated method stub

	}

}
