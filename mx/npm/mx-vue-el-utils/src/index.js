import MxLocale from './utils/mx-locale'
import MxNotify from './utils/mx-notify'
import MxAjax from './utils/mx-ajax'
import MxEcharts from './utils/mx-echarts'
import MxFormValidateRules from './utils/mx-form-validate-rules'

import MxIcon from './components/mx-icon'
import MxDialog from './components/mx-dialog'
import MxPaginateTable from './components/mx-paginate-table'

import MxPassword from './components/mx-password'
import MxChooseTag from './components/mx-choose-tag'
import MxChooseInput from './components/mx-choose-input'
import MxTagNormal from './components/mx-tag-normal'
import MxTagCouple from './components/mx-tag-couple'

import MxNormalLayout from './layout/mx-normal'

const components = [
  MxIcon,
  MxDialog,
  MxPaginateTable,
  MxPassword,
  MxChooseTag,
  MxChooseInput,
  MxTagNormal,
  MxTagCouple,
  MxNormalLayout
]

const install = function (Vue, opts = {}) {
  MxLocale.setLanguage(opts.locale)
  components.map(component => {
    Vue.component(component.name, component)
  })
}

if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue)
}

export default {
  locale: MxLocale.i18n.locale,
  i18n: MxLocale.i18n,
  install,
  MxLocale,
  MxNotify,
  MxAjax,
  MxEcharts,
  MxFormValidateRules,
  MxIcon,
  MxDialog,
  MxPaginateTable,
  MxPassword,
  MxChooseTag,
  MxChooseInput,
  MxTagNormal,
  MxTagCouple,
  MxNormalLayout
}

export {
  MxLocale,
  MxNotify,
  MxAjax,
  MxEcharts,
  MxFormValidateRules,
  MxIcon,
  MxDialog,
  MxPaginateTable,
  MxPassword,
  MxChooseTag,
  MxChooseInput,
  MxTagNormal,
  MxTagCouple,
  MxNormalLayout
}
