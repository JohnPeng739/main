import Mock from 'mockjs'
import {logger} from 'mx-app-utils'

import userManage from '../mock/user-manage'
import roleManage from '../mock/role-manage'
import departManage from '../mock/depart-manage'
import privilegeManage from '../mock/privilege-manage'
import accountManage from '../mock/account-manage'
import accreditManage from '../mock/accredit-manage'

const mock = () => {
  logger.debug('Load all the mock......')
  let mocks = [userManage, roleManage, departManage, privilegeManage, accountManage, accreditManage]
  mocks.forEach(m => {
    if (m && m.length > 0) {
      m.forEach(item => {
        if (item) {
          let {path, type, data} = item
          if (path && data) {
            if (!type || typeof type !== 'string') {
              type = 'get'
            }
            Mock.mock(path, type, data)
          }
        }
      })
    }
  })
}

export default () => {
  // if (process.env.NODE_ENV === 'development') {
    mock()
  // }
}
