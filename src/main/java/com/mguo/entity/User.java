package com.mguo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class User {
	private Integer id;

	private String first;

	private String last;

	private String email;

	private String password;

}
