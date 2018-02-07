<template>
  <mx-normal-layout :title="title" :loginUserName="loginUserName" :role="role" :tools="tools" :navData="navData"
                 :notice-value="noticeValue" v-on:logout="handleLogout"
                 v-on:goto="handleGoto" v-on:showNotice="handleShowNotice">
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
        role: 'admin',
        tools: ['/tests/notify', '/tests/tag', '/tests/page'],
        navData: navData,
        noticeValue: 123
      }
    },
    computed: {
      loginUserName () {
        return 'John Peng'
      }
    },
    methods: {
      handleShowNotice () {
        this.noticeValue = 0
        logger.debug('show notices.')
      },
      handleGoto (path) {
        logger.debug('Router click: %s', path)
        this.$router.push(path)
      },
      handleLogout () {
        logger.debug('Logout.')
      }
    }
  }
</script>
