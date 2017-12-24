import Vue from 'vue'
import VueI18n from 'vue-i18n'
import enLocale from 'element-ui/lib/locale/lang/en'
import zhCNLocale from 'element-ui/lib/locale/lang/zh-CN'

import en from './en'
import zhCN from './zhCN'

const messages = {
  en: en,
  zhCN: zhCN
}

Vue.use(VueI18n)

let lang = 'en'

export default {
  i18n: new VueI18n({locale: lang, messages}),
  elLocale: lang === 'en' ? enLocale : zhCNLocale,
  setLanguage (language) {
    if (lang !== null && lang !== undefined) {
      lang = language
      this.i18n = new VueI18n({locale: lang, messages})
      this.elLocale = lang === 'en' ? enLocale : zhCNLocale
    }
  }
}
