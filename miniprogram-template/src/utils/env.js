const { miniProgram } = wx.getAccountInfoSync()

// 获取小程序版本
const { envVersion } = miniProgram

let env = {
  baseURL: "http://localhost:8101/api"
}

switch (envVersion) {
  // 开发版
  case 'develop': 
    env.baseURL = 'http://localhost:8101/api'
    break;
  // 体验版
  case 'trial':
    env.baseURL = 'https://体验版请求地址'
    break;
  // 正式版
  case 'release':
    env.baseURL = 'https://正式版版请求地址'
    break;
  // 默认
  default:
    env.baseURL = 'https://默认请求地址'
    break;
}

export { env }