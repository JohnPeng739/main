import AppLocale from './locale'

import MxChooseDictInput from './components/mx-choose-dict-input'
import MxChooseDictTag from './components/mx-choose-dict-tag'
import MxDictManage from './components/mx-dict-manage'

import MxChooseUserInput from './components/rbac/mx-choose-user-input'
import MxUserManage from './components/rbac/mx-user-manage'
import MxDepartmentManage from './components/rbac/mx-department-manage'
import MxRoleManage from './components/rbac/mx-role-manage'
import MxPrivilegeManage from './components/rbac/mx-privilege-manage'
import MxAccountManage from './components/rbac/mx-account-manage'
import MxAccreditManage from './components/rbac/mx-accredit-manage'
import MxLoginHistoryManage from './components/rbac/mx-login-history-manage'
import MxOperateLogManage from './components/rbac/mx-operate-log-manage'

import MxStoreAccount from './store/account'

const components = [
  MxChooseDictInput,
  MxChooseDictTag,
  MxDictManage,
  MxChooseUserInput,
  MxUserManage,
  MxDepartmentManage,
  MxRoleManage,
  MxPrivilegeManage,
  MxAccountManage,
  MxAccreditManage,
  MxLoginHistoryManage,
  MxOperateLogManage
]

const install = function (Vue, opts = {}) {
  AppLocale.use(opts.locale)
  AppLocale.i18n()

  components.map(component => {
    Vue.component(component.name, component)
  })
}

if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue)
}

export default {
  version: '',
  locale: AppLocale.use,
  i18n: AppLocale.i18n,
  install,
  MxChooseDictInput,
  MxChooseDictTag,
  MxDictManage,
  MxChooseUserInput,
  MxUserManage,
  MxDepartmentManage,
  MxRoleManage,
  MxPrivilegeManage,
  MxAccountManage,
  MxAccreditManage,
  MxLoginHistoryManage,
  MxOperateLogManage,
  MxStoreAccount
}

export {
  MxChooseDictInput,
  MxChooseDictTag,
  MxDictManage,
  MxChooseUserInput,
  MxUserManage,
  MxDepartmentManage,
  MxRoleManage,
  MxPrivilegeManage,
  MxAccountManage,
  MxAccreditManage,
  MxLoginHistoryManage,
  MxOperateLogManage,
  MxStoreAccount
}
