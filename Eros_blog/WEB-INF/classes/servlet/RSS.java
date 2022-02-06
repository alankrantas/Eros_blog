package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import daoImpl.ContentDaoImpl;
import daoImpl.UserDaoImpl;
import data.Content;
import data.Manager;
import data.User;

/**
 * Servlet implementation class RSS
 */
/**
 * RSS feed servlet
 * 
 * @author Alan
 * 
 */
public class RSS extends HttpServlet {

	private static final long serialVersionUID = 5675654577145789066L;

	private Logger logger = Logger.getLogger(this.getClass().getName());// logger

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RSS() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ServletContext context = getServletContext();
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(context);

		UserDaoImpl userDao = (UserDaoImpl) applicationContext.getBean("userDao");
		ContentDaoImpl contentDao = (ContentDaoImpl) applicationContext.getBean("contentDao");
		String authorId = request.getParameter("author");
		String url = request.getRequestURL().toString();
		String blogUrl = request.getRequestURL().toString().replaceAll("RSS", "blog.faces?author=" + authorId);
		User author = null;
		Manager manager = null;
		List<Content> contents = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml; charset=utf-8");
		PrintWriter out = response.getWriter();
		XMLOutputter xmlOut = new XMLOutputter();
		Element root = new Element("nodata");
		Document doc = new Document(root);

		if (authorId != null && !authorId.isEmpty()) {

			try {

				// 讀取作者和文章
				author = userDao.querySingleUser(authorId);
				manager = userDao.queryManager();
				contents = contentDao.queryAuthorsContents(authorId, null, "2", 0, false);

				// 設定 RSS 2.0 feed
				root = new Element("rss");
				root.setAttribute("version", "2.0");
				Element channel = new Element("channel");
				root.addContent(channel);
				if (author != null) {
					Element userData = new Element("title");
					userData.setText(author.getBlogTitle());
					channel.addContent(userData);
					userData = new Element("link");
					userData.setText(blogUrl);
					channel.addContent(userData);
					userData = new Element("description");
					userData.setText(author.getDescription());
					channel.addContent(userData);
					userData = new Element("copyright");
					userData.setText("-- Eros blog -- Copyright 2011");
					channel.addContent(userData);
					userData = new Element("pubDate");
					userData.setText((sdf.format(new Date()).toString()));
					channel.addContent(userData);
					if (contents != null && contents.size() > 0) {
						userData = new Element("lastBuildDate");
						userData.setText((sdf.format(((Content) contents.get(0)).getDate())).toString());
						channel.addContent(userData);
					}
					userData = new Element("generator");
					userData.setText("Java Servlet 2.5");
					channel.addContent(userData);
					userData = new Element("managingEditor");
					userData.setText(author.getId());
					channel.addContent(userData);
					userData = new Element("webMaster");
					userData.setText(manager.getId());
					channel.addContent(userData);
					userData = null;
					if (contents != null && contents.size() > 0) {
						for (Content data : contents) {
							Element item = new Element("item");
							Element contentData = new Element("title");
							contentData.setText(data.getTitle());
							item.addContent(contentData);
							contentData = new Element("link");
							contentData.setText(url.replaceAll("RSS", "read.faces?content=" + data.getId()));
							item.addContent(contentData);
							contentData = new Element("description");
							if (data.getPic() != null && !data.getPic().isEmpty()) {
								CDATA cdata = new CDATA("<img src='" + data.getPic() + "' style='max-width: 300px; max-height: 500px;'/>");
								contentData.setContent(cdata);
							}
							item.addContent(contentData);
							contentData = new Element("author");
							contentData.setText(author.getName() + " (" + author.getId() + ")");
							item.addContent(contentData);
							contentData = new Element("pubDate");
							contentData.setText((sdf.format(data.getDate())).toString());
							item.addContent(contentData);
							contentData = new Element("source");
							contentData.setText(blogUrl);
							item.addContent(contentData);
							channel.addContent(item);
						}
					}
				}
				doc = new Document(root);

			} catch (SQLException e) {
				e.printStackTrace();
				this.logger.error(e);
			} catch (Exception e) {
				e.printStackTrace();
				this.logger.error(e);
			}
		}

		// 輸出 XML
		xmlOut.output(doc, out);
		out.flush();
		out.close();
	}

}
