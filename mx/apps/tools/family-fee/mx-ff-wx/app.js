//app.js
import utils from './utils/util.js'

App({
  globalData: {
    openId: null,
    family: null,
    account: null,
    courses: [],
    tabBar: {
      color: "#999999",
      selectedColor: "#0ABA07",
      borderStyle: "white",
      backgroundColor: "#f7f7fa",
      list: [{
        pagePath: "/pages/my-family/my-family",
        text: "我的家",
        iconPath: "/images/my-family.png",
        selectedIconPath: "/images/my-family-sel.png",
        selected: true
      }, {
        pagePath: "/pages/spending/spending",
        text: "搞腐败",
        iconPath: "/images/spending.png",
        selectedIconPath: "/images/spending-sel.png",
        selected: false
      }, {
        pagePath: "/pages/income/income",
        text: "抓创收",
        iconPath: "/images/income.png",
        selectedIconPath: "/images/income-sel.png",
        selected: false
      }, {
        tabFunc: function() {
          let items = ['编项目', '做预算']
          let pages = ['/pages/course/course', '/pages/budget/budget']
          wx.showActionSheet({
            itemList: items,
            success: function(res) {
              wx.navigateTo({
                url: pages[res.tapIndex],
              })
            }
          })
        },
        text: "...",
        iconPath: "/images/more.png",
        selectedIconPath: "/images/more.png",
        selected: false
      }],
      position: "bottom"
    }
  },
  editTabBar: function() {
    let tabBar = this.globalData.tabBar
    let currentPages = getCurrentPages()
    let _this = currentPages[currentPages.length - 1]
    let pagePath = _this.__route__

    if (pagePath.indexOf('/') != 0) {
      pagePath = '/' + pagePath
    }

    for (var i in tabBar.list) {
      tabBar.list[i].selected = false;
      (tabBar.list[i].pagePath == pagePath) && (tabBar.list[i].selected = true)
    }
    _this.setData({
      tabBar: tabBar
    })
  },
  onLaunch: function() {
    // 登录
    let me = this
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
        utils.get('wx/decode', {
          code: res.code
        }, decodeRes => {
          let openId = decodeRes.data.openid
          me.globalData.openId = openId
        }, () => {
          utils.error('使用login代码解码失败。')
        })
      }
    })
  }
})