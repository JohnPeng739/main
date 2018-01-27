import Mock from 'mockjs'

import userManage from './user-manage'
import roleManage from './role-manage'
import departManage from './depart-manage'
import privilegeManage from './privilege-manage'
import accountManage from './account-manage'
import accreditManage from './accredit-manage'

const mock = function () {
  [userManage, roleManage, departManage, privilegeManage, accountManage, accreditManage].forEach(m => {
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

export default mock
