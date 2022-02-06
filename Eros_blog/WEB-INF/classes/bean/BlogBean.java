package bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
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
 * Managedbean for blog.xhtml
 * 
 * @author Alan
 * 
 */
@Component(value = "blogBean")
@Scope("viewScoped")
public class BlogBean implements Serializable {

	private static final long serialVersionUID = 1459010028090935065L;

	private boolean hadLogin;// 登入狀態
	private String browseUserId;// 登入使用者代號
	private String authorReadId;// 傳入的使用者代碼
	private boolean hasUser;// 有傳入作者代碼
	private boolean hasUserPic;// 有使用者圖片可顯示
	private boolean hasBlogPic;// 有部落格圖片可顯示
	private boolean editable;// 可以編輯;
	private String viewMode;// 瀏覽模式(2=全部, 1=只有回覆文章, 0=只有未設成回覆文章)
	private User blogUser;// 部落格使用者
	private List<Content> contents;// 文章
	private String searchTitle;// 搜尋文章標題
	private boolean canNextPage;// 可以捲下一頁
	private boolean canPrePage;// 可以捲上一頁
	private int pageIndex;// 文章分頁號
	private long totalRows;// 文章總數
	private String selectedContentId;// 編輯和回覆時選擇的文章id
	private long authorTotalContentCount;// 作者發表文章總數

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
				HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
				this.browseUserId = "";
				this.searchTitle = "";
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

				// 檢查傳入使用者編號
				if (this.authorReadId == null || this.authorReadId.isEmpty()) {
					this.hasUser = false;
					this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "-- Eros blog --", "查無使用者"));
				} else {

					// 檢查部落格作者
					if (this.userDao.checkSingleUser(this.authorReadId)) {
						this.hasUser = true;
						// 載入部落格作者
						this.blogUser = this.userDao.querySingleUser(this.authorReadId);
						this.cssFileName = this.blogUser.getCssFile();
						if (this.cssFileName == null || this.cssFileName.isEmpty()) {
							this.cssFileName = UserDao.DEFAULT_CSS_FILE;
						}
						if (this.blogUser.getPic() != null && !this.blogUser.getPic().isEmpty()) {
							this.hasUserPic = true;
						} else {
							this.hasUserPic = false;
						}
						if (this.blogUser.getBlogPic() != null && !this.blogUser.getBlogPic().isEmpty()) {
							this.hasBlogPic = true;
						} else {
							this.hasBlogPic = false;
						}
						// 瀏覽者為部落格主人
						if (this.browseUserId.equals(this.blogUser.getId())) {
							this.editable = true;
							this.viewMode = "2";
						} else {
							this.editable = false;
							this.viewMode = "0";
						}
						// 載入文章和設定瀏覽模式
						this.pageIndex = 0;
						this.selectedContentId = "";
						this.contents = this.contentDao.queryAuthorsContents(this.authorReadId, null, this.viewMode, this.pageIndex, false);
						this.totalRows = this.contentDao.queryAuthorsContentsCount(this.authorReadId, null, this.viewMode);
						this.authorTotalContentCount = this.totalRows;
						if (this.totalRows > ContentDao.PAGENUM) {
							this.canNextPage = true;
						} else {
							this.canNextPage = false;
						}
						this.canPrePage = false;

					} else {
						this.hasUser = false;
						this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "-- Eros blog --", "查無使用者"));
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

	// 寫新文章
	public String newContent() {

		return "editor?faces-redirect=true";
	}

	// 編輯文章
	public String editContent() {

		return "editor?content=" + this.selectedContentId + "&faces-redirect=true";
	}

	// 刪除文章
	public String deleteContent() {

		try {

			this.contentDao.deleteContent(this.selectedContentId);
			this.logger.info("使用者 " + this.browseUserId + " 刪除了文章 (編號" + this.selectedContentId + ")");

			this.pageIndex = 0;
			if (this.editable) {
				this.viewMode = "2";
			} else {
				this.viewMode = "0";
			}
			this.searchTitle = "";
			this.contents = this.contentDao.queryAuthorsContents(this.authorReadId, null, this.viewMode, this.pageIndex, false);
			this.totalRows = this.contentDao.queryAuthorsContentsCount(this.authorReadId, null, this.viewMode);
			this.authorTotalContentCount = this.contentDao.queryAuthorsContentsCount(this.authorReadId, null, "2");
			if (this.totalRows > ContentDao.PAGENUM) {
				this.canNextPage = true;
			} else {
				this.canNextPage = false;
			}
			this.canPrePage = false;
			this.searchTitle = "";

		} catch (SQLException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}

		return "blog?author=" + this.blogUser.getId() + "&faces-redirect=true";
	}

	// 設定瀏覽模式
	public void setViewMode(ValueChangeEvent event) {

		try {

			this.viewMode = event.getNewValue().toString();
			this.pageIndex = 0;
			this.searchTitle = "";
			this.contents = this.contentDao.queryAuthorsContents(this.authorReadId, null, this.viewMode, this.pageIndex, false);
			this.totalRows = this.contentDao.queryAuthorsContentsCount(this.authorReadId, null, this.viewMode);
			if (this.totalRows == 0) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "-- Eros blog --", "查無文章"));
			}
			if (this.totalRows > ContentDao.PAGENUM) {
				this.canNextPage = true;
			} else {
				this.canNextPage = false;
			}
			this.canPrePage = false;

		} catch (SQLException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		} finally {

		}
	}

	// 搜尋文章
	public void searchContents() {

		try {

			if (this.searchTitle == null || this.searchTitle.isEmpty()) {
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "-- Eros blog --", "請輸入搜尋文字"));
			} else {

				this.contents = this.contentDao.queryAuthorsContents(this.authorReadId, this.searchTitle, this.viewMode, this.pageIndex, false);
				this.totalRows = this.contentDao.queryAuthorsContentsCount(this.authorReadId, this.searchTitle, this.viewMode);
				if (this.totalRows == 0) {
					this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "-- Eros blog --", "查無文章"));
				}
				if (this.totalRows > ContentDao.PAGENUM) {
					this.canNextPage = true;
				} else {
					this.canNextPage = false;
				}
				this.canPrePage = false;

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

	// 下一頁
	public void contentNextPage() {

		try {

			int maxPages = (int) (this.totalRows / ContentDao.PAGENUM);
			if (this.pageIndex < maxPages) {
				this.pageIndex++;
				this.contents = this.contentDao.queryAuthorsContents(this.authorReadId, this.searchTitle, this.viewMode, this.pageIndex, false);
			}
			if (this.pageIndex < maxPages) {
				this.canNextPage = true;
			} else {
				this.canNextPage = false;
			}
			if (this.pageIndex > 0) {
				this.canPrePage = true;
			} else {
				this.canPrePage = false;
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

	// 上一頁
	public void contentPrePage() {

		try {

			int maxPages = (int) (this.totalRows / ContentDao.PAGENUM);
			if (this.pageIndex > 0) {
				this.pageIndex--;
				this.contents = this.contentDao.queryAuthorsContents(this.authorReadId, this.searchTitle, this.viewMode, this.pageIndex, false);
			}
			if (this.pageIndex < maxPages) {
				this.canNextPage = true;
			} else {
				this.canNextPage = false;
			}
			if (this.pageIndex > 0) {
				this.canPrePage = true;
			} else {
				this.canPrePage = false;
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

	public boolean isHadLogin() {
		return hadLogin;
	}

	public void setHadLogin(boolean hadLogin) {
		this.hadLogin = hadLogin;
	}

	public boolean isHasUserPic() {
		return hasUserPic;
	}

	public void setHasUserPic(boolean hasUserPic) {
		this.hasUserPic = hasUserPic;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public User getBlogUser() {
		return blogUser;
	}

	public void setBlogUser(User blogUser) {
		this.blogUser = blogUser;
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

	public String getAuthorReadId() {
		return authorReadId;
	}

	public void setAuthorReadId(String authorReadId) {
		this.authorReadId = authorReadId;
	}

	public boolean isHasUser() {
		return hasUser;
	}

	public void setHasUser(boolean hasUser) {
		this.hasUser = hasUser;
	}

	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public List<Content> getContents() {
		return contents;
	}

	public void setContents(List<Content> contents) {
		this.contents = contents;
	}

	public String getSelectedContentId() {
		return selectedContentId;
	}

	public void setSelectedContentId(String selectedContentId) {
		this.selectedContentId = selectedContentId;
	}

	public boolean isCanNextPage() {
		return canNextPage;
	}

	public void setCanNextPage(boolean canNextPage) {
		this.canNextPage = canNextPage;
	}

	public boolean isCanPrePage() {
		return canPrePage;
	}

	public void setCanPrePage(boolean canPrePage) {
		this.canPrePage = canPrePage;
	}

	public boolean isHasBlogPic() {
		return hasBlogPic;
	}

	public void setHasBlogPic(boolean hasBlogPic) {
		this.hasBlogPic = hasBlogPic;
	}

	public String getBrowseUserId() {
		return browseUserId;
	}

	public void setBrowseUserId(String browseUserId) {
		this.browseUserId = browseUserId;
	}

	public String getSearchTitle() {
		return searchTitle;
	}

	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	public long getAuthorTotalContentCount() {
		return authorTotalContentCount;
	}

	public void setAuthorTotalContentCount(long authorTotalContentCount) {
		this.authorTotalContentCount = authorTotalContentCount;
	}

	public long getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

}
