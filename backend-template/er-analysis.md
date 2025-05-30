# 新闻系统数据库ER图结构分析

## 核心实体

### 1. 用户(User)
- 主键：id
- 核心属性：
  - email：用户邮箱(账号)
  - userPassword：密码
  - userName：用户昵称
  - userAvatar：用户头像
  - userRole：用户角色(user/admin/ban)
  - gender：性别(0-女，1-男，2-未知)
- 关联关系：
  - 一对多：News(发布新闻)
  - 一对多：Comment(发表评论)
  - 一对多：NewsFeedback(提交反馈)
  - 一对多：NewsPush(接收推送)
  - 一对多：UserSubscription(订阅分类)
  - 一对多：UserNewsView(浏览记录)
  - 一对多：UserOperationLog(操作日志)

### 2. 新闻(News)
- 主键：id
- 核心属性：
  - title：标题
  - content：内容
  - coverImage：封面图片
  - source：来源
  - author：作者
  - status：状态(0-草稿，1-已提交，2-审核通过，3-已发布，4-审核失败，5-已下架，6-反馈新闻)
  - viewCount：浏览量
  - likeCount：点赞数
  - commentCount：评论数
- 关联关系：
  - 多对一：NewsTag(所属分类)
  - 一对多：Comment(包含评论)
  - 一对多：NewsFeedback(关联反馈)
  - 一对多：NewsPush(推送记录)
  - 一对多：UserNewsView(浏览记录)

### 3. 新闻标签(NewsTag)
- 主键：id
- 核心属性：
  - tagName：标签名称
- 关联关系：
  - 一对多：News(分类下的新闻)
  - 一对多：UserSubscription(被用户订阅)

### 4. 评论(Comment)
- 主键：id
- 核心属性：
  - content：评论内容
  - newsId：新闻id
  - userId：用户id
  - parentId：父评论id
  - likeNum：点赞数
- 关联关系：
  - 多对一：News(所属新闻)
  - 多对一：User(评论用户)
  - 自关联：Comment(评论回复)

### 5. 新闻反馈(NewsFeedback)
- 主键：id
- 核心属性：
  - userId：反馈用户ID
  - newsId：关联新闻ID
  - title：新闻标题
  - content：反馈内容
  - reviewNotes：审核备注
  - reviewerId：审核人ID
- 关联关系：
  - 多对一：User(反馈用户)
  - 多对一：News(关联新闻)

### 6. 新闻推送(NewsPush)
- 主键：id
- 核心属性：
  - userId：用户ID
  - newsId：新闻ID
  - pushTime：推送时间
  - pushType：推送类型(1-即时推送，2-定时推送)
  - status：推送状态(0-待推送，1-已推送，2-推送失败)
  - isRead：是否已读
- 关联关系：
  - 多对一：User(接收用户)
  - 多对一：News(推送新闻)

### 7. 用户订阅(UserSubscription)
- 主键：id
- 核心属性：
  - userId：用户id
  - category：订阅分类
  - status：状态(0-取消，1-订阅中)
- 关联关系：
  - 多对一：User(订阅用户)
  - 多对一：NewsTag(订阅分类)

### 8. 用户浏览记录(UserNewsView)
- 主键：id
- 核心属性：
  - userId：用户ID
  - newsId：新闻ID
  - viewTime：浏览时间
- 关联关系：
  - 多对一：User(浏览用户)
  - 多对一：News(浏览新闻)

### 9. 用户操作日志(UserOperationLog)
- 主键：id
- 核心属性：
  - userId：用户ID
  - operationType：操作类型(VIEW/LIKE/COMMENT/FEEDBACK/SUBSCRIBE)
  - targetId：操作目标ID
  - targetType：操作目标类型(NEWS/TAG/COMMENT)
  - operationDetail：操作详情
- 关联关系：
  - 多对一：User(操作用户)

### 10. 敏感词(SensitiveWord)
- 主键：id
- 核心属性：
  - word：敏感词
  - status：状态(1-有效，0-无效)

## 主要业务关系

1. 用户-新闻关系：
   - 用户可以发布新闻
   - 用户可以浏览新闻
   - 用户可以评论新闻
   - 用户可以反馈新闻

2. 新闻-分类关系：
   - 每条新闻属于一个分类
   - 用户可以订阅特定分类

3. 评论系统：
   - 支持多级评论
   - 评论可以点赞
   - 评论关联用户和新闻

4. 推送系统：
   - 支持即时推送和定时推送
   - 记录推送状态和阅读状态

5. 用户行为追踪：
   - 记录用户浏览历史
   - 记录用户操作日志
   - 支持用户订阅管理

## 数据完整性约束

1. 外键约束：
   - 评论关联新闻和用户
   - 新闻关联分类
   - 用户订阅关联分类
   - 推送记录关联用户和新闻

2. 唯一性约束：
   - 用户邮箱唯一
   - 敏感词唯一
   - 用户浏览记录唯一(用户+新闻+时间)

3. 默认值约束：
   - 创建时间默认当前时间
   - 更新时间自动更新
   - 删除标记默认0
   - 状态字段默认值设置 