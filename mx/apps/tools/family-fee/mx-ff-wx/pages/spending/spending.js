// pages/spending/spending.js
import utils from '../../utils/util.js'

let app = getApp()

Page({
  /**
   * 页面的初始数据
   */
  data: {
    family: {},
    courses: [],
    courseIndex: 0,
    members: [],
    memberIndex: 0
  },
  tapTabItem: function(e) {
    utils.switchTabBar(app.globalData.tabBar.list, this.route, e)
  },
  bindCourseChange: function(e) {
    this.setData({
      courseIndex: e.detail.value
    })
  },
  bindMemberChange: function(e) {
    this.setData({
      memberIndex: e.detail.value
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    let {
      family,
      courses
    } = app.globalData
    wx.setNavigationBarTitle({
      title: family.name,
    })
    let members = ['公共']
    let tarCourses = ['预算外']
    family.members.forEach(member => members.push(member.role))
    courses.forEach(course => tarCourses.push(course.name))
    this.setData({
      family: family,
      courses: tarCourses,
      members: members
    })
    app.editTabBar()
  }
})