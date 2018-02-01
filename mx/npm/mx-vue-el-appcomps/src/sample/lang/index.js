import Vue from 'vue'

import MxVueElUtils from 'mx-vue-el-utils'
// import {AppLocaleMessages} from '../../index'
import {AppLocaleMessages} from '../../../dist/mx-vue-el-appcomps.min'

import myEn from './en'
import myZhCN from './zh-CN'

const MxLocale = MxVueElUtils.MxLocale

MxLocale.mergeMessages({
  en: myEn,
  'zh-CN': myZhCN
})

MxLocale.setLanguage('en')
MxLocale.mergeMessages(AppLocaleMessages)

const changeLanguage = (lang) => {
  if (lang) {
    MxLocale.setLanguage(lang)
    Vue.use(MxVueElUtils, {locale: lang})
  }
}

export default MxLocale

export {
  MxLocale,
  changeLanguage
}
