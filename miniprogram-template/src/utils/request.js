// 通用请求封装
class WxRequest {

  // 默认配置参数
  defaults = {
    baseURL: "", // 后端服务地址
    url: "", // 接口请求路径
    data: null, // 请求参数
    method: "GET", // 默认请求方法
    // 请求头
    header: {
      "Content-type": "application/json"
    },
    timeout: 6000, // 小程序默认超时时间是60000ms，一分钟
    isLoading: true // 使用默认的loading效果
  }

  // 默认拦截器对象
  interceptors = {
    // 请求拦截器
    request: (config) => config,
    // 响应拦截器
    response: (response) => response
  }

  // 请求队列，存储请求表示，解决loading闪烁问题
  queue = []

  constructor(params = {}) {
    // 合并配置参数
    this.defaults = Object.assign({}, this.defaults, params)
  }

  /**
   * @description 实际处理请求，文件上传&网络请求
   * @param {*} options 参数项
   */
  request(options) {

    // 解决并发请求loading闪烁问题
    this.timerId && clearTimeout(this.timerId)

    // 拼接获取完整url（baseURL+url）
    options.url = this.defaults.baseURL + options.url

    // 合并参数
    options = { ...this.defaults, ...options }

    if (options.isLoading && options.method !== 'UPLOAD') {
      // queue为空则显示loading
      this.queue.length === 0 && wx.showLoading({ title: '加载中' })
      this.queue.push("request")
    }

    // 调用请求拦截器，新增或修改请求参数
    options = this.interceptors.request(options)

    return new Promise((resolve, reject) => {
      if (options.method === 'UPLOAD') {
        // 文件请求
        wx.uploadFile({
          ...options,
          success: (res) => {
            res.data = JSON.parse(res.data)
            const mergeRes = Object.assign({}, res,
              {
                config: options,
                isSuccess: true
              }
            )
            resolve(this.interceptors.response(mergeRes))
          },
          fail: (err) => {
            const mergeErr = Object.assign({}, err,
              {
                config: options,
                isSuccess: false
              }
            )
            reject(this.interceptors.response(mergeErr))
          }
        })
      } else {
        // 网络请求
        wx.request({
          ...options,
          success: (res) => {
            // 响应拦截器，处理响应结果
            const mergeRes = Object.assign({}, res, { config: options, isSuccess: true })
            resolve(this.interceptors.response(mergeRes))
          },
          fail: (err) => {
            // 响应拦截器，处理响应结果
            const mergeErr = Object.assign({}, err, { config: options, isSuccess: false })
            reject(this.interceptors.response(mergeErr))
          },
          complete: () => {
            if (options.isLoading) {
              this.queue.pop()
              // queue为空则隐藏loading
              this.queue.length === 0 && this.queue.push('request')

              // 解决并发请求loading闪烁问题
              this.timerId = setTimeout(() => {
                this.queue.pop()
                this.queue.length === 0 && wx.hideLoading()
                clearTimeout(this.timerId)
              }, 1);
            }
          }
        })
      }
    })
  }

  /**
 * @description 封装 GET 方法
 * @param {*} url 请求路径
 * @param {*} data 参数
 * @param {*} config 其他配置项
 */
  get(url, data = {}, config = {}) {
    return this.request(Object.assign({ url, data, method: "GET" }, config))
  }

  /**
 * @description 封装 POST 方法
 * @param {*} url 请求路径
 * @param {*} data 参数
 * @param {*} config 其他配置项
 */
  post(url, data = {}, config = {}) {
    return this.request(Object.assign({ url, data, method: "POST" }, config))
  }

  /**
 * @description 封装 PUT 方法
 * @param {*} url 请求路径
 * @param {*} data 参数
 * @param {*} config 其他配置项
 */
  put(url, data = {}, config = {}) {
    return this.request(Object.assign({ url, data, method: "PUT" }, config))
  }

  /**
   * @description 封装 DETELE 方法
   * @param {*} url 请求路径
   * @param {*} data 参数
   * @param {*} config 其他配置项
   */
  delete(url, data = {}, config = {}) {
    return this.request(Object.assign({ url, data, method: "DETELE" }, config))
  }

  /**
   * @description 封装upload实例方法
   * @param {*} url 文件上传地址
   * @param {*} filePath 要上传的文件资源路径
   * @param {*} name 文件对应的key
   * @param {*} config 其他配置项
   */
  upload(url, filePath, name = "file", config = {}) {
    return this.request(
      Object.assign({ url, filePath, name, method: 'UPLOAD' }, config)
    )
  }

  /**
   * @description 处理并发请求
   * @param  {...any} promise 请求列表
   */
  all(...promise) {
    // 传递请求数组，返回请求结果数组
    return Promise.all(promise);
  }

}

export default WxRequest;