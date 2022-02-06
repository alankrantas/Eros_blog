/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50513
Source Host           : localhost:3306
Source Database       : eros

Target Server Type    : MYSQL
Target Server Version : 50513
File Encoding         : 65001

Date: 2011-09-04 17:18:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `content`
-- ----------------------------
DROP TABLE IF EXISTS `content`;
CREATE TABLE `content` (
  `content_id` varchar(20) NOT NULL,
  `content_author` varchar(20) NOT NULL,
  `content_date` datetime NOT NULL,
  `content_lastmodify_date` datetime NOT NULL,
  `content_title` varchar(100) NOT NULL,
  `content_body` longtext,
  `content_reply_id` varchar(20) DEFAULT NULL,
  `content_pic` varchar(255) DEFAULT NULL,
  `content_is_reply_only` int(1) NOT NULL,
  UNIQUE KEY `id` (`content_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of content
-- ----------------------------

-- ----------------------------
-- Table structure for `manager`
-- ----------------------------
DROP TABLE IF EXISTS `manager`;
CREATE TABLE `manager` (
  `manager_id` varchar(20) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `email_pw` varchar(50) DEFAULT NULL,
  `smtp_server` varchar(50) DEFAULT NULL,
  `smtp_server_port` varchar(5) DEFAULT NULL,
  UNIQUE KEY `manager_id` (`manager_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of manager
-- ----------------------------
INSERT INTO `manager` VALUES ('admin', '', '', '', '');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` varchar(20) NOT NULL,
  `user_pw` varchar(50) NOT NULL,
  `user_name` varchar(50) NOT NULL,
  `user_description` varchar(200) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `user_pic` varchar(255) DEFAULT NULL,
  `blog_title` varchar(50) NOT NULL,
  `blog_pic` varchar(255) DEFAULT NULL,
  `account_date` datetime NOT NULL,
  `css_file` varchar(25) DEFAULT NULL,
  UNIQUE KEY `id` (`user_id`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('admin', '31323334', '管理員', '', '', '', 'This is the Eros blog.', '', '2011-09-01 00:00:00', '');
