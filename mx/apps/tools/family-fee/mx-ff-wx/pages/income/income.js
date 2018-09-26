// pages/income/income.js
import utils from '../../utils/util.js'

const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    family: {},
    account: {},
    allCourses: [], 
    courses: [],
    courseIndex: [0, 0, 0]
  },
  bindCourseChange: function(e) {
    console.log(e.detail.value)
  },
  tapTabItem: function (e) {
    utils.switchTabBar(app.globalData.tabBar.list, this.route, e)
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
  onLoad: function (options) {
    let {family, account, courses} = app.globalData
    wx.setNavigationBarTitle({
      title: family.name,
    })
    let allCourses = app.globalData.courses
    allCourses = this.filterCourse(allCourses, 'SPENDING')
    this.setData({
      courses: allCourses,
      family: family,
      account: account
    })
    app.editTabBar()
  }
})