<template>
  <div>
    <mx-normal-layout v-if="layout === 'normal'" :title="title" :login-user="loginUser" :nav-data="navData"
                      :notice-value="noticeValue" v-on:changeLocale="handleChangeLocale"
                      v-on:clickPersonalMenu="handleClickPersonalMenu" v-on:goto="handleGoto"
                      v-on:showNotice="handleShowNotice">
      <div slot="account-info" style="color: red;">登入时间：453453</div>
      <router-view slot="content-body"></router-view>
    </mx-normal-layout>
    <mx-max-layout v-else-if="layout === 'max'" :title="title" :login-user="loginUser" :nav-data="navData"
                   :notice-value="noticeValue" v-on:changeLocale="handleChangeLocale"
                   v-on:clickPersonalMenu="handleClickPersonalMenu" v-on:goto="handleGoto"
                   v-on:showNotice="handleShowNotice">
      <div slot="account-info" style="color: blue;">登入时间：123456</div>
      <router-view slot="content-body"></router-view>
    </mx-max-layout>
  </div>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import { navData } from './router'
  import { MxLocale } from '../utils/mx-locale'

  export default {
    name: 'app',
    data () {
      return {
        layout: 'max',
        title: 'The Demo System',
        loginUser: {
          code: 'pengmx',
          name: '彭明喜',
          roles: ['admin'],
          favoriteTools: ['/tests/notify', '/tests/tag', '/tests/page']
        },
        navData: navData,
        noticeValue: 123
      }
    },
    computed: {},
    methods: {
      handleShowNotice () {
        this.noticeValue = 0
        logger.debug('show notices.')
      },
      handleChangeLocale (lang) {
        MxLocale.changeLanguage(lang)
      },
      handleGoto (path) {
        logger.debug('Router click: %s', path)
        this.$router.push(path)
      },
      handleClickPersonalMenu (menu) {
        logger.debug('click menu: %s.', menu)
      }
    }
  }
</script>
