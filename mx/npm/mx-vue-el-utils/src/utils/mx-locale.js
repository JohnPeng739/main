import Vue from 'vue'
import VueI18n from 'vue-i18n'
import { logger } from 'mx-app-utils'

Vue.use(VueI18n)

class MxLocale {
  static currentLanguage () {
    return MxLocale._lang
  }

  static changeLanguage (lang) {
    if (lang !== MxLocale._lang) {
      if (MxLocale._supports.indexOf(lang) >= 0) {
        MxLocale._i18n.locale = lang
        if (window.localStorage) {
          window.localStorage.setItem('locale', lang)
        }
      } else {
        logger.warn('Unsupported language: %s.', lang)
      }
    }
  }

  static i18n () {
    return MxLocale._i18n
  }

  static supports () {
    return MxLocale._supports
  }

  static mergeMessage (lang, message) {
    if (lang && typeof lang === 'string' && lang.length > 0 && message && message instanceof Object) {
      MxLocale._i18n.mergeLocaleMessage(lang, message)
    }
  }

  static mergeMessages (messages) {
    if (messages && messages instanceof Object) {
      Object.keys(messages).map(k => {
        if (MxLocale._supports.indexOf(k) >= 0) {
          // 仅合并在支持列表中的语种资源
          this.mergeMessage(k, messages[k])
        }
      })
    }
  }
}

MxLocale._lang = 'en'
if (window.localStorage) {
  let lang = window.localStorage.getItem('locale')
  if (lang && typeof lang === 'string' && lang.length > 0) {
    MxLocale._lang = lang
  }
}
MxLocale._i18n = new VueI18n({
  locale: MxLocale._lang,
  fallbackLocale: 'en',
  messages: {}
})
MxLocale._supports = ['en', 'zh-CN']

export default MxLocale

export { MxLocale }
