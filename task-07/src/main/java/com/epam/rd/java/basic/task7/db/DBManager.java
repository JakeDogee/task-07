package com.epam.rd.java.basic.task7.db;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

import com.epam.rd.java.basic.task7.db.entity.*;


public class DBManager {

	private static DBManager instance;

	private static final String INSERT_USER = "INSERT INTO users VALUES (DEFAULT, ?)";
	private static final String INSERT_TEAM = "INSERT INTO teams VALUES (DEFAULT, ?)";

	private static final String SELECT_ALL_USERS = "SELECT * FROM users";
	private static final String SELECT_ALL_TEAMS = "SELECT * FROM teams";

	private static final String DELETE_USERS = "DELETE FROM users WHERE login = ?";
	private static final String GET_USER = "SELECT * FROM users WHERE login = ?";
	private static final String GET_TEAM ="SELECT * FROM teams WHERE name = ?";

	private static final String GET_USER_TEAMS = "SELECT teams.name from users_teams inner join teams on teams.id = team_id inner join users on users.id = user_id where users.login = ?";

	private static final String DELETE_TEAM = "DELETE from teams where name = ?";
	private static final String UPDATE_TEAM = "UPDATE teams set name = ? where id = ?";


	private static final String WORK_BD = "jdbc:derby:memory:testdb;create=true";

	private static final String MY_BD = "jdbc:mysql://localhost:3306/testdb";
	private static final String MY_NAME = "root";
	private static final String MY_PASSWORD = "Elias130720031567";

	private Connection connector(){
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection connection = null;
		try{
//			connection = DriverManager.getConnection(MY_BD,MY_NAME,MY_PASSWORD);
			connection = DriverManager.getConnection(WORK_BD);
		}catch (Exception e){
			e.printStackTrace();
		}
		return connection;
	}

	public static synchronized DBManager getInstance() {
		if (instance==null)
			instance = new DBManager();
		return instance;
	}

	private DBManager() {}


	public List<User> findAllUsers() throws DBException {

		try (PreparedStatement statement = connector().prepareStatement(SELECT_ALL_USERS)){
			ResultSet result = statement.executeQuery();
			List<User> users = new ArrayList<>();
			while (result.next()) {
				User user = new User();
				user.setId(result.getInt(1));
				user.setLogin(result.getString(2));
				users.add(user);
			}
			return users;
		}catch (Exception e){
			throw new DBException(e.getMessage(),e.getCause());
		}
	}

	public boolean insertUser(User user) throws DBException {
		try(PreparedStatement posted = connector().prepareStatement(INSERT_USER)) {
			posted.setString(1,user.getLogin());
			posted.executeUpdate();
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteUsers(User... users) throws DBException {
		Arrays.stream(users).forEach(x->{
			Connection con = connector();
			try {
				String sql = DELETE_USERS;

				PreparedStatement posted = con.prepareStatement(sql);
				posted.setString(1,x.getLogin());

				posted.executeUpdate();
			}catch (Exception e){
				e.printStackTrace();
			}
		});
		return true;
	}

	public User getUser(String login) throws DBException {
		Connection con = connector();
		try(PreparedStatement statement = con.prepareStatement(GET_USER)) {
			statement.setString(1,login);
			ResultSet result = statement.executeQuery();
			User user = new User();
			while (result.next()) {
				user.setId(result.getInt(1));
				user.setLogin(result.getString(2));
			}
			return user;
		}catch (Exception e){e.printStackTrace();}
		return null;
	}

	public Team getTeam(String name) throws DBException {
		try(PreparedStatement statement = connector().prepareStatement(GET_TEAM)) {
			statement.setString(1,name);
			Team team = new Team();
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				team.setId(result.getInt(1));
				team.setName(result.getString(2));
			}
			return team;
		}catch (Exception e){e.printStackTrace();}

		return null;
	}

	public List<Team> findAllTeams() throws DBException {
		try(PreparedStatement statement = connector().prepareStatement(SELECT_ALL_TEAMS)) {
			ResultSet result = statement.executeQuery();
			List<Team> teams = new ArrayList<>();
			while (result.next()) {
				Team team = new Team();
				team.setId(result.getInt(1));
				team.setName(result.getString(2));
				teams.add(team);
			}
			return teams;
		}catch (Exception e){e.printStackTrace();}

		return null;
	}

	public boolean insertTeam(Team team) throws DBException {
		try(PreparedStatement posted = connector().prepareStatement(INSERT_TEAM);) {
			posted.setString(1,team.getName());
			posted.executeUpdate();
			return true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public boolean setTeamsForUser(User user, Team... teams) throws DBException {

		try(Connection con = connector()) {

			String sql = "INSERT INTO users_teams values";
			for (int i = 0; i < teams.length; i++) {
				if(i > 0)sql = sql + " , ";
				sql =  sql + "(" + getUser(user.getLogin()).getId() + " , " + getTeam(teams[i].getName()).getId() + ")";
			}
			con.setAutoCommit(false);

			try(PreparedStatement posted = con.prepareStatement(sql)) {
				posted.executeUpdate();
			}catch (Exception ex){
				con.commit();
				con.setAutoCommit(true);
				throw ex;
			}
			con.commit();
			con.setAutoCommit(true);

		}catch (Exception e){
			throw new DBException(e.getMessage(), e.getCause());
		}

		return true;
	}

	public List<Team> getUserTeams(User user) throws DBException {
		Connection con = connector();
		try(PreparedStatement statement = connector().prepareStatement(GET_USER_TEAMS);) {
			statement.setString(1,user.getLogin());
			ResultSet result = statement.executeQuery();
			List<Team> teams = new ArrayList<>();
			while (result.next()) {
				teams.add(Team.createTeam(result.getString(1)));
			}
			return teams;
		}catch (Exception e){
			throw new DBException(e.getMessage(), e.getCause());
		}
	}

	public boolean deleteTeam(Team team) throws DBException {
		Connection con = connector();
		try(PreparedStatement posted = connector().prepareStatement(DELETE_TEAM)) {
			posted.setString(1,team.getName());
			posted.executeUpdate();
			return true;
		}catch (Exception e){
			throw new DBException(e.getMessage(), e.getCause());
		}
	}

	public boolean updateTeam(Team team) throws DBException {
		try(PreparedStatement posted = connector().prepareStatement(UPDATE_TEAM)) {
			posted.setString(1,team.getName());
			posted.setInt(2,team.getId());
			posted.executeUpdate();
			return true;
		}catch (Exception e){
			throw new DBException(e.getMessage(), e.getCause());
		}
	}

}
