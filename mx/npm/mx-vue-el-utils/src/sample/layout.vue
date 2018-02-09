<template>
  <mx-normal-layout :title="title" :login-user="loginUser" :navData="navData"
                    :notice-value="noticeValue" v-on:clickMenu="handleClickMenu"
                    v-on:goto="handleGoto" v-on:showNotice="handleShowNotice">
    <div slot="account-info" style="color: red;">登入时间：453453</div>
    <router-view slot="content-body"></router-view>
  </mx-normal-layout>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import { navData } from './router'

  export default {
    name: 'app',
    data () {
      return {
        title: 'The Demo System',
        loginUser: {
          code: 'pengmx',
          name: '彭明喜',
          roles: ['admin'],
          favorityTools: ['/tests/notify', '/tests/tag', '/tests/page']
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
      handleGoto (path) {
        logger.debug('Router click: %s', path)
        this.$router.push(path)
      },
      handleClickMenu (menu) {
        logger.debug('click menu: %s.', menu)
      }
    }
  }
</script>
