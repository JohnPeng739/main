//index.js
import utils from '../../utils/util.js'

//获取应用实例
const app = getApp()

Page({
  data: {
    hasReady: false,
    account: {},
    family: {},
    hasAccount: false,
    hasFamily: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    nodes: '<p class="p">要使用<span class="important">家庭账单</span>微信小程序，需要使用到你的微信公共信息，比如：你的昵称、账户、位置信息。</p><p class="p"><span class="notice">应用确保不会将这些信息用于系统之外的用途！</span></p>'
  },
  init: function() {
    wx.hideLoading()
    wx.hideTabBar()
    let {account, family, hasFamily} = this.data
    if (hasFamily) {
      app.globalData.account = account
      app.globalData.family = family
      wx.setNavigationBarTitle({
        title: family.name,
      })
      wx.showTabBar()
      wx.redirectTo({
        url: '/pages/my-family/my-family',
      })
      // TODO 暂时关闭后台访问科目
      /*
      utils.get('courses/families/' + family.id, {userCode: app.globalData.openId}, res => {
        if (res.data.errorCode === 0) {
          app.globalData.courses = res.data.data
        } else {
          utils.error(res.data.errorMessage)
        }
      })
      */
    }
    if (account) {
      // do nothing
    } else if (this.data.canIUse) {
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        // TODO
        console.log(res)
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          // TODO
          console.log(res)
        }
      })
    }
    if (account && family) {
      wx.showTabBar()
    }
    console.log('init finish')
  },
  //事件处理函数
  onCreateFamily: function() {
    wx.navigateTo({
      url: '../create-family/create-family',
    })
  },
  onJoinFamily: function() {
    let me = this
    wx.scanCode({
      onlyFromCamera: false,
      scanType: ['barCode', 'qrCode', 'datamatrix', 'pdf417'],
      success: function(res) {
        let {
          scanType,
          charSet,
          path,
          result,
          rawData
        } = res
        let {
          id,
          familyName
        } = JSON.parse(result)
        wx.showModal({
          title: '请确认',
          content: '您真要加入到家庭“' + familyName + '“吗？',
          confirmText: '是',
          showCancel: true,
          cancelText: '否',
          success: function(confirmRes) {
            if (confirmRes.confirm) {
              // 确认加入家庭
              utils.put('families/' + id + '/join', {
                familyId: id,
                role: '',
                accountId: app.globalData.account.id
              }, joinRes = {
                // TODO 处理加入家庭
              }, () => {
                utils.error('加入指定家庭失败。')
              })
            } else {
              // 取消加入家庭
              console.log('cancel')
            }
          }
        })
      },
      fail: function(e) {
        utils.error('扫码加入指定家庭失败！')
      }
    })
  },
  onLoad: function() {
    wx.setNavigationBarTitle({
      title: '家庭账单',
    })
    wx.hideTabBar()
    wx.showLoading({
      title: '拼命加载中......',
    })
    let me = this
    setTimeout(() => {
      utils.get('account/summary', { userCode: getApp().globalData.openId }, res => {
        if (res.data.errorCode === 0) {
          let data = res.data
          if (data.data) {
            let { account, family } = data.data
            me.setData({
              account: account,
              family: family,
              hasAccount: account !== null && account !== undefined,
              hasFamily: family !== null && family !== undefined
            })
          }
        } else {
          me.setData({
            hasAccount: false,
            hasFamily: false
          })
          utils.error(rs.data.errorMessage)
        }
        me.init()
        wx.hideLoading()
      }, () => {
        utils.error('获取指定openid的账户和家庭失败。')
        })
      me.setData({
        hasReady: true
      })
    }, 1000)
  },
  getUserInfo: function(e) {
    let me = this
    let userInfo = e.detail.userInfo
    utils.post('accounts/registry', {
      openId: app.globalData.openId,
      unionId: '',
      nickname: userInfo.nickName,
      avatarUrl: userInfo.avatarUrl,
      country: userInfo.country,
      province: userInfo.province,
      city: userInfo.city
    }, res => {
      if (res.data.errorCode === 0) {
        let account = res.data.data
        app.globalData.account = account
        me.setData({
          account: account,
          hasAccount: true
        })
      }
    }, () => {
      utils.error('注册账户失败。')
    })
  }
})