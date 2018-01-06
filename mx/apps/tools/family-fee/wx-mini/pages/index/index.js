//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    motto: '使用Family Fee，需要获取你的微信公开信息：昵称、头像等。',
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  //事件处理函数
  gotoPage: function (e) {
    wx.navigateTo({
      url: e.target.id
    })
  },
  onLoad: function () {
    if (app.globalData.userInfo) {
      let data = this.data
      data.userInfo = app.globalData.userInfo
      data.hasUserInfo = true
      this.setData(data)
    } else if (this.data.canIUse) {
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        let data = this.data
        data.userInfo = res.userInfo
        data.hasUserInfo = true
        this.setData(data)
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          let data = this.data
          data.userInfo = app.globalData.userInfo
          data.hasUserInfo = true
          this.setData(data)
        }
      })
    }
    // 后台获取家庭信息
    let me = this
    wx.getStorage({
      key: 'myFamily',
      success: function (res) {
        let data = me.data
        data.myFamily = {
          name: res.data.name,
          owner: {
            nickName: '上善若水',
            avatarUrl: 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqXGb80ZKOdmqaJpFvfPOIsRq0dszcMysBicPic8e0VIh4WajXR34CyV2qhdDzvdqybWnpvFT4leictw/0'
          },
          members: [{
            nickName: 'abc',
            avatarUrl: 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqXGb80ZKOdmqaJpFvfPOIsRq0dszcMysBicPic8e0VIh4WajXR34CyV2qhdDzvdqybWnpvFT4leictw/0'
          }]
        }
        me.setData(data)
        app.globalData.myFamily = data.myFamily
      },
    })
  },
  getUserInfo: function (e) {
    if (e.detail.userInfo) {
      app.globalData.userInfo = e.detail.userInfo
      let data = this.data
      data.userInfo = app.globalData.userInfo
      data.hasUserInfo = true
      this.setData(data)
    }
  }
})
