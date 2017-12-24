import Vue from 'vue'
import VueI18n from 'vue-i18n'
import enLocale from 'element-ui/lib/locale/lang/en'
import zhCNLocale from 'element-ui/lib/locale/lang/zh-CN'
import { mixin } from 'mx-app-utils'

import en from './en'
import zhCN from './zhCN'

let bundle = {
  en: en,
  zhCN: zhCN
}

Vue.use(VueI18n)

let lang = 'en'

export default {
  i18n: new VueI18n({locale: lang, messages: bundle}),
  elLocale: lang === 'en' ? enLocale : zhCNLocale,
  setLanguage (language, ...messages) {
    if (lang !== null && lang !== undefined) {
      lang = language
    }
    if (messages !== null && messages !== undefined) {
      if (messages instanceof Array) {
        messages.forEach(row => mixin(bundle, row))
      } else {
        mixin(bundle, messages)
      }
    }
    this.i18n = new VueI18n({locale: lang, messages: bundle})
    this.elLocale = lang === 'en' ? enLocale : zhCNLocale
  }
}
