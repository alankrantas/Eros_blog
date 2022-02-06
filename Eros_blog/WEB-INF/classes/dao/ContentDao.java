package dao;

import java.sql.SQLException;
import java.util.List;

import data.Content;

/**
 * Interface for ContentDaoImpl
 * 
 * @author Alan
 * 
 */
public interface ContentDao {

	/**
	 * 部落格顯示文章和 RSS 匯出的最高數量
	 */
	public static final int PAGENUM = 30;
	/**
	 * 文章可被回覆的最大數量
	 */
	public static final int REPLY_LIMIT = 1000;

	/**
	 * 查詢最近的文章列表
	 * 
	 * @param num
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public List<Content> queryLatestContents(int num) throws SQLException, Exception;

	/**
	 * 查詢特定作者的文章列表
	 * 
	 * @param author
	 * @param title
	 * @param queryType
	 *            2=全部, 0=主要, 1=回覆
	 * @param pageNum
	 * @param returnBody
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public List<Content> queryAuthorsContents(String author, String title, String queryType, int pageNum, boolean returnBody) throws SQLException, Exception;

	/**
	 * 查詢特定作者的文章數量
	 * 
	 * @param author
	 * @param title
	 * @param queryType
	 *            2=全部, 0=主要, 1=回覆
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public long queryAuthorsContentsCount(String author, String title, String queryType) throws SQLException, Exception;

	/**
	 * 查詢單篇文章(詳細)
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Content querySingleContent(String id) throws SQLException, Exception;

	/**
	 * 查詢單篇文章(概要)
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Content querySingleContentSimple(String id) throws SQLException, Exception;

	/**
	 * 查詢文章是否存在
	 * 
	 * @param id
	 * @param author
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean queryIfContentExists(String id, String author) throws SQLException, Exception;

	/**
	 * 傳回新增文章使用的id
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String returnNewContentId() throws SQLException, Exception;

	/**
	 * 查詢針對某文章回覆的文章列表
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public List<Content> queryRepliedContents(String id) throws SQLException, Exception;

	/**
	 * 查詢針對某文章回覆的文章數量
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public int queryRepliedContentsCount(String id) throws SQLException, Exception;

	/**
	 * 新增文章
	 * 
	 * @param content
	 * @throws SQLException
	 * @throws Exception
	 */
	public void addContent(Content content) throws SQLException, Exception;

	/**
	 * 更新文章
	 * 
	 * @param content
	 * @throws SQLException
	 * @throws Exception
	 */
	public void updateContent(Content content) throws SQLException, Exception;

	/**
	 * 刪除文章
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws Exception
	 */
	public void deleteContent(String id) throws SQLException, Exception;

}
