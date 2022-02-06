package data;

import java.io.Serializable;

/**
 * Data object for emails
 * 
 * @author Alan
 * 
 */
public class EmailContent implements Serializable {

	private static final long serialVersionUID = 5835357721234023861L;

	private String title;// 郵件標題
	private String body;// 郵件內文
	private String receiver;// 收件者

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

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

}
