import {navData} from '../modules/manage/router/index'

export default [{
  path: /\/rest\/login(\?\w=\w)*/,
  type: 'post',
  data: {
    data: {
      'code': 'joy',
      'name': 'Joy.Peng',
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
  }
}, {
  path: /\/rest\/logout\?userCode=\w/,
  type: 'post',
  data: {
    data: {
      'success|5-5': true
    }
  }
}]
