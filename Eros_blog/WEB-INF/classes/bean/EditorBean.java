package bean;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.servlet.http.HttpSession;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import util.SendEmail;
import util.StringHex;
import util.Uploader;
import dao.ContentDao;
import dao.UserDao;
import data.Content;
import data.EmailContent;
import data.Manager;
import data.User;

/**
 * Managedbean for editor.xhtml
 * 
 * @author Alan
 * 
 */
@Component(value = "editorBean")
@Scope("viewScoped")
public class EditorBean implements Serializable {

	private static final long serialVersionUID = 3027019473422023115L;

	private boolean hadLogin;// 登入狀態
	private boolean hasSavedContent;// 有已經存檔的文章
	private String contentEditId;// 傳入文章id參數
	private String replyEditId;// 傳入回覆文章id參數
	private String submitText;// 送出鈕文字
	private Content content = new Content();// 文章內容
	private Content repliedContent = new Content();// 本文回覆的文章
	private boolean hasRepliedContent;// 本文有回覆之文章
	private String authorId;// 作者id
	private boolean normalEditorMode;// 編輯器模式(1=正常, 2=簡單)

	private FacesContext facesContext;// facesContext
	private Logger logger = Logger.getLogger(this.getClass().getName());// logger
	private String cssFileName;// CSS 主題檔案名稱

	@Autowired
	private UserDao userDao;// dao
	@Autowired
	private ContentDao contentDao;// dao

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
				if (session.getAttribute("userId") == null || session.getAttribute("userId").toString().isEmpty()) {
					this.hadLogin = false;
					this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "請先登入"));
				} else {

					this.hadLogin = true;
					this.authorId = session.getAttribute("userId").toString();
					this.cssFileName = this.userDao.queryCss(this.authorId);
					if (this.cssFileName == null || this.cssFileName.isEmpty()) {
						this.cssFileName = UserDao.DEFAULT_CSS_FILE;
					}
					// 檢查傳入參數是否有編輯文章之id
					if (this.contentEditId == null || this.contentEditId.isEmpty()) {
						this.content = new Content();
						this.repliedContent = new Content();
						this.contentEditId = "";
						this.submitText = "確定新增";
						this.hasSavedContent = false;
					} else {
						// 檢查文章
						if (this.contentDao.queryIfContentExists(this.contentEditId, this.authorId)) {
							// 讀取文章
							this.content = this.contentDao.querySingleContent(this.contentEditId);
							if (this.content.getReply_id() != null && !this.content.getReply_id().isEmpty()) {
								this.replyEditId = this.content.getReply_id();
							} else {
								this.replyEditId = "";
							}
							this.submitText = "確定儲存";
							this.hasSavedContent = true;
						} else {
							this.contentEditId = "";
							this.content = new Content();
							this.repliedContent = new Content();
							this.submitText = "確定新增";
							this.hasSavedContent = false;
							this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "-- Eros blog --", "無法讀取指定的文章編號"));
						}
					}
					// 設定回復之文章
					if (this.replyEditId != null && !this.replyEditId.isEmpty()) {
						this.content.setReply_id(this.replyEditId);
						if (this.contentDao.queryIfContentExists(this.replyEditId, null)) {
							this.setRepliedContent();
							this.hasRepliedContent = true;
						} else {
							this.hasRepliedContent = false;
							this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "查無回覆文章"));
						}
					} else {
						this.content.setReply_id("");
						this.hasRepliedContent = false;
					}
					this.normalEditorMode = true;

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

	// 新增或修改文章
	public void submitContent() {

		try {

			this.facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) this.facesContext.getExternalContext().getSession(false);

			// 檢查文章
			if (this.content.getTitle() == null || this.content.getTitle().trim().isEmpty()) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "標題不得為空"));
			} else if (this.content.getReply_id() != null && !this.content.getReply_id().trim().isEmpty() && this.contentDao.queryRepliedContentsCount(this.content.getReply_id()) > ContentDao.REPLY_LIMIT) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "欲回覆之文章已經達到回覆上限"));
			} else if (this.content.getId() != null && !this.content.getId().isEmpty() && this.content.getId().equals(this.content.getReply_id())) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "不可回覆自己"));
			} else if (this.content.getReply_id() != null && !this.content.getReply_id().isEmpty() && !this.contentDao.queryIfContentExists(this.content.getReply_id(), null)) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "查無回覆文章"));
			} else {

				// 資料庫新增或更新
				if (this.contentEditId == null || this.contentEditId.isEmpty()) {

					this.content.setId(this.contentDao.returnNewContentId());
					this.content.setAuthorId(session.getAttribute("userId").toString());
					this.content.setDate(new Date());
					this.content.setLastmodify_date(new Date());
					this.contentDao.addContent(content);
					this.submitText = "修改";
					this.contentEditId = this.content.getId();
					this.hasSavedContent = true;
					this.logger.info("使用者 " + this.authorId + " 新增了一篇文章 (編號 " + this.content.getId() + ")");
					this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "-- Eros blog --", "文章已新增 (編號 " + this.content.getId() + ")"));

					// 回覆 email
					if (this.content.getReply_id() != null && !this.content.getReply_id().isEmpty()) {
						if (this.repliedContent == null) {
							this.repliedContent = this.contentDao.querySingleContentSimple(this.content.getReply_id());
						}
						User authorUser = this.userDao.querySingleUserSimple(this.content.getAuthorId());
						User repliedUser = this.userDao.querySingleUserSimple(this.repliedContent.getAuthorId());
						if (repliedUser.getEmail() != null && !repliedUser.getEmail().isEmpty()) {
							Manager manager = this.userDao.queryManager();
							if (manager.getEmail() != null && !manager.getEmail().isEmpty()) {
								manager.setEmail_pw(StringHex.convertHexToString(manager.getEmail_pw()));
								ExternalContext ex = this.facesContext.getExternalContext();
								String url = "http://" + ex.getRequestServerName();
								if (ex.getRequestServerPort() != 80) {
									url += ":" + String.valueOf(ex.getRequestServerPort());
								}
								url += "/" + ex.getRequestServletPath();
								url += "/read?content=" + this.content.getId() + "&faces-redirect=true";
								EmailContent emailContent = new EmailContent();
								emailContent.setReceiver(repliedUser.getEmail());
								emailContent.setTitle("-- Eros blog -- 文章回覆通知");
								String str = "親愛的使用者 " + repliedUser.getName() + " (" + repliedUser.getId() + ")";
								str += "<br/><br/><br/>使用者 " + authorUser.getName() + " (" + authorUser.getId() + ") 對您的文章 (" + this.repliedContent.getTitle() + ") 發表了一篇回應：";
								str += "<br/><br/><a href='" + url + "'>" + this.content.getTitle() + "</a><br/>";
								str += "<br/><br/><br/>Eros blog 管理者敬上";
								str += "<br/>(本信件為自動信件，請勿回覆)";
								emailContent.setBody(str);
								SendEmail.SendManagerMail(manager, emailContent, false);
								this.logger.info("使用者 " + this.authorId + " 對使用者 " + repliedUser.getId() + " 的文章回覆通知信寄出");
							}
						}
					}

				} else {
					this.content.setLastmodify_date(new Date());
					this.contentDao.updateContent(content);
					this.hasSavedContent = true;
					this.logger.info("使用者 " + this.authorId + " 修改了一篇文章 (編號 " + this.content.getId() + " )");
					this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "-- Eros blog --", "文章已更新 (編號 " + this.content.getId() + ")"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (EmailException e) {
			e.printStackTrace();
			this.logger.error(e);
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "Email 寄送錯誤: " + e.getMessage()));
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
	}

	// 開新文章
	public String newContent() {

		this.content = new Content();
		this.repliedContent = new Content();
		this.contentEditId = "";
		this.replyEditId = "";
		this.hasSavedContent = false;
		this.hasRepliedContent = false;
		this.submitText = "確定新增";
		return "editor";
	}

	// 更新回覆文章資訊
	public void queryRepliedContent() {

		try {

			this.facesContext = FacesContext.getCurrentInstance();

			if (this.content.getReply_id() != null && !this.content.getReply_id().isEmpty()) {
				if (this.contentDao.queryIfContentExists(this.content.getReply_id(), null)) {
					this.setRepliedContent();
					this.hasRepliedContent = true;
				} else {
					this.hasRepliedContent = false;
					this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "查無回覆文章"));
				}
			} else {
				this.hasRepliedContent = false;
				this.content.setReply_id("");
				this.repliedContent = null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}
	}

	// 設定回覆文章時的標題
	private void setRepliedContent() throws SQLException, Exception {

		try {

			this.repliedContent = this.contentDao.querySingleContentSimple(this.content.getReply_id());
			if (this.content.getTitle() == null || this.content.getTitle().isEmpty()) {
				this.content.setTitle("Re: " + this.repliedContent.getTitle());
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

	}

	// 閱讀存檔的文章
	public String readContent() {

		return "read?content=" + this.content.getId() + "&faces-redirect=true";
	}

	// 上傳插圖
	public void uploadContentPic(FileUploadEvent event) {

		try {

			String newFileName = this.generateContentPicName();
			this.content.setPic("resources/img/" + Uploader.createFile(newFileName, "img", event.getFile()));

		} catch (IOException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

	}

	private String generateContentPicName() {

		Calendar date = Calendar.getInstance();
		String result = "";

		try {
			result = this.authorId + "_" + String.valueOf(date.get(Calendar.YEAR)) + (String.valueOf(date.get(Calendar.MONTH) + 1).length() == 1 ? "0" : "") + String.valueOf(date.get(Calendar.MONTH) + 1)
					+ (String.valueOf(date.get(Calendar.DAY_OF_MONTH)).length() == 1 ? "0" : "") + String.valueOf(date.get(Calendar.DAY_OF_MONTH)) + (String.valueOf(date.get(Calendar.HOUR)).length() == 1 ? "0" : "")
					+ String.valueOf(date.get(Calendar.HOUR)) + (String.valueOf(date.get(Calendar.MINUTE)).length() == 1 ? "0" : "") + String.valueOf(date.get(Calendar.MINUTE)) + (String.valueOf(date.get(Calendar.SECOND)).length() == 1 ? "0" : "")
					+ String.valueOf(date.get(Calendar.SECOND));
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

		return result;
	}

	public String getContentEditId() {
		return contentEditId;
	}

	public void setContentEditId(String contentEditId) {
		this.contentEditId = contentEditId;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public ContentDao getContentDao() {
		return contentDao;
	}

	public void setContentDao(ContentDao contentDao) {
		this.contentDao = contentDao;
	}

	public boolean isHadLogin() {
		return hadLogin;
	}

	public void setHadLogin(boolean hadLogin) {
		this.hadLogin = hadLogin;
	}

	public String getSubmitText() {
		return submitText;
	}

	public void setSubmitText(String submitText) {
		this.submitText = submitText;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getReplyEditId() {
		return replyEditId;
	}

	public void setReplyEditId(String replyEditId) {
		this.replyEditId = replyEditId;
	}

	public boolean isNormalEditorMode() {
		return normalEditorMode;
	}

	public void setNormalEditorMode(boolean normalEditorMode) {
		this.normalEditorMode = normalEditorMode;
	}

	public boolean isHasSavedContent() {
		return hasSavedContent;
	}

	public void setHasSavedContent(boolean hasSavedContent) {
		this.hasSavedContent = hasSavedContent;
	}

	public Content getRepliedContent() {
		return repliedContent;
	}

	public void setRepliedContent(Content repliedContent) {
		this.repliedContent = repliedContent;
	}

	public boolean isHasRepliedContent() {
		return hasRepliedContent;
	}

	public void setHasRepliedContent(boolean hasRepliedContent) {
		this.hasRepliedContent = hasRepliedContent;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
