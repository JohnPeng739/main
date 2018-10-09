const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const ajax = (url, method, data, success, fail) => {
  wx.request({
    url: 'https://mx-john.cn/rest/v1/' + url,
    data: data,
    method: method,
    success: success,
    fail: fail
  })
}

const get = (url, data, success, fail) => ajax(url, 'GET', data, success, fail)
const post = (url, data, success, fail) => ajax(url, 'POST', data, success, fail)
const put = (url, data, success, fail) => ajax(url, 'PUT', data, success, fail)
const del = (url, data, success, fail) => ajax(url, 'DELETE', data, success, fail)

const error = message => {
  wx.showModal({
    title: '错误',
    content: message,
    showCancel: false
  })
}

const info = message => {
  wx.showToast({
    title: message,
    icon: 'none',
    duration: 3000
  })
}

const dateString = (date) => {
  let year = date.getFullYear()
  let month = date.getMonth() + 1
  let day = date.getDate()
  return year + '-' + month + '-' + day
}

const switchTabBar = (tabBarItems, currRoute, e) => {
  var currUrl = currRoute;
  var url = e.currentTarget.dataset.url;
  (currUrl.indexOf('/') != 0) && (currUrl = '/' + currUrl);
  if (url) {
    if (currUrl != url) {
      wx.redirectTo({
        url: url,
      })
    }
  } else {
    tabBarItems.forEach(item => {
      if (item.tabFunc) {
        item.tabFunc()
      }
    })
  }
}

const getMultiColumnData = (data, selectIndex) => {
  let list = data
  let item = null
  for (let index in selectIndex) {
    if (list && list.length > selectIndex[index]) {
      item = list[selectIndex[index]]
      if (item) {
        list = item.children
      }
    }
  }
  return item
}

module.exports = {
  formatTime: formatTime,
  get: get,
  post: post,
  put: put,
  del: del,
  error: error,
  info: info,
  dateString: dateString,
  switchTabBar: switchTabBar,
  getMultiColumnData: getMultiColumnData
}