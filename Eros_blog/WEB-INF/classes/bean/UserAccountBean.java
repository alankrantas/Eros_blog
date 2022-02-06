package bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.servlet.http.HttpSession;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import util.SendEmail;
import util.StringHex;
import util.Uploader;
import util.Validator;
import dao.UserDao;
import data.EmailContent;
import data.Manager;
import data.User;

/**
 * Managedbean for user_account.xhtml
 * 
 * @author Alan
 * 
 */
@Component(value = "userAccountBean")
@Scope("viewScoped")
public class UserAccountBean implements Serializable {

	private static final long serialVersionUID = -6514432977078005122L;

	private boolean hadLogin;// 登入狀態
	private String browseUserId;// 登入使用者代號
	private boolean asManager;// 是管理者
	private User user;// 使用者資料
	private Manager manager;// 管理者資料
	private String displayHeader;// 網頁顯示標題
	private String comparePw;// 申請時的對照密碼
	private StreamedContent downloadFile;// 下載 CSS 檔

	@Autowired
	private UserDao userDao;

	FacesContext facesContext;
	private Logger logger = Logger.getLogger(this.getClass().getName());;
	private String cssFileName;// CSS 主題檔案名稱

	// 初始化
	public String getInit() {

		try {

			// 避免重覆載入
			if (this.facesContext == null) {

				this.facesContext = FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) this.facesContext.getExternalContext().getSession(false);
				this.cssFileName = UserDao.DEFAULT_CSS_FILE;

				// 還原可能中斷的登入登出轉頁
				if (session.getAttribute("loginBean") != null) {
					((LoginBean) session.getAttribute("loginBean")).setOriginalUrl("");
				}

				// 檢查登入狀態
				this.manager = null;
				if (session.getAttribute("userId") == null || session.getAttribute("userId").toString().isEmpty()) {
					this.hadLogin = false;
					this.asManager = false;
					this.user = new User();
					this.displayHeader = "申請新使用者";
				} else {

					// 載入使用者
					this.hadLogin = true;
					this.browseUserId = session.getAttribute("userId").toString();
					this.displayHeader = "修改資料";
					this.user = this.userDao.querySingleUser(this.browseUserId);
					this.user.setPw(StringHex.convertHexToString(this.user.getPw()));
					this.user.setDescription(this.user.getDescription().trim().replaceAll("<br/>", "\n"));
					this.cssFileName = this.user.getCssFile();
					if (this.cssFileName == null || this.cssFileName.isEmpty()) {
						this.cssFileName = UserDao.DEFAULT_CSS_FILE;
					}
					// 載入管理者 (若使用者是管理者)
					if (this.userDao.checkIsUserManager(this.browseUserId)) {
						this.manager = this.userDao.queryManager();
						this.manager.setEmail_pw(StringHex.convertHexToString(this.manager.getEmail_pw()));
						this.asManager = true;
					} else {
						this.asManager = false;
					}
					// 設定可下載的 CSS 範本
					File file = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//resources//css//eros_default.css"));
					InputStream stream = new FileInputStream(file);
					this.downloadFile = new DefaultStreamedContent(stream, "text/css", "eros_blog_templete.css");

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

	// 送出新使用者資料
	public String submitUser() {

		String returnStr = "";

		try {

			this.facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) this.facesContext.getExternalContext().getSession(false);

			if (!this.hadLogin && (this.user.getId() == null || this.user.getId().trim().isEmpty())) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "必須輸入帳號"));
			} else if (!this.hadLogin && this.userDao.checkSingleUser(this.user.getId())) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "帳號已有人使用"));
			} else if (!Validator.isValidEngNumString(this.user.getId())) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "帳號必須只能是英數"));
			} else if (this.user.getName() == null || this.user.getName().trim().isEmpty()) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "必須輸入暱稱"));
			} else if (this.user.getPw() == null || this.user.getPw().trim().isEmpty()) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "必須輸入密碼"));
			} else if (!this.hadLogin && !this.user.getPw().equals(this.comparePw)) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "密碼輸入不一致"));
			} else if (!Validator.isValidEngNumString(this.user.getPw())) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "密碼必須只能是英數"));
			} else if (this.user.getEmail() != null && !this.user.getEmail().trim().isEmpty() && !Validator.isValidEmailAddress(this.user.getEmail())) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "電子郵件格式錯誤"));
			} else if (this.user.getBlogTitle() == null || this.user.getBlogTitle().trim().isEmpty()) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "必須輸入部落格標題"));
			} else if (this.asManager
					&& (this.user.getEmail() == null || this.user.getEmail().trim().isEmpty() || this.manager.getEmail_pw() == null || this.manager.getEmail_pw().trim().isEmpty() || this.manager.getSmtpServer() == null
							|| this.manager.getSmtpServer().trim().isEmpty() || this.manager.getSmtpServer_port() == null || this.manager.getSmtpServer_port().isEmpty())) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "管理者必須輸入電子郵件資訊"));
			} else {

				if (this.asManager) {
					// 更新管理者資料
					this.manager.setId(this.user.getId());
					this.manager.setEmail(this.user.getEmail());
					this.manager.setEmail_pw(StringHex.convertStringToHex(this.manager.getEmail_pw()));
					this.userDao.updateManager(this.manager);
					this.logger.info("管理者 " + this.manager.getId() + " 更新了管理者資料");
				}
				// 調整使用者資料
				this.user.setId(this.user.getId().trim().toLowerCase());
				this.user.setPw(this.user.getPw().trim().toLowerCase());
				this.user.setPw(StringHex.convertStringToHex(this.user.getPw()));
				this.user.setDescription(this.user.getDescription().trim().replaceAll("\r\n", "<br/>").replaceAll("\n", "<br/>"));
				this.user.setBlogTitle(this.user.getBlogTitle());
				if (this.user.getEmail() != null) {
					this.user.setEmail(this.user.getEmail().trim());
				}
				if (this.user.getPic() != null) {
					this.user.setPic(this.user.getPic().trim());
				}
				if (this.user.getBlogPic() != null) {
					this.user.setBlogPic(this.user.getBlogPic().trim());
				}
				if (this.hadLogin) {
					// 更新使用者資料
					this.userDao.updateUser(this.user);
					this.logger.info("使用者 " + this.user.getId() + " 更新了使用者資料");
					this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "-- Eros blog --", "使用者資料已更新"));
				} else {
					// 新增使用者
					this.user.setAccountDate(new Date());
					this.userDao.addUser(this.user);
					this.logger.info("使用者 " + this.user.getId() + " 成功申請帳號");
					session.removeAttribute("userId");
					session.setAttribute("userId", this.user.getId());
					this.logger.info("使用者 " + this.user.getId() + " 申請後登入");
					returnStr = "blog?author=" + this.user.getId() + "&faces-redirect=true";
					// 寄出通知信
					if (this.user.getEmail() != null && !this.user.getEmail().trim().isEmpty()) {
						if (!this.asManager && (this.manager == null || this.manager.getId().trim().isEmpty())) {
							this.manager = this.userDao.queryManager();
						}
						if (this.manager.getEmail() != null && !this.manager.getEmail().isEmpty()) {
							this.manager.setEmail_pw(StringHex.convertHexToString(this.manager.getEmail_pw()));
							EmailContent emailContent = new EmailContent();
							emailContent.setReceiver(this.user.getEmail());
							emailContent.setTitle("-- Eros blog -- 註冊成功通知");
							String str = "親愛的使用者 " + this.user.getName() + " (" + this.user.getId() + ")";
							str += "<br/><br/>感謝您在 Eros blog 註冊！您可以開始使用您的部落格和撰寫、回覆文章，期待您發表的優質好文 :)";
							str += "<br/><br/><br/>Eros blog 管理者敬上";
							str += "<br/>(本信件為自動信件，請勿回覆)";
							emailContent.setBody(str);
							SendEmail.SendManagerMail(manager, emailContent, true);
							this.logger.info("註冊通知信已發給使用者 " + this.user.getId() + " 以及管理員");
						}
					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (EmailException e) {
			e.printStackTrace();
			this.logger.error(e);
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "Email 寄送異常: " + e.getMessage()));
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			this.logger.error(e);
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "Email 寄送錯誤: " + e.getMessage()));
		} catch (MessagingException e) {
			e.printStackTrace();
			this.logger.error(e);
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "Email 寄送錯誤: " + e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

		return returnStr;
	}

	// 上傳使用者圖像
	public void uploadUserPic(FileUploadEvent event) {

		try {

			this.user.setPic("resources/img/" + Uploader.createFile(this.user.getId() + "_user", "img", event.getFile()));
			this.logger.info("使用者 " + this.user.getId() + " 上傳了自訂個人圖像");

		} catch (IOException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		}

	}

	// 上傳部落格 banner
	public void uploadBlogPic(FileUploadEvent event) {

		try {

			this.user.setBlogPic("resources/img/" + Uploader.createFile(this.user.getId() + "_banner", "img", event.getFile()));
			this.logger.info("使用者 " + this.user.getId() + " 上傳了自訂部落格橫幅");

		} catch (IOException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		}

	}

	// 上傳 CSS 檔
	public void uploadCSS(FileUploadEvent event) {

		try {

			this.user.setCssFile(Uploader.createFile(this.user.getId(), "css", event.getFile()));
			this.logger.info("使用者 " + this.user.getId() + " 上傳了自訂 CSS 檔");

		} catch (IOException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		}

	}

	public boolean isHadLogin() {
		return hadLogin;
	}

	public void setHadLogin(boolean hadLogin) {
		this.hadLogin = hadLogin;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDisplayHeader() {
		return displayHeader;
	}

	public void setDisplayHeader(String displayHeader) {
		this.displayHeader = displayHeader;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public boolean isAsManager() {
		return asManager;
	}

	public void setAsManager(boolean asManager) {
		this.asManager = asManager;
	}

	public String getBrowseUserId() {
		return browseUserId;
	}

	public void setBrowseUserId(String browseUserId) {
		this.browseUserId = browseUserId;
	}

	public StreamedContent getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}

	public String getComparePw() {
		return comparePw;
	}

	public void setComparePw(String comparePw) {
		this.comparePw = comparePw;
	}

}
