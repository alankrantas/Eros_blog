package bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import util.StringHex;
import dao.UserDao;
import data.User;

/**
 * Managedbean for index.xhtml, etc
 * 
 * @author Alan
 * 
 */
@Component(value = "loginBean")
@Scope("session")
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 7587858765740402207L;

	private boolean hadLogin;// 登入狀態
	private User loginUser;// 登入的使用者
	private boolean switchLoginPanel;// 登入區顯示狀態
	private String originalUrl;// 保留登入登出轉頁的網址(由其他網頁寫入)
	private String timeZoneData;// 時區 (給網頁的格式轉換器用)

	private FacesContext facesContext;// facesContext
	private Logger logger = Logger.getLogger(this.getClass().getName());// logger
	private String cssFileName;// CSS 主題檔案名稱

	@Autowired
	private UserDao userDao;// dao

	// 物件初始化
	public LoginBean() {

		this.logger.info("Eros blog 系統已啟動");

		try {

			// 取得系統時區
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			this.timeZoneData = calendar.getTimeZone().getID();
			this.logger.info("系統時區: " + this.timeZoneData);

		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

	}

	// 初始化
	public String getInit() {

		try {

			// 避免重覆載入
			if (facesContext == null) {

				this.facesContext = FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) this.facesContext.getExternalContext().getSession(false);
				this.loginUser = new User();
				this.switchLoginPanel = false;
				this.cssFileName = UserDao.DEFAULT_CSS_FILE;

				// 檢查登入狀態
				if (session.getAttribute("userId") == null || session.getAttribute("userId").toString().isEmpty()) {
					this.hadLogin = false;
				} else {
					this.hadLogin = true;
					this.loginUser.setId(session.getAttribute("userId").toString());
					this.cssFileName = this.userDao.queryCss(this.loginUser.getId());
					if (this.cssFileName == null || this.cssFileName.isEmpty()) {
						this.cssFileName = UserDao.DEFAULT_CSS_FILE;
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

		return this.cssFileName;
	}

	// 登入
	public String login() {

		String resultStr = "";

		try {

			this.facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) this.facesContext.getExternalContext().getSession(false);

			if (this.loginUser.getId() == null || this.loginUser.getId().isEmpty()) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "請輸入使用者名稱"));
			} else if (this.loginUser.getPw() == null || this.loginUser.getPw().isEmpty()) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "請輸入使用者密碼"));
			} else {

				if (this.userDao.checkUserLogin(this.loginUser.getId(), StringHex.convertStringToHex(this.loginUser.getPw()))) {
					// 設定登入
					session.removeAttribute("userId");
					session.setAttribute("userId", this.loginUser.getId());
					this.logger.info("使用者 " + this.loginUser.getId() + " 登入成功");
					this.hadLogin = true;
					this.switchLoginPanel = false;
					// 處理轉頁
					if (this.originalUrl != null && !originalUrl.isEmpty()) {
						resultStr = this.processOriginalUrl();
						this.originalUrl = "";
					} else {
						resultStr = "";
						this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "-- Eros blog --", "登入成功"));
					}
				} else {
					this.logger.info("使用者 " + this.loginUser.getId() + "登入失敗");
					this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "登入失敗; 使用者名稱或密碼錯誤"));
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

		return resultStr;
	}

	// 登出
	public String logout() {

		String resultStr = "";

		try {

			this.facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) this.facesContext.getExternalContext().getSession(false);
			session.removeAttribute("userId");
			this.logger.info("使用者 " + this.loginUser.getId() + " 登出");

			this.hadLogin = false;
			this.loginUser = new User();
			this.switchLoginPanel = false;
			if (this.originalUrl != null && !originalUrl.isEmpty()) {
				resultStr = this.processOriginalUrl();
				this.originalUrl = "";
			} else {
				resultStr = "";
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "-- Eros blog --", "登出成功"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

		return resultStr;
	}

	// 處理轉頁字串
	private String processOriginalUrl() {

		String result = "";

		try {

			this.originalUrl = this.originalUrl.replaceAll("&faces-redirect=true", "");
			result = this.originalUrl + (this.originalUrl.indexOf("?") != -1 ? "&faces-redirect=true" : "?faces-redirect=true");

		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}
		return result;
	}

	public boolean isHadLogin() {
		return hadLogin;
	}

	public void setHadLogin(boolean hadLogin) {
		this.hadLogin = hadLogin;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	public boolean isSwitchLoginPanel() {
		return switchLoginPanel;
	}

	public void setSwitchLoginPanel(boolean switchLoginPanel) {
		this.switchLoginPanel = switchLoginPanel;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getTimeZoneData() {
		return timeZoneData;
	}

	public void setTimeZoneData(String timeZoneData) {
		this.timeZoneData = timeZoneData;
	}

}
