// pages/test/test.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    items: [{ id: '001', name: 'item 1 abcdefgh', myStyle: '' }, { id: '002', name: 'item 2', myStyle: '' }, { id: '003', name: 'item 3', myStyle: '' }],
    removeBtnWidth: 150
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
    let items = this.data.items
    items[index].myStyle = style
    this.setData({
      items: items
    })
  },

  bindTouchEnd: function (e) {
    let endX = e.changedTouches[0].clientX
    let disX = this.data.startX - endX
    let removeBtnWidth = this.data.removeBtnWidth
    let style = (disX > removeBtnWidth / 2) ? 'left: -' + removeBtnWidth + 'rpx;' : 'left: 0rpx;'
    let index = e.target.dataset.index
    let items = this.data.items
    items[index].myStyle = style
    this.setData({
      items: items
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
  
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