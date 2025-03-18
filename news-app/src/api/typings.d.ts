declare namespace API {
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

  type BaseResponseListComment_ = {
    code?: number;
    data?: Comment[];
    message?: string;
  };

  type BaseResponseListNewsTag_ = {
    code?: number;
    data?: NewsTag[];
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

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
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
    parentId?: number;
    updateTime?: string;
    userAvatar?: string;
    userId?: number;
    username?: string;
  };

  type deleteAllViewsByUserIdUsingPOSTParams = {
    /** userId */
    userId: number;
  };

  type deleteCommentByIdUsingDELETEParams = {
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
    category?: string;
    commentcount?: number;
    content?: string;
    coverimage?: string;
    createtime?: string;
    id?: number;
    images?: string;
    isdelete?: number;
    likecount?: number;
    source?: string;
    sourceurl?: string;
    status?: number;
    title?: string;
    updatetime?: string;
    viewcount?: number;
  };

  type NewsQueryRequest = {
    author?: string;
    category?: number;
    current?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
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

  type recordViewUsingPOSTParams = {
    /** newsId */
    newsId: number;
    /** newsTitle */
    newsTitle: string;
    /** userId */
    userId: number;
  };

  type shelfNewsUsingPUTParams = {
    /** id */
    id: number;
  };

  type updateNewsUsingPUTParams = {
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
