import Vue from 'vue'
import VueI18n from 'vue-i18n'

import elEn from 'element-ui/lib/locale/lang/en'
import elZhCN from 'element-ui/lib/locale/lang/zh-CN'

import mxEn from 'mx-vue-el-utils/dist/locale/lang/en'
import mxZhCN from 'mx-vue-el-utils/dist/locale/lang/zh-CN'

import appEn from '@/locale/lang/en'
import appZhCN from '@/locale/lang/zh-CN'

import myEn from './en'
import myZhCN from './zh-CN'

Vue.use(VueI18n)

let MyLanguage = {
  elLang: elEn,
  mxLang: mxEn,
  appLang: appEn,
  myI18n: new VueI18n({locale: 'en', messages: {en: myEn, 'zh-CN': myZhCN}})
}

const setLanguage = function (lang) {
  if (lang === 'en') {
    MyLanguage = {elLang: elEn, mxLang: mxEn, appLang: appEn, myI18n: new VueI18n({locale: 'en', messages: {en: myEn, 'zh-CN': myZhCN}})}
  } else if (lang === 'zh-CN') {
    MyLanguage = {elLang: elZhCN, mxLang: mxZhCN, appLang: appZhCN, myI18n: new VueI18n({locale: 'zh-CN', messages: {en: myEn, 'zh-CN': myZhCN}})}
  }
}

export default MyLanguage

export {MyLanguage, setLanguage}
