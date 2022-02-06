## Eros blog (2011 年舊專案)

![image](https://raw.githubusercontent.com/alankrantas/Eros_blog/main/Eros_blog/resources/img/eros_banner.jpg?token=GHSAT0AAAAAABHRDDFULPZM75JOYCAAB2HMYP7TMBA)

**Eros blog** 是個以 Java 2 Enterprise Edition (J2EE) 相關技術開發的簡易部落格系統，屬於個人練習之無營利專案，其目的是傳寫一個擁有基本功能，並且較易安裝自用的專案，可給多人使用。部落格和單篇文章都可以用瀏覽器書籤記錄位置，而這個系統的特色 (及簡化) 之一是可以用文章回覆文章，沒有正式的留言。此外文章版面設計上比較適合寫書評之類的專業文章。

Eros 是希臘神話的愛神，也是最早發現的近地小行星 (433 Eros) 的名稱。

本專案整合了 JavaServer Faces (JSF) 2 和 Spring 3 DAO 的使用，而這兩個都盡量使用 annotation 的方式來設定。

> 註：本專案原置於 [Google Code](https://code.google.com/archive/p/eros-java-blog/) 但已遭刪除。2011 年秋，我第一份工作 (研發替代役) 即將結束時，決定拿當時自學的技術寫一個自己的專案，而這就是當時的成果。當時我的公司只用過 JSF 1，而且共用的 XML 設定檔常常很難管理。那時我發現使用 annotation 可以大幅減少對 XML 的依賴。當然單體式 Java 網站如今已經褪流行，因此這個 repo 只是要作為紀念而已。

### 功能

目前版本 v0.1 (2011/09)：

* 登入 / 登出
* 註冊 / 修改使用者資料
* 瀏覽特定作者部落格
* 瀏覽特定文章
* 新增 / 編輯 / 刪除 / 查詢文章
* 上傳自訂 css 檔 / 部落格橫幅圖 / 使用者圖像 / 部落格插圖 (傳到系統目錄下)
* Email 註冊通知 / 文章回覆通知
* RSS 2.0 feed (不含全文) / 單篇文章匯出 html 版(可列印或備份用)
* console / file logger

### 管理者說明

* 預設資料庫連線名稱為 mysql (localhost:3306)，資料庫名稱 eros (編碼 utf8 / utf8_general_ci)，帳號 root，無密碼。用 Navicat 使用 SQL 檔建立資料庫後，裡面會有一個管理者帳號 (ID: admin, PW: 1234) 可在網頁上登入。
* 若要啟用 email 通知，管理員必須設定一個系統用信箱及 SMTP 伺服器 (如使用 Gmail)
* 目前還沒開放管理者對使用者進行直接的管理
* log 在 WEB-INF/logs 下
* resources\img\ 和 resources\css\ 分別存放圖片和 CSS 檔，專案裡已經有的檔案是預設檔。

### CSS

* 使用者註冊之後就可以上傳使用者圖像、部落格橫幅和 CSS 檔 (可以下載預設檔當範本來修改)
* 我個人推薦的顏色調配工具是這兩個網站 http://www.degraeve.com/color-palette/ http://colorschemedesigner.com/

### 未來考慮功能

無限期延後...

* 文章自訂分類 / 分類查詢
* 使用者可選擇瀏覽和刪除上傳的文章插圖
* 站內追蹤使用者 / 站上使用者動態
* RSS / MT 匯出備份
* RSS / MT 匯入建立文章

### 開發技術

* JDK 1.6
* JavaServer Faces 2.0.6 (View & Controller)
* Primefaces 2.2.1 (JSF 2 控制項套件)
* Spring 3.0.5 (Model & JDBC 層)
* Commons Email 1.2 + Java Mail 1.4.4 (email)
* JDOM 1.1.1 (XML 解析)
* Commons logging 1.1.1 + Apache log4j 1.2.16 (logging)
* MySQL 5.5 Community Server (資料庫)
* MySQL connector for Java 5.1.17

### 其他

* 由於 JSF 2.1.x 和 Primefaces 2.1.1 會起衝突，故退回去用 2.0.6。
* 管理資料庫可使用 Navicat Lite 或其他 client；但內附的 SQL 檔若不修改，只能給 Navicat 來跑
* 我用 Eclipse 3.7 來開發
* 伺服器用 Apache Tomcat 6.0.33
