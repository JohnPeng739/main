import Vue from 'vue'
import MxVueElUtils from '@/index'
// import {MxLocale} from '../../../dist/mx-vue-el-utils.min'
import MxLocale from '@/utils/mx-locale'

import myEn from './en.json'
import myZhCN from './zh-CN.json'

MxLocale.mergeMessages({
  en: myEn,
  'zh-CN': myZhCN
})

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
