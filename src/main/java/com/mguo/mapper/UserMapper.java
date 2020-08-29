package com.mguo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.mguo.entity.User;

public interface UserMapper {

	@Select("select id, email, first, last from Users")
	public List<User> getAllUsers(); 
	
	@Select("select id, email, first, last from Users where email=#{email} and password=#{password}")
	public User findUserByEmailAndPassword(String email, String password);

	@Select("select id, email, first, last from Users where email=#{email}")
	public User findUserByEmail(String email);

	@Select("select id, email, first, last from Users where id=#{id}")
	public User findUserByID(Integer id);
	
	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into Users(email, first, last) values (#{email}, #{first}, #{last}) ")
	public int saveUser(User user);

	@Update("UPDATE Users SET email=#{email}, FIRST=#{first}, LAST=#{last} WHERE id=#{id}")
	public void updateUser(User user);
	
	@Delete("DELETE FROM Users WHERE id=#{id}")
	public void deleteUser(Integer id);
	
	@Insert("insert into UserToken(email, token) values (#{email}, #{token}) ")
	public int saveUserToken(String email, String token);

	@Select("select * from Users WHERE email = (SELECT email FROM UserToken WHERE token=#{token})")
	public User findUserByToken(String token);

	@Delete("DELETE FROM UserToken WHERE email=#{email}")
	public void deleteUserToken(String email);
	
}
