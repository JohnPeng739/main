// pages/income/income.js
import {formatDate} from '../../utils/util.js'

const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    myFamily: {
      name: ''
    },
    incomeTime: formatDate(new Date()),
    courses: [],
    courseIndex: 0,
    owners: [],
    ownerIndex: 0
  },

  bindTimeChange: function (e) {
    let data = this.data
    data.incomeTime = e.detail.value
    this.setData(data)
  },

  bindSaveIncome: function (e) {
    console.log(e.detail.value)
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let myFamily = app.globalData.myFamily
    let data = this.data
    if (myFamily) {
      data.myFamily.name = myFamily.name
      let owners = []
      myFamily.members.forEach(member => {
        owners.push(member.nickName)
      })
      data.owners = owners
    }
    // TODO 获取科目
    data.courses = ['网购', '公共事业', '外出腐败', '旅游']
    this.setData(data)
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