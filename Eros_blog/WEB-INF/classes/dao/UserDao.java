package dao;

import java.sql.SQLException;
import java.util.List;

import data.Manager;
import data.User;

/**
 * Interface for UserDaoImpl
 * 
 * @author Alan
 * 
 */
public interface UserDao {

	public static final String DEFAULT_CSS_FILE = "eros_default.css";// 預設 CSS

	/**
	 * 檢查使用者登入
	 * 
	 * @param id
	 * @param pw
	 *            必須轉成 hex 格式
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean checkUserLogin(String id, String pw) throws SQLException, Exception;

	/**
	 * 檢查使用者是否存在
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean checkSingleUser(String id) throws SQLException, Exception;

	/**
	 * 搜尋使用者
	 * 
	 * @param id
	 * @param name
	 * @param email
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public List<User> searchUsers(String id, String name, String email) throws SQLException, Exception;

	/**
	 * 查詢單一使用者 (詳細)
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public User querySingleUser(String id) throws SQLException, Exception;

	/**
	 * 查詢單一使用者 (概要)
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public User querySingleUserSimple(String id) throws SQLException, Exception;

	/**
	 * 查詢 CSS 主題
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String queryCss(String id) throws SQLException, Exception;

	/**
	 * 新增使用者
	 * 
	 * @param user
	 * @throws SQLException
	 * @throws Exception
	 */
	public void addUser(User user) throws SQLException, Exception;

	/**
	 * 更新使用者
	 * 
	 * @param user
	 * @throws SQLException
	 * @throws Exception
	 */
	public void updateUser(User user) throws SQLException, Exception;

	/**
	 * 刪除使用者和其發表的所有文章
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws Exception
	 */
	public void deleteUserAndContents(String id) throws SQLException, Exception;

	/**
	 * 重設使用者密碼
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws Exception
	 */
	public void resetUserPassword(String id) throws SQLException, Exception;

	/**
	 * 檢查使用者是否為管理者
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean checkIsUserManager(String id) throws SQLException, Exception;

	/**
	 * 查詢管理者
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Manager queryManager() throws SQLException, Exception;

	/**
	 * 更新管理者
	 * 
	 * @param manager
	 * @throws SQLException
	 * @throws Exception
	 */
	public void updateManager(Manager manager) throws SQLException, Exception;

}
