<template>
  <layout-normal :title="title" :loginUserName="loginUserName" :role="role" :tools="tools" :navData="navData"
                 v-on:logout="handleLogout" v-on:showUserInfo="handleShowUserInfo" v-on:goto="handleGoto">
  </layout-normal>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import LayoutNormal from '@/layout/normal/index.vue'
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
        navData: navData
      }
    },
    computed: {
      loginUserName () {
        return 'John Peng'
      }
    },
    methods: {
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
