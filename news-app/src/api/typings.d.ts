export interface News {
  id: number
  title: string
  content: string
  author: string
  createtime: string
  viewcount: number
  likecount?: number
}

export interface Comment {
  id: number
  content: string
  userId: number
  newsId: number
  username: string
  userAvatar: string
  createTime: string
}
