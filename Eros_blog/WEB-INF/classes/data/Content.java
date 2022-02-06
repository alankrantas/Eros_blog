package data;

import java.io.Serializable;
import java.util.Date;

/**
 * Data object for contents
 * 
 * @author Alan
 * 
 */
public class Content implements Serializable {

	private static final long serialVersionUID = -6604591549577933601L;

	private String id;// 文章編號
	private String author;// 作者名字(來自user.user_name)
	private String authorId;// 作者帳號
	private String title;// 標題
	private String body;// 內文
	private Date date;// 發表時間
	private Date lastmodify_date;// 最後修改時間
	private String reply_id;// 回覆文章編號
	private String pic;// 文章圖片網址
	private boolean replyOnly;// 是否為回覆專用
	private String replyNum;// 擁有的回覆文章數量

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getReply_id() {
		return reply_id;
	}

	public void setReply_id(String reply_id) {
		this.reply_id = reply_id;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public Date getLastmodify_date() {
		return lastmodify_date;
	}

	public void setLastmodify_date(Date lastmodify_date) {
		this.lastmodify_date = lastmodify_date;
	}

	public boolean isReplyOnly() {
		return replyOnly;
	}

	public void setReplyOnly(boolean replyOnly) {
		this.replyOnly = replyOnly;
	}

	public String getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(String replyNum) {
		this.replyNum = replyNum;
	}

}