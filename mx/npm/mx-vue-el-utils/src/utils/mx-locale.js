import Vue from 'vue'
import VueI18n from 'vue-i18n'

import ElEn from 'element-ui/lib/locale/lang/en'
import ElZhCN from 'element-ui/lib/locale/lang/zh-CN'

import MxEn from '@/assets/locale/en.json'
import MxZhCN from '@/assets/locale/zh-CN.json'

Vue.use(VueI18n)

const i18n = new VueI18n({
  locale: 'en',
  fallbackLocale: 'en',
  messages: {
    en: MxEn,
    'zh-CN': MxZhCN
  }
})

let setLanguage = (l) => {
  if (l) {
    if (l === 'en') {
      i18n.locale = 'en'
      i18n.mergeLocaleMessage('en', ElEn)
    } else if (l === 'zh-CN') {
      i18n.locale = 'zh-CN'
      i18n.mergeLocaleMessage('zh-CN', ElZhCN)
    }
  }
}

let mergeMessage = (lang, message) => {
  if (lang && message) {
    i18n.mergeLocaleMessage(lang, message)
  }
}

let mergeMessages = (messages) => {
  if (messages) {
    Object.keys(messages).map(k => mergeMessage(k, messages[k]))
  }
}

export default {
  i18n,
  mergeMessage,
  mergeMessages,
  setLanguage
}
