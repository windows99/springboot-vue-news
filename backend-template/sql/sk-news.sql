/*
 Navicat Premium Data Transfer

 Source Server         : mysql5
 Source Server Type    : MySQL
 Source Server Version : 50743
 Source Host           : localhost:3306
 Source Schema         : sk-news

 Target Server Type    : MySQL
 Target Server Version : 50743
 File Encoding         : 65001

 Date: 11/05/2025 09:35:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `newsTitle` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `newsId` bigint(20) NOT NULL COMMENT '新闻id',
  `userId` bigint(20) NOT NULL COMMENT '用户id',
  `parentId` bigint(20) NULL DEFAULT NULL COMMENT '父评论id',
  `likeNum` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_newsId`(`newsId`) USING BTREE,
  INDEX `idx_userId`(`userId`) USING BTREE,
  CONSTRAINT `fk_comment_news` FOREIGN KEY (`newsId`) REFERENCES `news` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1921039013092503554 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `coverImage` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片',
  `source` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源',
  `sourceUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源链接',
  `author` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者',
  `category` bigint(20) NOT NULL COMMENT '分类',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态  0：草稿（可编辑）；1：已提交（待审核）；2：审核通过（待发布）；3：已发布；4：审核失败（需要修改）；5：已下架（从发布状态撤回）；6：反馈新闻（待审核）',
  `viewCount` int(11) NOT NULL DEFAULT 0 COMMENT '浏览量',
  `likeCount` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `commentCount` int(11) NOT NULL DEFAULT 0 COMMENT '评论数',
  `notes` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作备注，如拒接审核、用户反馈',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title`) USING BTREE,
  INDEX `idx_category`(`category`) USING BTREE,
  INDEX `idx_author`(`author`) USING BTREE,
  INDEX `idx_source`(`source`) USING BTREE,
  CONSTRAINT `fk_news_tag` FOREIGN KEY (`category`) REFERENCES `news_tag` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1921076943974551554 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '新闻' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for news_feedback
-- ----------------------------
DROP TABLE IF EXISTS `news_feedback`;
CREATE TABLE `news_feedback`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint(20) NOT NULL COMMENT '反馈用户ID',
  `newsId` bigint(20) NULL DEFAULT NULL COMMENT '关联新闻ID（如果是新新闻则为NULL）',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新闻标题',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '反馈内容',
  `reviewNotes` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核备注',
  `reviewTime` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `reviewerId` bigint(20) NULL DEFAULT NULL COMMENT '审核人ID',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`, `isDelete`) USING BTREE,
  INDEX `idx_userId`(`userId`) USING BTREE,
  INDEX `idx_newsId`(`newsId`) USING BTREE,
  CONSTRAINT `fk_news_feedback_news` FOREIGN KEY (`newsId`) REFERENCES `news` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_news_feedback_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '新闻反馈' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for news_push
-- ----------------------------
DROP TABLE IF EXISTS `news_push`;
CREATE TABLE `news_push`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `newsId` bigint(20) NOT NULL COMMENT '新闻ID',
  `pushTime` datetime NOT NULL COMMENT '推送时间',
  `pushType` tinyint(4) NOT NULL COMMENT '推送类型 1-即时推送 2-定时推送',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '推送状态 0-待推送 1-已推送 2-推送失败',
  `isRead` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `readTime` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_userId`(`userId`) USING BTREE,
  INDEX `idx_newsId`(`newsId`) USING BTREE,
  INDEX `idx_pushTime`(`pushTime`) USING BTREE,
  CONSTRAINT `fk_news_push_news` FOREIGN KEY (`newsId`) REFERENCES `news` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_news_push_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 628 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '新闻推送记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for news_tag
-- ----------------------------
DROP TABLE IF EXISTS `news_tag`;
CREATE TABLE `news_tag`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tagName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tagName`(`tagName`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1903048402666127362 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '新闻标签' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word`  (
  `id` bigint(20) NOT NULL,
  `word` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '敏感词',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `word`(`word`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敏感词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `email` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人邮箱(账号)',
  `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `unionId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信开放平台id',
  `openId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信小程序openId',
  `userName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `userAvatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像',
  `userProfile` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户简介',
  `userPhone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户手机号',
  `userRole` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `gender` tinyint(4) NULL DEFAULT 2 COMMENT '0-女，1-男，2-未知',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_unionId`(`openId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1909095734444707843 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_news_view
-- ----------------------------
DROP TABLE IF EXISTS `user_news_view`;
CREATE TABLE `user_news_view`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `newsTitle` varchar(521) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '浏览标题',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `newsId` bigint(20) NOT NULL COMMENT '新闻ID',
  `viewTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`, `userId`) USING BTREE,
  UNIQUE INDEX `uk_user_news_view`(`userId`, `newsId`, `viewTime`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1921036885854760963 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户浏览新闻记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `user_operation_log`;
CREATE TABLE `user_operation_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `operationType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型：VIEW-浏览、LIKE-点赞、COMMENT-评论、FEEDBACK-反馈、SUBSCRIBE-订阅',
  `targetId` bigint(20) NOT NULL COMMENT '操作目标ID',
  `targetType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作目标类型：NEWS-新闻、TAG-标签、COMMENT-评论',
  `operationTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `operationDetail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作详情',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_userId`(`userId`) USING BTREE,
  INDEX `idx_operationTime`(`operationTime`) USING BTREE,
  CONSTRAINT `fk_user_operation_log_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_subscription
-- ----------------------------
DROP TABLE IF EXISTS `user_subscription`;
CREATE TABLE `user_subscription`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint(20) NOT NULL COMMENT '用户id',
  `category` bigint(20) NULL DEFAULT NULL COMMENT '订阅分类',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 0-取消 1-订阅中',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_userId`(`userId`) USING BTREE,
  INDEX `idx_category`(`category`) USING BTREE,
  CONSTRAINT `fk_news_tag_user` FOREIGN KEY (`category`) REFERENCES `news_tag` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_subscription_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1921045658715430916 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户订阅' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
