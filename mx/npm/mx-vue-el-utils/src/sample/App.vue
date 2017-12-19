<style rel="stylesheet/less" lang="less">
  @font-face {
    font-family: 'Material Icons';
    font-style: normal;
    font-weight: 400;
    src: url('./assets/iconfont/MaterialIcons-Regular.eot'); /* For IE6-8 */
    src: local('Material Icons'),
    local('MaterialIcons-Regular'),
    url('./assets/iconfont/MaterialIcons-Regular.woff2') format('woff2'),
    url('./assets/iconfont/MaterialIcons-Regular.woff') format('woff'),
    url('./assets/iconfont/MaterialIcons-Regular.ttf') format('truetype');
  }

  .material-icons {
    font-family: 'Material Icons';
    font-weight: normal;
    font-style: normal;
    font-size: 24px; /* Preferred icon size */
    display: inline-block;
    line-height: 1;
    text-transform: none;
    letter-spacing: normal;
    word-wrap: normal;
    white-space: nowrap;
    direction: ltr;
    /* Support for all WebKit browsers. */
    -webkit-font-smoothing: antialiased;
    /* Support for Safari and Chrome. */
    text-rendering: optimizeLegibility;
    /* Support for Firefox. */
    -moz-osx-font-smoothing: grayscale;
    /* Support for IE. */
    font-feature-settings: 'liga';
  }

  body {
    margin: 0;
  }
</style>

<template>
  <layout-normal :title="title" :loginUserName="loginUserName" :role="role" :tools="tools" :navData="navData"
                 v-on:logout="handleLogout" v-on:showUserInfo="handleShowUserInfo" v-on:goto="handleGoto">
  </layout-normal>
</template>

<script>
  import {logger} from 'mx-app-utils'
  // import LayoutNormal from '@/layout/normal/index.vue'
  import {LayoutNormal} from '../../dist/mx-vue-el-utils.min'
  import '../../dist/css/index.min.css'
  import { navData } from './router'

  export default {
    name: 'app',
    components: {LayoutNormal},
    data () {
      return {
        title: 'The Demo System',
        loginUserName: 'Josh Peng',
        role: 'admin',
        tools: ['/tests/notify', '/tests/tag', '/tests/page'],
        navData: navData
      }
    },
    methods: {
      handleGoto (path) {
        logger.debug('Router click: %s', path)
        this.$router.push(path)
      },
      handleLogout () {
        logger.debug('logout')
      },
      handleShowUserInfo () {
        logger.debug('show user info: ' + this.loginUserName + '.')
      }
    }
  }
</script>
