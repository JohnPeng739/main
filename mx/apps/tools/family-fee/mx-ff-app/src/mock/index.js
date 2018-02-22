import Mock from 'mockjs'

import Account from '../mock/account'
import Family from '../mock/family'

const mocks = [Account, Family]
// TODO 添加相关模块的Mock模拟

const mock = function () {
  mocks.forEach(m => {
    if (m && m instanceof Array && m.length > 0) {
      m.forEach(item => {
        if (item && item.path && item.data) {
          let {path, type, data} = item
          if (!type || typeof type !== 'string' || type.length <= 0) {
            type = 'get'
          }
          Mock.mock(path, type, data)
        }
      })
    }
  })
}

export default mock
