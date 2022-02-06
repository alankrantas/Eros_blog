package data;

import java.io.Serializable;
import java.util.Date;

/**
 * Data object for users
 * 
 * @author Alan
 * 
 */
public class User implements Serializable {

	private static final long serialVersionUID = 5621283882328934893L;

	private String id;// 使用者帳號
	private String pw;// 密碼
	private String name;// 暱稱
	private String description;// 自我描述
	private String email;// 電子郵件
	private String pic;// 個人圖片網址
	private String blogTitle;// 部落格標題
	private String blogPic;// 部落格橫幅圖片網址
	private String cssFile;// CSS 主題檔
	private Date accountDate;// 帳號建立日期

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Date getAccountDate() {
		return accountDate;
	}

	public void setAccountDate(Date accountDate) {
		this.accountDate = accountDate;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBlogTitle() {
		return blogTitle;
	}

	public void setBlogTitle(String blogTitle) {
		this.blogTitle = blogTitle;
	}

	public String getBlogPic() {
		return blogPic;
	}

	public void setBlogPic(String blogPic) {
		this.blogPic = blogPic;
	}

	public String getCssFile() {
		return cssFile;
	}

	public void setCssFile(String cssFile) {
		this.cssFile = cssFile;
	}

}
