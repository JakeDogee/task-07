package com.epam.rd.java.basic.task7.db.entity;

public class User {

	private int id;

	private String login;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public static User createUser(String login) {
		User newOne = new User();
		newOne.setId(0);
		newOne.setLogin(login);
		return newOne;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User user = (User) o;

		return login != null ? login.equals(user.login) : user.login == null;
	}

	@Override
	public int hashCode() {
		return login != null ? login.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", login='" + login + '\'' +
				'}';
	}
}
