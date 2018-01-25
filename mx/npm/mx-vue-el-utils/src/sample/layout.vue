<template>
  <layout-normal :title="title" :loginUserName="loginUserName" :role="role" :tools="tools" :navData="navData"
                 notice-path="notice" :notice-value="noticeValue" v-on:logout="handleLogout"
                 v-on:showUserInfo="handleShowUserInfo" v-on:goto="handleGoto" v-on:showNotice="handleShowNotice">
    <router-view slot="content-body"></router-view>
  </layout-normal>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import LayoutNormal from '@/layout/mx-normal/index.vue'
  // import {LayoutNormal} from '../../dist/mx-vue-el-utils.min'
  import { navData } from './router'

  export default {
    name: 'app',
    components: {LayoutNormal},
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
      },
      handleShowUserInfo () {
        logger.debug('show user info: ' + this.loginUserName + '.')
      }
    }
  }
</script>
