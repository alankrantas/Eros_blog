package daoImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dao.UserDao;
import data.Manager;
import data.User;

/**
 * UserDao JDBC 實作, 方法請參閱介面
 * 
 * @author Alan
 * 
 */
@Repository("userDao")
public class UserDaoImpl implements UserDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean checkUserLogin(String id, String pw) throws SQLException, Exception {

		String sql = "select count(*) from user where user_id = ? and user_pw = ?";
		int count = this.jdbcTemplate.queryForInt(sql, new Object[] { id, pw });
		return count > 0 ? true : false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<User> searchUsers(String id, String name, String email) throws SQLException, Exception {

		String sql = "select * from user";
		String sql2 = "";
		String param = "";
		ArrayList<User> users = null;

		if (id != null && !id.isEmpty()) {
			param = id;
			sql2 += " locate(?, user_id) > 0";
		} else if (name != null && !name.isEmpty()) {
			param = name;
			sql2 += " locate(?, user_name) > 0";
		} else if (email != null && !email.isEmpty()) {
			param = email;
			sql2 += " locate(?, email) > 0";
		}
		if (!sql2.equals("")) {
			sql = sql + " where" + sql2 + " order by id";
		} else {
			sql += " order by id";
		}

		List rows = this.jdbcTemplate.queryForList(sql, new Object[] { param });
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			users = new ArrayList<User>();
		}
		while (it.hasNext()) {
			Map userMap = (Map) it.next();
			User user = new User();
			user.setId(userMap.get("user_id").toString());
			user.setName(userMap.get("user_name").toString());
			user.setPic(userMap.get("user_pic").toString());
			user.setBlogTitle(userMap.get("blog_title").toString());
			users.add(user);
		}

		return users;
	}

	@Override
	public boolean checkSingleUser(String id) throws SQLException, Exception {

		String sql = "select count(*) from user where user_id = ?";
		int count = this.jdbcTemplate.queryForInt(sql, new Object[] { id });
		return count > 0 ? true : false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public User querySingleUser(String id) throws SQLException, Exception {

		String sql = "select * from user where user_id = ?";
		User user = null;

		List rows = this.jdbcTemplate.queryForList(sql, new Object[] { id });
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Map userMap = (Map) it.next();
			user = new User();
			user.setId(userMap.get("user_id").toString());
			user.setPw(userMap.get("user_pw").toString());
			user.setName(userMap.get("user_name").toString());
			user.setDescription(userMap.get("user_description") != null ? userMap.get("user_description").toString() : "");
			user.setEmail(userMap.get("email") != null ? userMap.get("email").toString() : "");
			user.setPic(userMap.get("user_pic") != null ? userMap.get("user_pic").toString() : "");
			user.setBlogTitle(userMap.get("blog_title").toString());
			user.setBlogPic(userMap.get("blog_pic") != null ? userMap.get("blog_pic").toString() : "");
			user.setCssFile(userMap.get("css_file") != null ? userMap.get("css_file").toString() : "");
			user.setAccountDate((Date) userMap.get("account_date"));
		}

		return user;

	}

	@Override
	@SuppressWarnings("rawtypes")
	public User querySingleUserSimple(String id) throws SQLException, Exception {

		String sql = "select * from user where user_id = ?";
		User user = null;

		List rows = this.jdbcTemplate.queryForList(sql, new Object[] { id });
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Map userMap = (Map) it.next();
			user = new User();
			user.setId(userMap.get("user_id").toString());
			user.setPw(userMap.get("user_pw").toString());
			user.setName(userMap.get("user_name").toString());
			user.setEmail(userMap.get("email") != null ? userMap.get("email").toString() : "");
		}

		return user;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public String queryCss(String id) throws SQLException, Exception {

		String result = "";
		String sql = "select css_file from user where user_id = ?";

		List rows = this.jdbcTemplate.queryForList(sql, new Object[] { id });
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Map userMap = (Map) it.next();
			result = userMap.get("css_file") != null ? userMap.get("css_file").toString() : "";
		}
		return result;
	}

	@Override
	public void addUser(User user) throws SQLException, Exception {

		String sql = "insert into user (user_id, user_pw, user_name, user_description, email, blog_title, user_pic, blog_pic, css_file, account_date) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		this.jdbcTemplate.update(sql, new Object[] { user.getId(), user.getPw(), user.getName(), user.getDescription(), user.getEmail(), user.getBlogTitle(), user.getPic(), user.getBlogPic(), user.getCssFile(), new Date() });
	}

	@Override
	public void updateUser(User user) throws SQLException, Exception {

		String sql = "update user set user_pw = ?, user_name = ?, user_description = ?, email = ?, blog_title = ?, user_pic = ?, blog_pic = ?, css_file = ? where user_id = ?";
		this.jdbcTemplate.update(sql, new Object[] { user.getPw(), user.getName(), user.getDescription(), user.getEmail(), user.getBlogTitle(), user.getPic(), user.getBlogPic(), user.getCssFile(), user.getId() });
	}

	@Override
	public void deleteUserAndContents(String id) throws SQLException, Exception {

		String sql = "delete from content where content_author = ?";
		this.jdbcTemplate.update(sql, new Object[] { id });

		sql = "delete from user where user_id = ?";
		this.jdbcTemplate.update(sql, new Object[] { id });
	}

	@Override
	public void resetUserPassword(String id) throws SQLException, Exception {

		String sql = "update user set user_pw = '31323334' from content where user_id = ?";
		this.jdbcTemplate.update(sql);
	}

	@Override
	public boolean checkIsUserManager(String id) throws SQLException, Exception {

		String sql = "select count(*) from manager where manager_id = ?";
		int count = this.jdbcTemplate.queryForInt(sql, new Object[] { id });
		return count > 0 ? true : false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Manager queryManager() throws SQLException, Exception {

		String sql = "select * from manager";
		Manager manager = null;

		List rows = this.jdbcTemplate.queryForList(sql);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Map userMap = (Map) it.next();
			manager = new Manager();
			manager.setId(userMap.get("manager_id").toString());
			manager.setEmail(userMap.get("email") != null ? userMap.get("email").toString() : "");
			manager.setEmail_pw(userMap.get("email_pw") != null ? userMap.get("email_pw").toString() : "");
			manager.setSmtpServer(userMap.get("smtp_server") != null ? userMap.get("smtp_server").toString() : "");
			manager.setSmtpServer_port(userMap.get("smtp_server_port") != null ? userMap.get("smtp_server_port").toString() : "");
		}

		return manager;
	}

	@Override
	public void updateManager(Manager manager) throws SQLException, Exception {

		String sql = "update manager set email = ?, email_pw = ?, smtp_server = ?, smtp_server_port = ? where manager_id = ?";
		this.jdbcTemplate.update(sql, new Object[] { manager.getEmail(), manager.getEmail_pw(), manager.getSmtpServer(), manager.getSmtpServer_port(), manager.getId() });
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
