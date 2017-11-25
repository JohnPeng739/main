import {navData} from '../modules/manage/router/index'

let user = {
  'code': 'joy',
  'name': 'Joy.Peng',
  'valid|5-5': true,
  'online|5-5': true,
  'createdTime': '@integer',
  'updatedTime': '@integer',
  'tools': () => {
    let menuItems = []
    let foundMenuItems = (list) => {
      Object.keys(list).forEach(key => {
        let menuItem = list[key]
        if (menuItem.children) {
          foundMenuItems(menuItem.children)
        } else {
          menuItems.push(menuItem.path)
        }
      })
    }
    foundMenuItems(navData)
    let num = Math.random() * 6
    let tools = []
    for (let index = 0; index < num; index++) {
      let item = menuItems[Math.round(Math.random() * (menuItems.length - 1))]
      if (tools.indexOf(item) === -1) {
        tools.push(item)
      }
    }
    return tools
  },
  'roles': ['manager']
}

export default [{
  path: /\/rest\/init/,
  type: 'get',
  data: {
    data: user
  }
}, {
  path: /\/rest\/user\/logs\?userCode=\w/,
  type: 'get',
  data: {
    'data|5-100': [{
      'id': '@guid',
      'message': '@csentence',
      'operator': '@ctitle',
      'createdTime': '@integer'
    }]
  }
}, {
  path: /\/rest\/user\/logs\?userCode=\w/,
  type: 'post',
  data: {
    'pagination': {total: 60, size: 20, page: 1},
    'data|5-100': [{
      'id': '@guid',
      'message': '@csentence',
      'operator': '@ctitle',
      'createdTime': '@integer'
    }]
  }
}, {
  path: /\/rest\/users\?userCode=\w/,
  type: 'get',
  data: {
    'data|5-100': [user]
  }
}, {
  path: /\/rest\/users\?userCode=\w/,
  type: 'post',
  data: {
    'pagination': {total: 100, size: 20, page: 1},
    'data|5-100': [user]
  }
}, {
  path: /\/rest\/users\/\w(\?userCode=\w)*/,
  type: 'get',
  data: {
    data: user
  }
}, {
  path: /\/rest\/password\/change\?userCode=\w/,
  type: 'post',
  data: {
    data: user
  }
}, {
  path: /\/rest\/login(\?\w=\w)*/,
  type: 'post',
  data: {
    data: user
  }
}, {
  path: /\/rest\/logout\?userCode=\w/,
  type: 'post',
  data: {
    data: {
      'success|1': true
    }
  }
}]
