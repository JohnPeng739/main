//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    userInfo: {},
    family: {},
    hasFamily: false,
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    nodes: '<p class="p">要使用<span class="important">家庭账单</span>微信小程序，需要使用到你的微信公共信息，比如：你的昵称、账户、位置信息。</p><p class="p"><span class="notice">应用确保不会将这些信息用于系统之外的用途！</span></p>'
  },
  //事件处理函数
  onCreateFamily: function () {
    wx.navigateTo({
      url: '../create-family/create-family',
    })
  },
  onJoinFamily: function () {
    let me = this
    wx.scanCode({
      onlyFromCamera: false,
      scanType: ['barCode', 'qrCode', 'datamatrix', 'pdf417'],
      success: function(res) {
        let {scanType, charSet, path, result, rawData} = res
        let {id, familyName} = JSON.parse(result)
        wx.showModal({
          title: '请确认',
          content: '您真要加入到家庭“' + familyName + '“吗？',
          confirmText: '是',
          showCancel: true,
          cancelText: '否',
          success: function(confirmRes) {
            if (confirmRes.confirm) {
              // TODO 确认加入家庭
              let family = { id: id, name: familyName }
              app.globalData.family = family
              me.setData({
                family: family,
                hasFamily: true
              })
              wx.setNavigationBarTitle({
                title: familyName,
              })
              console.log(familyName)
            } else {
              // 取消加入家庭
              console.log('cancel')
            }
          }
        })
      },
      fail: function(e) {
        wx.showToast({
          title: '扫码加入指定家庭失败！',
          icon: 'none',
          duration: 3000
        })
      }
    })
  },
  onLoad: function() {
    if (app.globalData.family) {
      this.setData({
        family: app.globalData.family,
        hasFamily: true
      })
      wx.setNavigationBarTitle({
        title: app.globalData.family.name,
      })
    }
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true
      })
    } else if (this.data.canIUse) {
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        this.setUserInfo(res.userInfo)
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          this.setUserInfo(res.userInfo)
        }
      })
    }
  },
  getUserInfo: function (e) {
    this.setUserInfo(e.detail.userInfo)
  },
  setUserInfo: function(userInfo) {
    if (userInfo) {
      app.globalData.userInfo = userInfo
      this.setData({
        userInfo: userInfo,
        hasUserInfo: true
      })
      // TODO registry user
    }
  }
})