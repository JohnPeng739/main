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
          url: 'https://mx-john.cn/rest/v1/wx/decode',
          data: {
            code: res.code
          },
          method: 'GET',
          success: function(decodeRes) {
            let openId = decodeRes.data.openid
            me.globalData.openId = openId
            wx.request({
              url: 'https://mx-john.cn/rest/v1/family',
              data: {
                userCode: openId
              },
              method: 'GET',
              success: function(familyRes) {
                if (familyRes.data.errorCode === 0) {
                  me.globalData.family = familyRes.data.data
                }
              },
              fail: function() {
                console.log('get family fail.')
              }
            })
          },
          fail: function(decodeRes) {
            console.log('使用login代码解码失败。')
          }
        })
      }
    })
  }
})