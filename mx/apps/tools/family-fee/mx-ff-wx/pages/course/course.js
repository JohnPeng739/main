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
    courseIndex: [0, 0, 0],
    parentCourse: {}
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
    this.setData({
      course: e.detail.value
    })
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
  formSubmit: function (e) {
    let form = e.detail.value
    let {
      parentCourse,
      typeIndex,
      enableParentCourse
    } = this.data
    let data = {
      type: typeIndex === 0 ? 'SPENDING' : 'INCOME',
      parentCourseId: enableParentCourse ? parentCourse.id : '',
      code: form.code,
      name: form.name,
      desc: form.desc
    }
    if (!code || code.length <= 0) {
      utils.error('必须输入项目代码！')
      return
    }
    if (!name || name.length <= 0) {
      utils.error('必须输入项目名称！')
      return
    }
    utils.post('courses/new', data, function (res) {
      if (res.data.errorCode === 0) {
        console.log(res.data.data)
        utils.info('保存一个项目。')
      } else {
        utils.error(res.data.errorMessage)
      }
    })
  },
  formReset: function () {
    //
  },
  onLoad: function(e) {
    let {
      family
    } = app.globalData
    let course = utils.getMultiColumnData(courses, this.data.courseIndex)
    this.setData({
      parentCourse: course
    })
    wx.setNavigationBarTitle({
      title: family.name + ' > 项目设置',
    })
    this.initData()
  }
})