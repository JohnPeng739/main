// pages/create-family/create-family.js
let app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    role: 0,
    roles: ["皇帝", "皇后", "王子", "公主", "太上皇", "其他"]
  },

  formSubmit: function(e) {
    let form = e.detail.value
    let family = {familyName: form.familyName, role: this.data.roles[form.role]}
    console.log(family)
    app.globalData.family = family
    // TODO 保存家庭
    wx.reLaunch({
      url: '../index/index'
    })
  },

  formReset: function() {
    this.setData({
      role: 0
    })
  },

  bindRoleChange: function(e) {
    this.setData({
      role: e.detail.value
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      userInfo: app.globalData.userInfo
    })
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