import AppEn from './assets/locale/en.json'
import AppZhCN from './assets/locale/zh-CN.json'

import MxChooseDictInput from './components/mx-choose-dict-input'
import MxChooseDictTag from './components/mx-choose-dict-tag'
import MxDictManage from './components/mx-dict-manage'

import MxChooseUserInput from './components/rbac/mx-choose-user-input'
import MxAccountSetting from './components/rbac/mx-account-setting'
import MxChangePassword from './components/rbac/mx-change-password'
import MxUserManage from './components/rbac/mx-user-manage'
import MxDepartmentManage from './components/rbac/mx-department-manage'
import MxRoleManage from './components/rbac/mx-role-manage'
import MxPrivilegeManage from './components/rbac/mx-privilege-manage'
import MxAccountManage from './components/rbac/mx-account-manage'
import MxAccreditManage from './components/rbac/mx-accredit-manage'
import MxLoginHistoryManage from './components/rbac/mx-login-history-manage'
import MxOperateLogManage from './components/rbac/mx-operate-log-manage'

import RbacLayout from './layout/rbac-layout.vue'
import MxStoreAccount from './store/account'

const AppLocaleMessages = {
  en: AppEn,
  'zh-CN': AppZhCN
}

const components = [
  MxChooseDictInput,
  MxChooseDictTag,
  MxDictManage,
  MxChooseUserInput,
  MxAccountSetting,
  MxChangePassword,
  MxUserManage,
  MxDepartmentManage,
  MxRoleManage,
  MxPrivilegeManage,
  MxAccountManage,
  MxAccreditManage,
  MxLoginHistoryManage,
  MxOperateLogManage,
  RbacLayout
]

const install = function (Vue, opts = {}) {
  components.map(component => {
    Vue.component(component.name, component)
  })
}

if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue)
}

export default {
  install,
  AppLocaleMessages,
  MxChooseDictInput,
  MxChooseDictTag,
  MxDictManage,
  MxChooseUserInput,
  MxAccountSetting,
  MxChangePassword,
  MxUserManage,
  MxDepartmentManage,
  MxRoleManage,
  MxPrivilegeManage,
  MxAccountManage,
  MxAccreditManage,
  MxLoginHistoryManage,
  MxOperateLogManage,
  MxStoreAccount,
  RbacLayout
}

export {
  AppLocaleMessages,
  MxChooseDictInput,
  MxChooseDictTag,
  MxDictManage,
  MxChooseUserInput,
  MxAccountSetting,
  MxChangePassword,
  MxUserManage,
  MxDepartmentManage,
  MxRoleManage,
  MxPrivilegeManage,
  MxAccountManage,
  MxAccreditManage,
  MxLoginHistoryManage,
  MxOperateLogManage,
  MxStoreAccount,
  RbacLayout
}
