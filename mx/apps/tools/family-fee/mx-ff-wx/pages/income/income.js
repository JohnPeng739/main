// pages/income/income.js
import utils from '../../utils/util.js'

const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    family: {},
    account: {}
  },
  tapTabItem: function (e) {
    utils.switchTabBar(app.globalData.tabBar.list, this.route, e)
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let {family, account, courses} = app.globalData
    wx.setNavigationBarTitle({
      title: family.name,
    })
    this.setData({
      family: family,
      account: account,
      courses: courses
    })
    app.editTabBar()
  }
})