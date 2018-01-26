import MxLocale from './locale'

import MxNotify from './utils/mx-notify'
import MxAjax from './utils/mx-ajax'
import MxEcharts from './utils/mx-echarts'
import MxFormValidateRules from './utils/mx-form-validate-rules'

import MxIcon from './components/mx-icon'
import MxDialog from './components/mx-dialog'
import MxPaginateTable from './components/mx-paginate-table'

import MxChooseTag from './components/mx-choose-tag'
import MxChooseInput from './components/mx-choose-input'
import MxTagNormal from './components/mx-tag-normal'
import MxTagCouple from './components/mx-tag-couple'

import MxNormalLayout from './layout/mx-normal'

const components = [
  MxIcon,
  MxDialog,
  MxPaginateTable,
  MxChooseTag,
  MxChooseInput,
  MxTagNormal,
  MxTagCouple,
  MxNormalLayout
]

const install = function (Vue, opts = {}) {
  MxLocale.use(opts.locale)
  MxLocale.i18n()
  components.map(component => {
    Vue.component(component.name, component)
  })
  Vue.prototype.$mxError = MxNotify.error
  Vue.prototype.$mxWarn = MxNotify.warn
  Vue.prototype.$mxInfo = MxNotify.info
  Vue.prototype.$mxConfirm = MxNotify.confirm
  Vue.prototype.$mxFormValidateWarn = MxNotify.formValidateWarn

  Vue.prototype.$mxGet = MxAjax.get
  Vue.prototype.$mxPost = MxAjax.post
  Vue.prototype.$mxPut = MxAjax.put
  Vue.prototype.$mxDel = MxAjax.del
}

if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue)
}

export default {
  version: '1.2.4',
  locale: MxLocale.use,
  i18n: MxLocale.i18n,
  install,
  MxNotify,
  MxAjax,
  MxEcharts,
  MxFormValidateRules,
  MxIcon,
  MxDialog,
  MxPaginateTable,
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
  MxChooseTag,
  MxChooseInput,
  MxTagNormal,
  MxTagCouple,
  MxNormalLayout
}
