package daoImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dao.ContentDao;
import data.Content;

/**
 * ContentDao JDBC 實作, 方法請參閱介面
 * 
 * @author Alan
 * 
 */
@Repository("contentDao")
public class ContentDaoImpl implements ContentDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	@SuppressWarnings("rawtypes")
	public List<Content> queryLatestContents(int num) throws SQLException, Exception {

		String sql = "select c.content_id, c.content_author, u.user_name, c.content_date, c.content_title, c.content_pic, c.content_is_reply_only, (select count(*) from content r where c.content_id = r.content_reply_id) as content_reply_count from user u, content c where c.content_author = u.user_id order by c.content_date desc limit "
				+ String.valueOf(num);
		ArrayList<Content> contents = null;

		List rows = this.jdbcTemplate.queryForList(sql);

		Iterator it = rows.iterator();
		if (it.hasNext()) {
			contents = new ArrayList<Content>();
		}

		while (it.hasNext()) {
			Map userMap = (Map) it.next();
			Content content = new Content();
			content = new Content();
			content.setId(userMap.get("content_id").toString());
			content.setAuthor(userMap.get("user_name").toString());
			content.setAuthorId(userMap.get("content_author").toString());
			content.setDate((Date) userMap.get("content_date"));
			content.setTitle(userMap.get("content_title").toString());
			content.setPic(userMap.get("content_pic") != null ? userMap.get("content_pic").toString() : "");
			content.setReplyOnly(userMap.get("content_is_reply_only").toString().equals("1") ? true : false);
			content.setReplyNum(userMap.get("content_reply_count").toString());
			contents.add(content);
		}

		return contents;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<Content> queryAuthorsContents(String author, String title, String queryType, int pageNum, boolean returnBody) throws SQLException, Exception {

		String sql = "select";
		if (returnBody) {
			sql += " c.content_body";
		}
		sql += " c.content_id, c.content_date, c.content_title, c.content_pic, c.content_is_reply_only, (select count(*) from content r where c.content_id = r.content_reply_id) as content_reply_count from user u, content c where c.content_author = u.user_id";
		if (author != null && !author.isEmpty()) {
			sql += " and c.content_author = ?";
		}
		if (title != null && !title.isEmpty()) {
			sql += " and locate(?, c.content_title) > 0";
		}
		if (queryType.equals("0")) {
			sql += " and c.content_is_reply_only = 0";
		} else if (queryType.equals("1")) {
			sql += " and c.content_is_reply_only = 1";
		}
		sql += " order by c.content_date desc limit " + String.valueOf(PAGENUM) + " offset " + String.valueOf(pageNum * PAGENUM);
		ArrayList<Content> contents = null;

		List rows = null;
		if (author != null && !author.isEmpty() && title != null && !title.isEmpty()) {
			rows = this.jdbcTemplate.queryForList(sql, new Object[] { author, title });
		} else if (author != null && !author.isEmpty() && (title == null || title.isEmpty())) {
			rows = this.jdbcTemplate.queryForList(sql, new Object[] { author });
		} else if (title != null && !title.isEmpty() && (author == null || author.isEmpty())) {
			rows = this.jdbcTemplate.queryForList(sql, new Object[] { title });
		} else {
			rows = this.jdbcTemplate.queryForList(sql);
		}

		Iterator it = rows.iterator();
		if (it.hasNext()) {
			contents = new ArrayList<Content>();
		}

		while (it.hasNext()) {
			Map userMap = (Map) it.next();
			Content content = new Content();
			content = new Content();
			content.setId(userMap.get("content_id").toString());
			content.setDate((Date) userMap.get("content_date"));
			content.setTitle(userMap.get("content_title").toString());
			if (returnBody) {
				content.setBody(userMap.get("content_body").toString());
			}
			content.setPic(userMap.get("content_pic") != null ? userMap.get("content_pic").toString() : "");
			content.setReplyOnly(userMap.get("content_is_reply_only").toString().equals("1") ? true : false);
			content.setReplyNum(userMap.get("content_reply_count").toString());
			contents.add(content);
		}

		return contents;
	}

	@Override
	public long queryAuthorsContentsCount(String author, String title, String queryType) throws SQLException, Exception {

		String sql = "select count(*) from user, content where content_author = user_id";
		if (author != null && !author.isEmpty()) {
			sql += " and content_author = ?";
		}
		if (title != null && !title.isEmpty()) {
			sql += " and locate(?, content_title) > 0";
		}
		if (queryType.equals("0")) {
			sql += " and content_is_reply_only = 0";
		} else if (queryType.equals("1")) {
			sql += " and content_is_reply_only = 1";
		}

		long count = 0;
		if (author != null && !author.isEmpty() && title != null && !title.isEmpty()) {
			count = this.jdbcTemplate.queryForInt(sql, new Object[] { author, title });
		} else if (author != null && !author.isEmpty() && (title == null || title.isEmpty())) {
			count = this.jdbcTemplate.queryForInt(sql, new Object[] { author });
		} else if (title != null && !title.isEmpty() && (author == null || author.isEmpty())) {
			count = this.jdbcTemplate.queryForInt(sql, new Object[] { title });
		} else {
			count = this.jdbcTemplate.queryForInt(sql);
		}

		return count;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Content querySingleContent(String id) throws SQLException, Exception {

		String sql = "select c.content_id, c.content_author, u.user_name, c.content_date, c.content_lastmodify_date, c.content_title, c.content_body, c.content_reply_id, c.content_pic, c.content_is_reply_only, (select count(*) from content r where c.content_id = r.content_reply_id) as content_reply_count from user u, content c where c.content_id = ? and c.content_author = u.user_id";
		Content content = null;

		List rows = this.jdbcTemplate.queryForList(sql, new Object[] { id });
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Map userMap = (Map) it.next();
			content = new Content();
			content.setId(userMap.get("content_id").toString());
			content.setAuthor(userMap.get("user_name").toString());
			content.setAuthorId(userMap.get("content_author").toString());
			content.setDate((Date) userMap.get("content_date"));
			content.setLastmodify_date((Date) userMap.get("content_lastmodify_date"));
			content.setTitle(userMap.get("content_title").toString());
			content.setBody(userMap.get("content_body").toString());
			content.setReply_id(userMap.get("content_reply_id") != null ? userMap.get("content_reply_id").toString() : "");
			content.setPic(userMap.get("content_pic") != null ? userMap.get("content_pic").toString() : "");
			content.setReplyOnly(userMap.get("content_is_reply_only").toString().equals("1") ? true : false);
			content.setReplyNum(userMap.get("content_reply_count").toString());
		}

		return content;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Content querySingleContentSimple(String id) throws SQLException, Exception {

		String sql = "select content_id, content_author, user_name, content_title from user, content where content_id = ? and content_author = user_id";
		Content content = null;

		List rows = this.jdbcTemplate.queryForList(sql, new Object[] { id });
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Map userMap = (Map) it.next();
			content = new Content();
			content.setId(userMap.get("content_id").toString());
			content.setAuthor(userMap.get("content_author").toString());
			content.setAuthor(userMap.get("user_name").toString());
			content.setAuthorId(userMap.get("content_author").toString());
			content.setTitle(userMap.get("content_title").toString());
		}

		return content;
	}

	@Override
	public boolean queryIfContentExists(String id, String author) throws SQLException, Exception {

		int count = 0;
		String sql = "";

		if (author != null && !author.isEmpty()) {
			sql = "select count(*) from content where content_id = ? and content_author = ?";
			count = this.jdbcTemplate.queryForInt(sql, new Object[] { id, author });
		} else {
			sql = "select count(*) from content where content_id = ?";
			count = this.jdbcTemplate.queryForInt(sql, new Object[] { id });
		}

		return count > 0 ? true : false;
	}

	@Override
	public String returnNewContentId() throws SQLException, Exception {

		String sql = "select max(content_id) from content order by content_id desc";
		String maxId = this.jdbcTemplate.queryForObject(sql, String.class);
		return maxId != null ? String.valueOf(Integer.parseInt(maxId) + 1) : "1";
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<Content> queryRepliedContents(String id) throws SQLException, Exception {

		String sql = "select content_id, content_author, user_name, content_date, content_title from user, content where content_reply_id = ? and content_author = user_id order by content_date";
		ArrayList<Content> contents = null;
		List rows = this.jdbcTemplate.queryForList(sql, new Object[] { id });
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			contents = new ArrayList<Content>();
		}
		while (it.hasNext()) {
			Map userMap = (Map) it.next();
			Content content = new Content();
			content.setId(userMap.get("content_id").toString());
			content.setAuthorId(userMap.get("content_author").toString());
			content.setAuthor(userMap.get("user_name").toString());
			content.setDate((Date) userMap.get("content_date"));
			content.setTitle(userMap.get("content_title").toString());
			contents.add(content);
		}

		return contents;
	}

	@Override
	public int queryRepliedContentsCount(String id) throws SQLException, Exception {

		String sql = "select count(*) from content where content_reply_id = ?";
		int count = this.jdbcTemplate.queryForInt(sql, new Object[] { id });
		return count;
	}

	@Override
	public void addContent(Content content) throws SQLException, Exception {

		String sql = "insert into content (content_id, content_author, content_date, content_lastmodify_date, content_title, content_body, content_reply_id, content_pic, content_is_reply_only) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		this.jdbcTemplate.update(sql, new Object[] { content.getId(), content.getAuthorId(), content.getDate(), content.getLastmodify_date(), content.getTitle(), content.getBody(), content.getReply_id(), content.getPic(),
				content.isReplyOnly() ? "1" : "0" });
	}

	@Override
	public void updateContent(Content content) throws SQLException, Exception {

		String sql = "update content set content_lastmodify_date = ?, content_title = ?, content_body = ?, content_reply_id = ?, content_pic = ?, content_is_reply_only = ? where content_id = ?";
		this.jdbcTemplate.update(sql, new Object[] { content.getLastmodify_date(), content.getTitle(), content.getBody(), content.getReply_id(), content.getPic(), content.isReplyOnly() ? "1" : "0", content.getId() });
	}

	@Override
	public void deleteContent(String id) throws SQLException, Exception {

		String sql = "delete from content where content_id = ?";
		this.jdbcTemplate.update(sql, new Object[] { id });
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) throws SQLException {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate() throws SQLException {
		return jdbcTemplate;
	}

}
