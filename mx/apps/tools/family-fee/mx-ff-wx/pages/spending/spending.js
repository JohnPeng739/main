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
    courseIndex: [0, 0, 0],
    members: [],
    memberIndex: 0
  },
  tapTabItem: function(e) {
    utils.switchTabBar(app.globalData.tabBar.list, this.route, e)
  },
  bindCourseChange: function(e) {
    console.log(e.detail.value)
  },
  bindMemberChange: function(e) {
    console.log(e.detail.value)
    this.setData({
      memberIndex: e.detail.value
    })
  },
  filterCourse: function (courses, filterType) {
    let result = []
    courses.forEach(course => {
      if (course.type !== filterType) {
        if (course.children && course.children.length > 0) {
          let children = this.filterCourse(course.children)
          children.sort((o1, o2) => o1.order - o2.order)
          course.children = children
        }
        result.push(course)
      }
    })
    result.sort((o1, o2) => o1.order - o2.order)
    return result
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
    family.members.forEach(member => members.push(member.role))
    courses = this.filterCourse(courses, 'INCOME')
    this.setData({
      courses: courses,
      family: family,
      members: members
    })
    app.editTabBar()
  }
})