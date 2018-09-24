// pages/course/course.js
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    allCourses: [],
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
    this.initData(0, 0)
  },
  bindEnableParentCourseChange: function(e) {
    this.setData({
      enableParentCourse: e.detail.value
    })
  },
  bindCourseChange: function(e) {
    this.setData({
      courseIndex: e.detail.value
    })
  },
  bindColumnChange: function(e) {
    let {
      column,
      value
    } = e.detail
    let {
      courseIndex
    } = this.data
    courseIndex[column] = value
    this.initData(column, value)
  },
  filterCourse: function(courses, filterType) {
    let result = []
    courses.forEach(course => {
      if (course.type !== filterType) {
        if (course.children && course.children.length > 0) {
          let children = this.filterCourse(course.children)
          course.children = children
        }
        result.push(course)
      }
    })
    return result
  },
  initData: function(column, value) {
    let allCourses = app.globalData.courses
    let {
      typeIndex,
      courses,
      courseIndex
    } = this.data
    let filterType = ('' + typeIndex !== '1' ? 'INCOME' : 'SPENDING')
    allCourses = this.filterCourse(allCourses, filterType)
    let list = allCourses
    let item = null
    let result = []
    // 最多支持3级科目
    for (let index = 0; index <= 2; index++) {
      let col = []
      if (list && list.length > 0) {
        list.forEach(course => col.push(course.name))
        item = list[courseIndex[index]]
        list = item.children
      }
      result.push(col)
    }
    this.setData({
      courses: result
    })
  },
  onLoad: function(e) {
    let {family} = app.globalData
    wx.setNavigationBarTitle({
      title: family.name + ' > 项目设置',
    })
    this.initData(0, 0)
  }
})