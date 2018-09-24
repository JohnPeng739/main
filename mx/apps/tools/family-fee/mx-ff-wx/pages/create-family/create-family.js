// pages/create-family/create-family.js
import utils from '../../utils/util.js'

let app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    account: {},
    role: 0,
    roles: ["皇帝", "皇后", "王子", "公主", "太上皇", "其他"]
  },

  formSubmit: function(e) {
    let form = e.detail.value
    utils.post('families/new', {
      openId: app.globalData.openId,
      name: form.familyName,
      desc: form.familyDesc,
      // ownerRole: this.data.roles[form.role], 
      avatarUrl: ''
    }, res => {
      if (res.data.errorCode === 0) {
        app.globalData.family = res.data.data
        wx.reLaunch({
          url: '../index/index'
        })
        utils.info('创建家庭[' + res.data.data.name + ']成功！')
      } else {
        utils.error(res.data.errorMessage)
      }
    }, () => {
      utils.error('创建家庭失败。')
    })
  },

  formReset: function() {
    this.setData({
      role: 0
    })
  },

  bindRoleChange: function(e) {
    this.setData({
      role: e.detail.value
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      account: getApp().globalData.account
    })
  }
})