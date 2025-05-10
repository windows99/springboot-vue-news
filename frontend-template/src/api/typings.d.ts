declare namespace API {
  type addSensitiveWordUsingPOSTParams = {
    /** word */
    word?: string;
  };

  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseComment_ = {
    code?: number;
    data?: Comment;
    message?: string;
  };

  type BaseResponseDashboardVO_ = {
    code?: number;
    data?: DashboardVO;
    message?: string;
  };

  type BaseResponseInt_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseListComment_ = {
    code?: number;
    data?: Comment[];
    message?: string;
  };

  type BaseResponseListMapStringObject_ = {
    code?: number;
    data?: Record<string, any>[];
    message?: string;
  };

  type BaseResponseListNews_ = {
    code?: number;
    data?: News[];
    message?: string;
  };

  type BaseResponseListNewsFeedback_ = {
    code?: number;
    data?: NewsFeedback[];
    message?: string;
  };

  type BaseResponseListNewsPushDTO_ = {
    code?: number;
    data?: NewsPushDTO[];
    message?: string;
  };

  type BaseResponseListNewsTag_ = {
    code?: number;
    data?: NewsTag[];
    message?: string;
  };

  type BaseResponseListSensitiveWord_ = {
    code?: number;
    data?: SensitiveWord[];
    message?: string;
  };

  type BaseResponseListUserNewsView_ = {
    code?: number;
    data?: UserNewsView[];
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseNews_ = {
    code?: number;
    data?: News;
    message?: string;
  };

  type BaseResponseNewsFeedback_ = {
    code?: number;
    data?: NewsFeedback;
    message?: string;
  };

  type BaseResponseNewsPush_ = {
    code?: number;
    data?: NewsPush;
    message?: string;
  };

  type BaseResponseNewsTag_ = {
    code?: number;
    data?: NewsTag;
    message?: string;
  };

  type BaseResponseObject_ = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponsePageNews_ = {
    code?: number;
    data?: PageNews_;
    message?: string;
  };

  type BaseResponsePageNewsFeedbackVO_ = {
    code?: number;
    data?: PageNewsFeedbackVO_;
    message?: string;
  };

  type BaseResponsePageNewsPushVO_ = {
    code?: number;
    data?: PageNewsPushVO_;
    message?: string;
  };

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };

  type BaseResponsePageUserOperationLog_ = {
    code?: number;
    data?: PageUserOperationLog_;
    message?: string;
  };

  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type Comment = {
    content?: string;
    createTime?: string;
    id?: number;
    isDelete?: number;
    likeNum?: number;
    newsId?: number;
    newsTitle?: string;
    parentId?: number;
    updateTime?: string;
    userAvatar?: string;
    userId?: number;
    username?: string;
  };

  type DashboardVO = {
    categoryStats?: Record<string, any>[];
    hotNews?: Record<string, any>[];
    newsTrend?: Record<string, any>[];
    systemStatus?: Record<string, any>;
    tagStats?: Record<string, any>[];
    todayNews?: number;
    todayUsers?: number;
    totalNews?: number;
    totalUsers?: number;
    userActivity?: Record<string, any>[];
  };

  type deleteAllViewsByUserIdUsingPOSTParams = {
    /** userId */
    userId: number;
  };

  type deleteCommentByIdUsingDELETEParams = {
    /** id */
    id: number;
  };

  type deleteFeedbackUsingPOSTParams = {
    /** id */
    id: number;
  };

  type deleteFileUsingDELETEParams = {
    /** fileKey */
    fileKey: string;
  };

  type deleteNewsUsingDELETEParams = {
    /** id */
    id: number;
  };

  type DeleteRequest = {
    id?: number;
  };

  type deleteSensitiveWordUsingDELETEParams = {
    /** id */
    id: number;
  };

  type deleteTagUsingDELETEParams = {
    /** id */
    id: number;
  };

  type deleteViewByIdUsingPOSTParams = {
    /** userId */
    userId?: number;
    /** viewId */
    viewId: number;
  };

  type getCommentByIdUsingGETParams = {
    /** id */
    id: number;
  };

  type getCommentListUsingGETParams = {
    /** author */
    author?: string;
    /** newsId */
    newsId?: number;
    /** page */
    page?: number;
    /** pageSize */
    pageSize?: number;
  };

  type getFeedbackDetailUsingGETParams = {
    /** id */
    id: number;
  };

  type getHotNewsUsingGETParams = {
    /** current */
    current?: number;
    /** pageSize */
    pageSize?: number;
  };

  type getJisunewsUsingGETParams = {
    /** channel */
    channel?: string;
  };

  type getNewsByIdUsingGETParams = {
    /** id */
    id: number;
    /** userId */
    userId?: number;
  };

  type getOperationHistoryUsingGETParams = {
    /** current */
    current?: number;
    /** operationType */
    operationType?: string;
    /** pageSize */
    pageSize?: number;
    /** targetType */
    targetType?: string;
    /** userId */
    userId?: number;
  };

  type getPendingFeedbackListUsingGETParams = {
    /** current */
    current?: number;
    /** pageSize */
    pageSize?: number;
  };

  type getPushRecordDetailUsingGETParams = {
    /** recordId */
    recordId: number;
  };

  type getRecommendForUserUsingGETParams = {
    /** current */
    current?: number;
    /** pageSize */
    pageSize?: number;
    /** userId */
    userId: number;
  };

  type getTagListUsingGETParams = {
    /** page */
    page?: number;
    /** pageSize */
    pageSize?: number;
  };

  type getTagUsingGETParams = {
    /** id */
    id: number;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getViewHistoryUsingGETParams = {
    /** params */
    params: Record<string, any>;
    /** userId */
    userId: number;
  };

  type isSubscribedUsingGETParams = {
    /** tagId */
    tagId: number;
  };

  type listPushRecordsUsingGETParams = {
    /** current */
    current?: number;
    /** isRead */
    isRead?: number;
    /** pageSize */
    pageSize?: number;
  };

  type LoginUserVO = {
    createTime?: string;
    email?: string;
    gender?: number;
    id?: number;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type News = {
    author?: string;
    category?: number;
    commentCount?: number;
    content?: string;
    coverImage?: string;
    createtime?: string;
    id?: number;
    isdelete?: number;
    likeCount?: number;
    notes?: string;
    source?: string;
    sourceUrl?: string;
    status?: number;
    title?: string;
    updateTime?: string;
    viewcount?: number;
  };

  type NewsAddRequest = {
    author?: string;
    category?: number;
    content?: string;
    coverImage?: string;
    source?: string;
    status?: number;
    tagIds?: number[];
    title?: string;
    userId?: number;
  };

  type NewsFeedback = {
    content?: string;
    createTime?: string;
    id?: number;
    isDelete?: number;
    newsId?: number;
    reviewNotes?: string;
    reviewTime?: string;
    reviewerId?: number;
    title?: string;
    updateTime?: string;
    userId?: number;
  };

  type NewsFeedbackRequest = {
    content: string;
    newsId: number;
  };

  type NewsFeedbackReviewRequest = {
    approved?: boolean;
    feedbackId?: number;
    reviewNotes?: string;
  };

  type NewsFeedbackVO = {
    content?: string;
    createTime?: string;
    id?: number;
    isDelete?: number;
    newsId?: number;
    newsTitle?: string;
    reviewNotes?: string;
    reviewTime?: string;
    reviewerId?: number;
    title?: string;
    updateTime?: string;
    userId?: number;
    username?: string;
  };

  type NewsPush = {
    createTime?: string;
    id?: number;
    isDelete?: number;
    isRead?: number;
    newsId?: number;
    pushTime?: string;
    pushType?: number;
    readTime?: string;
    status?: number;
    updateTime?: string;
    userId?: number;
  };

  type NewsPushDTO = {
    author?: string;
    category?: string;
    commentCount?: number;
    content?: string;
    coverImage?: string;
    createTime?: string;
    isDelete?: number;
    likeCount?: number;
    newsId?: string;
    publishTime?: string;
    source?: string;
    status?: number;
    title?: string;
    updateTime?: string;
    userId?: string;
    viewCount?: number;
  };

  type NewsPushVO = {
    newsCoverImage?: string;
    newsPush?: NewsPush;
    newsTitle?: string;
  };

  type NewsQueryRequest = {
    author?: string;
    category?: number;
    content?: string;
    current?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    source?: string;
    status?: number;
    title?: string;
  };

  type NewsTag = {
    createtime?: string;
    id?: number;
    isdelete?: number;
    tagname?: string;
    updatetime?: string;
  };

  type NewsUpdateRequest = {
    author?: string;
    category?: number;
    content?: string;
    coverImage?: string;
    id?: number;
    source?: string;
    status?: number;
    tagIds?: number[];
    title?: string;
    userId?: number;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageNews_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: News[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageNewsFeedbackVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: NewsFeedbackVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageNewsPushVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: NewsPushVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUser_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: User[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserOperationLog_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserOperationLog[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type publishNewsUsingPUTParams = {
    /** id */
    id: number;
  };

  type pushImmediatelyUsingPOSTParams = {
    /** newsId */
    newsId: number;
    /** userIds */
    userIds?: number[];
  };

  type recordViewUsingPOSTParams = {
    /** newsId */
    newsId: number;
    /** newsTitle */
    newsTitle: string;
    /** userId */
    userId: number;
  };

  type SensitiveWord = {
    createtime?: string;
    id?: number;
    status?: number;
    updatetime?: string;
    word?: string;
  };

  type setStatusNewsUsingPUTParams = {
    /** id */
    id: number;
    /** statusInt */
    statusInt: number;
  };

  type shelfNewsUsingPUTParams = {
    /** id */
    id: number;
  };

  type updateNewsUsingPUTParams = {
    /** id */
    id: number;
  };

  type updateSensitiveWordUsingPUTParams = {
    /** id */
    id: number;
  };

  type updateTagUsingPUTParams = {
    /** id */
    id: number;
  };

  type uploadFileUsingPOSTParams = {
    biz?: string;
  };

  type User = {
    createTime?: string;
    email?: string;
    gender?: number;
    id?: number;
    isDelete?: number;
    openId?: string;
    unionId?: string;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    email?: string;
    gender?: number;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type userLoginByWxMiniUsingGETParams = {
    /** code */
    code: string;
  };

  type UserLoginRequest = {
    email?: string;
    userPassword?: string;
  };

  type UserNewsView = {
    id?: number;
    newsId?: number;
    newsTitle?: string;
    userId?: number;
    viewTime?: string;
  };

  type UserOperationLog = {
    createTime?: string;
    id?: number;
    isDelete?: number;
    operationDetail?: string;
    operationTime?: string;
    operationType?: string;
    targetId?: number;
    targetType?: string;
    updateTime?: string;
    userId?: number;
  };

  type UserPasswordResetRequest = {
    checkCode?: string;
    checkPassword?: string;
    email?: string;
    userPassword?: string;
  };

  type UserPasswordUpdateRequest = {
    checkCode?: string;
    checkPassword?: string;
    email?: string;
    oldPassword?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    current?: number;
    email?: string;
    gender?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    checkCode?: string;
    checkPassword?: string;
    email?: string;
    userPassword?: string;
  };

  type UserUpdateMyRequest = {
    email?: string;
    gender?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
  };

  type UserUpdateRequest = {
    email?: string;
    gender?: number;
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    createTime?: string;
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };
}
