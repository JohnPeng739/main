import Vue from 'vue'
import MxVueElUtils from 'mx-vue-el-utils'
import AppComps from 'mx-vue-el-appcomps'

import myEn from '../assets/lang/en.json'
import myZhCN from '../assets/lang/zh-CN.json'

const MxLocale = MxVueElUtils.MxLocale
const appLocaleMessages = AppComps.AppLocaleMessages
MxLocale.mergeMessages(appLocaleMessages)
MxLocale.mergeMessages({
  en:myEn,
  'zh-CN': myZhCN
})

MxLocale.setLanguage('en')

const changeLangauge = (lang) => {
  if (lang) {
    MxLocale.setLanguage(lang)
    Vue.use(MxVueElUtils, {locale: lang})
    Vue.use(AppComps, {locale: lang})
  }
}

export default ({app, store}) => {
  changeLangauge('zh-CN')
  app.i18n = MxLocale.i18n
  /*
  app.i18n.path = (link) => {
    if (app.i18n.locale === app.i18n.fallbackLocale) {
      return `/${link}`
    }
    return `/${app.i18n.locale}/${link}`
  }
  */
}

export {
  MxLocale,
  changeLangauge
}
