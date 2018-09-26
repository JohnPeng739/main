// pages/course/course.js
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    types: ['腐败', '创收'],
    typeIndex: 0,
    enableParentCourse: false,
    courses: [],
    courseIndex: [0, 0, 0]
  },
  bindTypeChange: function(e) {
    this.setData({
      typeIndex: e.detail.value
    })
    this.initData()
  },
  bindEnableParentCourseChange: function(e) {
    this.setData({
      enableParentCourse: e.detail.value
    })
  },
  bindCourseChange: function(e) {
    let select = e.detail.value
    console.log(select)
  },
  filterCourse: function(courses, filterType) {
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
  initData: function() {
    let allCourses = app.globalData.courses
    let {
      typeIndex,
      courses,
      courseIndex
    } = this.data
    let filterType = ('' + typeIndex !== '1' ? 'INCOME' : 'SPENDING')
    allCourses = this.filterCourse(allCourses, filterType)
    this.setData({
      courses: allCourses
    })
  },
  onLoad: function(e) {
    let {
      family
    } = app.globalData
    wx.setNavigationBarTitle({
      title: family.name + ' > 项目设置',
    })
    this.initData()
  }
})