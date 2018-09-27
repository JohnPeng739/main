// pages/setting/setting.js
import utils from '../../utils/util.js'

const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    account: {},
    gender: 'MALE'
  },
  genderChange: function(e) {
    this.setData({
      gender: e.detail.value
    })
  },
  bindAvatarChange: function(e) {
    wx.chooseImage({
      success: function(res) {
        console.log(res.tempFilePaths)
      },
    })
  },
  formSubmit: function (e) {
    let form = e.detail.value
    form.gender = this.data.gender
    form.id = this.data.account.id
    utils.put('accounts/' + form.id, form, function(res) {
      console.log(res)
      if (res.data.errorCode === 0) {
        app.globalData.account = res.data.data
        wx.navigateBack({})
        utils.info('保存个人账户信息成功。')
      } else {
        utils.error(res.data.errorMessage)
      }
    })
  },
  formReset: function () {
    this.setData({
      account: app.globalData.account,
      gender: app.globalData.account.gender
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let {account, family} = app.globalData
    if (family && family.name) {
      wx.setNavigationBarTitle({
        title: family.name,
      })
    }
    this.setData({
      account: account,
      gender: account.gender
    })
  }
})