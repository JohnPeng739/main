// pages/my-family/my-family.js
import utils from '../../utils/util.js'

const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    family: {},
    owner: {},
    members: []
  },
  tapTabItem: function(e) {
    utils.switchTabBar(app.globalData.tabBar.list, this.route, e)
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    let family = app.globalData.family
    this.setData({
      family: family
    })
    wx.setNavigationBarTitle({
      title: family.name,
    })
    let members = []
    let owner = {}
    family.members.forEach(member => {
      if (member.owner) {
        owner = member
      } else {
        members.push(member)
      }
    })
    this.setData({
      owner: owner,
      members: members
    })
    app.editTabBar()
  }
})