package bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import dao.ContentDao;
import dao.UserDao;
import data.Content;
import data.User;

/**
 * Managedbean for read.xhtml
 * 
 * @author Alan
 * 
 */
@Component(value = "readBean")
@Scope("viewScoped")
public class ReadBean implements Serializable {

	private static final long serialVersionUID = 8342442726315375116L;

	private boolean hadLogin;// 登入狀態
	private String browseUserId;// 登入使用者id
	private boolean hasContent;// 有文章可顯示
	private boolean hasPic;// 有圖片可顯示
	private boolean hasUserPic;// 有使用者圖片可顯示
	private boolean editable;// 可以編輯;
	private String contentReadId;// 讀取文章id
	private User author;// 作者資料
	private Content content;// 文章內容
	private Content repliedContent;// 回覆的文章
	private boolean hasRepliedContent;// 本文有回覆的對象
	private List<Content> replyContents = null;// 回覆文章列表

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
				} else {
					this.hadLogin = true;
					this.browseUserId = session.getAttribute("userId").toString();
				}

				// 檢查傳入文章編號
				if (this.contentReadId == null || this.contentReadId.isEmpty()) {
					this.hasContent = false;
					this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "-- Eros blog --", "查無文章"));
				} else {

					// 檢查文章
					if (this.contentDao.queryIfContentExists(this.contentReadId, null)) {
						// 載入文章
						this.content = this.contentDao.querySingleContent(this.contentReadId);
						this.hasContent = true;
						this.cssFileName = this.userDao.queryCss(this.content.getAuthorId());
						if (this.cssFileName == null || this.cssFileName.isEmpty()) {
							this.cssFileName = UserDao.DEFAULT_CSS_FILE;
						}
						if (this.content.getPic() != null && !this.content.getPic().isEmpty()) {
							this.hasPic = true;
						} else {
							this.hasPic = false;
						}
						// 瀏覽者是文章主人
						if (this.browseUserId != null && !this.browseUserId.isEmpty() && this.browseUserId.equals(this.content.getAuthorId())) {
							this.editable = true;
						} else {
							this.editable = false;
						}
						// 載入本文回覆之文章
						if (this.content.getReply_id() != null && !this.content.getReply_id().isEmpty()) {
							if (this.contentDao.queryIfContentExists(this.content.getReply_id(), null)) {
								this.repliedContent = this.contentDao.querySingleContentSimple(this.content.getReply_id());
								this.hasRepliedContent = true;
							} else {
								this.hasRepliedContent = false;
								if (this.editable) {
									this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "-- Eros blog --", "您引用的文章已經不存在"));
								}
							}
						} else {
							this.hasRepliedContent = false;
						}
						// 載入回覆文章列表和計算數量
						this.replyContents = this.contentDao.queryRepliedContents(this.contentReadId);
						// 載入作者
						this.author = this.userDao.querySingleUser(this.content.getAuthorId());
						if (this.author.getPic() != null && !this.author.getPic().isEmpty()) {
							this.hasUserPic = true;
						} else {
							this.hasUserPic = false;
						}
					} else {
						this.hasContent = false;
						this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "-- Eros blog --", "查無文章"));
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

	// 編輯文章
	public String editContent() {

		return "editor?content=" + this.content.getId() + "&faces-redirect=true";
	}

	// 刪除文章
	public String deleteContent() {

		try {

			this.contentDao.deleteContent(this.content.getId());
			this.logger.info("使用者 " + this.browseUserId + " 刪除了文章 (編號" + this.content.getId() + ")");

		} catch (SQLException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

		return "blog?author=" + this.author.getId() + "&faces-redirect=true";
	}

	// 回應文章
	public String replyContent() {

		return "editor?reply=" + this.content.getId() + "&faces-redirect=true";
	}

	public String getContentReadId() {
		return contentReadId;
	}

	public void setContentReadId(String contentReadId) {
		this.contentReadId = contentReadId;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public ContentDao getContentDao() {
		return contentDao;
	}

	public void setContentDao(ContentDao contentDao) {
		this.contentDao = contentDao;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public boolean isHasContent() {
		return hasContent;
	}

	public void setHasContent(boolean hasContent) {
		this.hasContent = hasContent;
	}

	public boolean isHadLogin() {
		return hadLogin;
	}

	public void setHadLogin(boolean hadLogin) {
		this.hadLogin = hadLogin;
	}

	public List<Content> getReplyContents() {
		return replyContents;
	}

	public void setReplyContents(List<Content> replyContents) {
		this.replyContents = replyContents;
	}

	public boolean isHasPic() {
		return hasPic;
	}

	public void setHasPic(boolean hasPic) {
		this.hasPic = hasPic;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isHasUserPic() {
		return hasUserPic;
	}

	public void setHasUserPic(boolean hasUserPic) {
		this.hasUserPic = hasUserPic;
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

	public String getBrowseUserId() {
		return browseUserId;
	}

	public void setBrowseUserId(String browseUserId) {
		this.browseUserId = browseUserId;
	}

}
