/**
 * @description 消息提示框封装：传入参数则覆盖默认参数，未传入则使用默认参数
 * @param {object} param0 和wx.showToast参数一致
 */
export const toast = ({ title = "数据加载中...", icon = 'none', duration = 2000, mask = true } = {}) => {
  wx.showToast({
    title,
    icon,
    duration,
    mask
  })
}

/**
 * 
 * @description 模态对话框封装：传入参数则覆盖默认参数，未传入则使用默认参数 confirm：确认，返回true;   cancel：取消，返回false
 * @param {object} param0 和wx.showModal参数一致
 */
export const modal = (options = {}) => {
  const defaults = { title: "提示", content: "您确认执行该操作吗？" }
  options = { ...defaults, ...options }
  return new Promise((resolve) => {
    wx.showModal({
      ...options,
      confirmColor: "#f3514f", // 确认改为红色
      complete: ({ cancel, confirm }) => {
        cancel && resolve(false)
        confirm && resolve(true)
      }
    })
  })
}

// 挂载到全局对象
wx.toast = toast
wx.modal = modal
