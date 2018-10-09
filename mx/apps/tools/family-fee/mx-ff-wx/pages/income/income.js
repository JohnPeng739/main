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
    courses: [],
    courseIndex: [0, 0, 0],
    members: [],
    memberIndex: 0,
    course: {},
    occurDate: utils.dateString(new Date())
  },
  bindCourseChange: function(e) {
    this.setData({
      course: e.detail.value
    })
  },
  bindDateChange: function (e) {
    this.setData({
      occurDate: e.detail.value
    })
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
  formSubmit: function (e) {
    let form = e.detail.value
    let {
      course,
      memberIndex,
      family,
      occurDate
    } = this.data
    let data = {
      openId: app.globalData.openId,
      familyId: family.id,
      courseId: course.id,
      desc: form.desc,
      ownerId: memberIndex > 0 ? family.members[memberIndex - 1].ffeeAccount.id : '',
      money: form.money,
      occurTime: Date.parse(occurDate)
    }
    if (!(data.money >= 0)) {
      utils.error('必须输入收入金额！')
      return
    }
    utils.post('incomes/new', data, function (res) {
      if (res.data.errorCode === 0) {
        console.log(res.data.data)
        utils.info('保存一笔收入成功。')
      } else {
        utils.error(res.data.errorMessage)
      }
    })
  },
  formReset: function () {
    //
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let {
      family,
      courses
    } = app.globalData
    wx.setNavigationBarTitle({
      title: family.name,
    })
    let members = ['公共']
    family.members.forEach(member => members.push(member.role))
    courses = this.filterCourse(courses, 'SPENDING')
    let course = utils.getMultiColumnData(courses, this.data.courseIndex)
    this.setData({
      courses: courses,
      family: family,
      members: members,
      course: course
    })
    app.editTabBar()
  }
})