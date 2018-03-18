import MxLocale from './utils/mx-locale'
import MxNotify from './utils/mx-notify'
import MxAjax from './utils/mx-ajax'
import MxAjaxEl from './utils/mx-ajax-el'
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
import MxDictSelect from './components/mx-dict-select'

import MxNormalLayout from './layout/mx-normal'
import MxMaxLayout from './layout/mx-max'

import ElEn from 'element-ui/lib/locale/lang/en'
import ElZhCN from 'element-ui/lib/locale/lang/zh-CN'
import MxEn from './assets/locale/en.json'
import MxZhCN from './assets/locale/zh-CN.json'

const components = [
  MxIcon,
  MxDialog,
  MxPaginateTable,
  MxPassword,
  MxChooseTag,
  MxChooseInput,
  MxTagNormal,
  MxTagCouple,
  MxDictSelect,
  MxNormalLayout,
  MxMaxLayout
]

const install = function (Vue, opts = {}) {
  MxLocale.mergeMessages({
    en: ElEn,
    'zh-CN': ElZhCN
  })
  MxLocale.mergeMessages({
    en: MxEn,
    'zh-CN': MxZhCN
  })
  components.map(component => {
    Vue.component(component.name, component)
  })
}

if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue)
}

export default {
  i18n: MxLocale.i18n(),
  install,
  MxLocale,
  MxNotify,
  MxAjax,
  MxAjaxEl,
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
  MxDictSelect,
  MxNormalLayout,
  MxMaxLayout
}

export {
  MxLocale,
  MxNotify,
  MxAjax,
  MxAjaxEl,
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
  MxDictSelect,
  MxNormalLayout,
  MxMaxLayout
}
