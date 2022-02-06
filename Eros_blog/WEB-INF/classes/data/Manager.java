package data;

import java.io.Serializable;

/**
 * Data object for manager
 * 
 * @author Alan
 * 
 */
public class Manager implements Serializable {

	private static final long serialVersionUID = 8512630582478720430L;

	private String id;// 管理者帳號
	private String email;// 電子郵件
	private String email_pw;// 密碼
	private String smtpServer;// SMTP 伺服器位址
	private String smtpServer_port;// SMTP 伺服器埠

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail_pw() {
		return email_pw;
	}

	public void setEmail_pw(String email_pw) {
		this.email_pw = email_pw;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public String getSmtpServer_port() {
		return smtpServer_port;
	}

	public void setSmtpServer_port(String smtpServer_port) {
		this.smtpServer_port = smtpServer_port;
	}

}
