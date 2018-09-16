//app.js
App({
  globalData: {
    openId: null,
    userInfo: null,
    family: null
  },
  onLaunch: function () {
    /*
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
    */

    // 登录
    let me = this;
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
        wx.request({
          url: 'https://mx-john.cn/rest/v1/wx/decode?code=' + res.code,
          method: 'GET',
          success: function(decodeRes) {
            me.globalData.openId = decodeRes.data.openid
            // TODO 根据openid获取家庭
          },
          fail: function(decodeRes) {
            console.log('使用login代码解码失败。')
          }
        })
      }
    })
  }
})