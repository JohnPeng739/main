//app.js
import utils from './utils/util.js'

App({
  globalData: {
    openId: null,
    family: null,
    account: null,
    courses: [{
      code: 'gz',
      name: '工资',
      type: 'INCOME',
      desc: '',
      isPublic: true,
      children: [{
        code: 'gz',
        name: '工资',
        type: 'INCOME',
        desc: '',
        isPublic: true
      }, {
        code: 'jj',
        name: '奖金',
        type: 'INCOME',
        desc: '',
        isPublic: true,
        children: [{
          code: 'yd',
          name: '月度',
          type: 'INCOME',
          desc: '',
          isPublic: true
        }, {
          code: 'jd',
          name: '季度',
          type: 'INCOME',
          desc: '',
          isPublic: true
        }, {
          code: 'nd',
          name: '年终',
          type: 'INCOME',
          desc: '',
          isPublic: true
        }]
      }, {
        code: 'bt',
        name: '补贴',
        type: 'INCOME',
        desc: '',
        isPublic: true
      }, {
        code: 'qt',
        name: '其他',
        type: '',
        desc: '',
        isPublic: true
      }]
    }, {
      code: 'fz',
      name: '房租',
      type: 'INCOME',
      desc: '',
      isPublic: true
    }, {
      code: 'trz',
      name: '投融资',
      type: 'INCOME',
      desc: '',
      isPublic: true
    }, {
      code: 'qt',
      name: '其他',
      type: '',
      desc: '',
      isPublic: true
    }, {
      code: 'sc',
      name: '私车',
      type: 'SPENDING',
      desc: '',
      isPublic: true,
      children: [{
        code: 'sj',
        name: '税金',
        type: 'SPENDING',
        desc: '',
        isPublic: true,
      }, {
        code: 'yf',
        name: '油费',
        type: 'SPENDING',
        desc: '',
        isPublic: true,
      }]
    }, {
      code: 'rckx',
      name: '日常开销',
      type: 'SPENDING',
      desc: '',
      isPublic: true,
      children: [{
        code: 'sdmq',
        name: '水电煤气',
        type: 'SPENDING',
        desc: '',
        isPublic: true,
      }, {
        code: 'shf',
        name: '生活费',
        type: 'SPENDING',
        desc: '',
        isPublic: true,
      }]
    }, {
      code: 'ly',
      name: '旅游',
      type: 'SPENDING',
      desc: '',
      isPublic: true
    }],
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
          console.log(openId)
        }, () => {
          utils.error('使用login代码解码失败。')
        })
      }
    })
  }
})