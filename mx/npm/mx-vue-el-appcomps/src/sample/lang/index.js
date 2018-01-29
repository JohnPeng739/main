import Vue from 'vue'
import VueI18n from 'vue-i18n'

import elEn from 'element-ui/lib/locale/lang/en'
import elZhCN from 'element-ui/lib/locale/lang/zh-CN'

import { MxLocale } from 'mx-vue-el-utils'
import { AppLocale } from '../../../dist/mx-vue-el-appcomps.min'
import myEn from './en'
import myZhCN from './zh-CN'
// import AppLocale from '@/locale'

Vue.use(VueI18n)

let MyLanguage = {
  elLang: elEn,
  mxLang: MxLocale.MxEn,
  appLang: AppLocale.AppEn,
  myI18n: new VueI18n({locale: 'en', messages: {en: myEn, 'zh-CN': myZhCN}})
}

const setLanguage = function (lang) {
  if (lang === 'en') {
    MyLanguage = {
      elLang: elEn,
      mxLang: MxLocale.MxEn,
      appLang: AppLocale.AppEn,
      myI18n: new VueI18n({locale: 'en', messages: {en: myEn, 'zh-CN': myZhCN}})
    }
  } else if (lang === 'zh-CN') {
    MyLanguage = {
      elLang: elZhCN,
      mxLang: MxLocale.MxZhCN,
      appLang: AppLocale.AppZhCN,
      myI18n: new VueI18n({locale: 'zh-CN', messages: {en: myEn, 'zh-CN': myZhCN}})
    }
  }
}

export default MyLanguage

export { MyLanguage, setLanguage }
