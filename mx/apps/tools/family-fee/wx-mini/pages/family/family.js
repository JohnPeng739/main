// pages/family/family.js
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    type: '',
    tips: '',
    myFamily: {},
    ownerTypes: ['粑粑', '麻麻', '长辈', '公主', '王子', '路人'],
    ownerTypeIndex: 0
  },

  bindOwnerTypeChange: function (e) {
    this.setData({
      ownerTypeIndex: e.detail.value
    })
  },

  bindAddMember: function (e) {
    console.log('add a new member.')
  },

  bindCreateFamily: function (e) {
    let familyName = e.detail.value.family
    if (familyName && familyName.length > 0) {
      // TODO 提交创建家庭
      wx.setStorage({
        key: 'myFamily',
        data: { name: familyName },
        success: function (res) {
          wx.navigateTo({ url: '../index/index' })
        }
      })
    } else {
      let data = this.data
      data.tips = '请输入家庭名称！'
      this.setData(data)
    }
  },

  bindJoinFamily: function (e) {
    console.log('join the family')
  },

  bindFamilyBill: function (e) {
    wx.navigateTo({ url: '../bill/bill', })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let userInfo = app.globalData.userInfo
    if (userInfo) {
      let data = this.data
      data.type = options.type
      if (app.globalData.myFamily) {
        data.myFamily = app.globalData.myFamily
      }
      data.myFamily.owner = {
        avatarUrl: userInfo.avatarUrl,
        nickName: userInfo.nickName
      }
      // TODO 获取家庭信息
      data.myFamily.members = [{
        nickName: 'abc',
        avatarUrl: 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqXGb80ZKOdmqaJpFvfPOIsRq0dszcMysBicPic8e0VIh4WajXR34CyV2qhdDzvdqybWnpvFT4leictw/0'
      }]
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