// pages/budget/budget.js
import { isBlank } from '../../utils/util.js'

const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    types: ['收入', '支出'],
    formVisible: false,
    form: {
      index: 0,
      course: '',
      money: '0.00',
      description: ''
    },
    budgetIncomes: [{ course: '工资', money: 1000.00, description: '工资性收入', myStyle: '' }, { course: '奖金', money: 1000.00, description: '奖金性收入', myStyle: '' }],
    budgetSpendings: [{ course: '网购', money: 1000.00, description: '工资性收入', myStyle: '' }, { course: '公共事业', money: 1000.00, description: '奖金性收入', myStyle: '' }],
    removeBtnWidth: 150
  },

  bindShowForm: function (e) {
    let type = e.target.dataset.type
    this.setData({formVisible: true, form: {index: type === 'income' ? 0 : 1, course: '', money: '0.00', description: ''}})
  },

  bindHideForm: function (e) {
    this.setData({formVisible: false})
  },

  bindTouchStart: function (e) {
    this.setData({
      startX: e.touches[0].clientX
    })
  },

  bindTouchMove: function (e) {
    let touch = e.touches[0]
    let moveX = touch.clientX
    let disX = this.data.startX - moveX
    let removeBtnWidth = this.data.removeBtnWidth
    let style = ''
    if (disX <= 0) {
      style = 'left: 0rpx;'
    } else {
      style = 'left: -' + disX + 'rpx;'
      if (disX >= removeBtnWidth) {
        style = 'left: -' + removeBtnWidth + 'rpx;'
      }
    }
    let index = e.target.dataset.index
    let type = e.target.dataset.type
    let items = (type === 'income' ? this.data.budgetIncomes : this.data.budgetSpendings)
    if (items[index]) {
      items[index].myStyle = style
      this.setData(type === 'income' ? { budgetIncomes: items } : { budgetSpendings: items })
    }
  },

  bindTouchEnd: function (e) {
    let endX = e.changedTouches[0].clientX
    let disX = this.data.startX - endX
    let removeBtnWidth = this.data.removeBtnWidth
    let style = (disX > removeBtnWidth / 2) ? 'left: -' + removeBtnWidth + 'rpx;' : 'left: 0rpx;'
    let index = e.target.dataset.index
    let type = e.target.dataset.type
    let items = (type === 'income' ? this.data.budgetIncomes : this.data.budgetSpendings)
    if (items[index]) {
      items[index].myStyle = style
      this.setData(type === 'income' ? { budgetIncomes: items } : { budgetSpendings: items })
    }
  },

  bindRemove: function (e) {
    console.log(e.target.dataset)
  },

  bindTypeChange: function (e) {
    let data = this.data
    data.form.index = e.detail.value
    this.setData(data)
  },

  bindAddBudget: function (e) {
    let { type, course, money, description } = e.detail.value
    let data = this.data
    type = data.types[type]
    money = Number(money)
    data.tips = ''
    if (isBlank(course)) {
      wx.showToast({ title: '输入科目' })
      return
    } else if (money <= 0) {
      wx.showToast({ title: '输入金额' })
      return
    } else {
      // TODO 保存预算
      if ('收入' === type) {
        data.budgetIncomes.push({ course, money, description })
        data.form = { index: 0, course: '', money: '0.00', description: '' }
        data.formVisible = false
      } else if ('支出' === type) {
        data.budgetSpendings.push({ course, money, description })
        data.form = { index: 0, course: '', money: '0.00', description: '' }
        data.formVisible = false
      }
    }
    this.setData(data)
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // TODO 获取家庭预算列表
    let myFamily = app.globalData.myFamily
    if (myFamily) {
      let data = this.data
      data.myFamily = myFamily
      if (myFamily.budget) {
        data.budgetIncomes = myFamily.budget.incomes
        data.budgetSpendings = myFamily.budget.spendings
      }
      this.setData(data)
    }
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})