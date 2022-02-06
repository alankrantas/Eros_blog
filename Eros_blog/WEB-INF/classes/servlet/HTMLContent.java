package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import daoImpl.ContentDaoImpl;
import data.Content;

/**
 * Servlet implementation class HTMLContent
 */
/**
 * 文章匯出 html servlet
 * 
 * @author Alan
 * 
 */
public class HTMLContent extends HttpServlet {

	private static final long serialVersionUID = 2230618851999990989L;

	private Logger logger = Logger.getLogger(this.getClass().getName());// logger
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HTMLContent() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ServletContext context = getServletContext();
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(context);
		HttpSession session = request.getSession(false);
		
		ContentDaoImpl contentDao = (ContentDaoImpl) applicationContext.getBean("contentDao");
		String contentId = request.getParameter("content");
		Content content = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		try {
			
			// 檢查登入
			if (session.getAttribute("userId") != null && !session.getAttribute("userId").toString().isEmpty()) {
				// 檢查文章id
				if (contentId != null && !contentId.isEmpty()) {
					// 讀取文章
					content = contentDao.querySingleContent(contentId);
					// 檢查是否為合法輸出者
					if (content != null && content.getAuthorId().equals(session.getAttribute("userId").toString())) {
						if (content != null) {
							out.println("<html>");
							out.println("<head><title> -- Eros blog -- 文章 #" + content.getId() + ": " + content.getTitle() + "</title></head");
							out.println("<body>");
							out.println("<b>-- Eros blog --</b><br/>");
							out.println("<br/>");
							out.println("作者: " + content.getAuthor() + " (" + content.getAuthorId() + ")<br/>");
							out.println("發表於: " + sdf.format(content.getDate()).toString() + "<br/>");
							out.println("<br/><hr/><br/>");
							if (content.getPic() != null && !content.getPic().isEmpty()) {
								out.println("<img src='" + content.getPic() + "' style='max-width: 500px; max-height: 500px;'/><br/><br/><br/><br/>");
							} else {
								out.println("<br/><br/><br/>");
							}
							out.println(content.getBody());
							out.println("<br/><br/><br/>");
							out.println("最後修改於: " + sdf.format(content.getLastmodify_date()) + "<br/>");
							out.println("本文件輸出於: " + sdf.format(new Date()).toString() + "<br/>");
							out.println("<br/>");
							out.println("<form>");
							out.println("<input type='button' value='列印' onclick='window.print();'>");
							out.println("</form>");
							out.println("</body>");
							out.println("</html>");
						}
					} else {
						out.println("<html>");
						out.println("<head><title>-- Eros blog -- 文章 #" + content.getId() + ": " + content.getTitle() + "</title></head");
						out.println("<body>");
						out.println("-- Eros blog --<br/>");
						out.println("<br/>");
						out.println("您沒有權限輸出此文章");
						out.println("</body>");
						out.println("</html>");
					}
				} else {
					out.println("<html>");
					out.println("<head><title>-- Eros blog --</title></head");
					out.println("<body>");
					out.println("-- Eros blog --<br/>");
					out.println("<br/>");
					out.println("查無文章");
					out.println("</body>");
					out.println("</html>");
				}
			} else {
				out.println("<html>");
				out.println("<head><title>-- Eros blog --</title></head");
				out.println("<body>");
				out.println("-- Eros blog --<br/>");
				out.println("<br/>");
				out.println("請先登入");
				out.println("</body>");
				out.println("</html>");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			this.logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e);
		}
		
		out.close();
	}

}
