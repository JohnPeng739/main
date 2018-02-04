<template>
  <div>
    <mx-normal-layout v-if="authenticated" :title="title" :loginUserName="loginUserName" :role="role" :tools="tools"
                      :navData="transformedNavData" v-on:logout="handleLogout" v-on:showUserInfo="handleShowUserInfo"
                      v-on:goto="handleGoto">
      <nuxt slot="content-body"/>
    </mx-normal-layout>
    <nuxt v-else></nuxt>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {logger} from 'mx-app-utils'
  import {MxNotify} from 'mx-vue-el-utils'
  import {navData} from '~/assets/AppData'
  import Nuxt from "../.nuxt/components/nuxt";

  export default {
    components: {Nuxt},
    name: 'layout',
    fetch ({store}) {
      console.log(sessionStorage)
      console.log(sessionStorage.getItem('authUser'))
    },
    data() {
      return {
        navData: navData,
        title: this.$t('title'),
        role: 'admin'
      }
    },
    computed: {
      ...mapGetters({
        authenticated: 'account/authenticated',
        loginUser: 'account/loginUser'
      }),
      loginUserName () {
        return this.authenticated ? this.loginUser.name : this.$t('NA')
      },
      transformedNavData: {
        get() {
          this.transformNavData(this.navData)
          return this.navData
        },
        set(val) {
        }
      },
      tools: {
        get() {
          return this.loginUser && this.loginUser.favorityTools ? this.loginUser.favorityTools : []
        },
        set(val) {
        }
      }
    },
    methods: {
      ...mapActions({
        logout: 'account/logout'
      }),
      transformNavData(list) {
        if (list && list instanceof Array && list.length > 0) {
          list.forEach(item => {
            let name = item.name
            let val = this.$t(name)
            if (val && val.length > 0) {
              item.name = val
            }
            this.transformNavData(item.children)
          })
        }
      },
      handleGoto(path) {
        logger.debug('Router click: %s', path)
        this.$router.push(path)
      },
      handleLogout() {
        let success = (data) => {
          if (data && data.account) {
            let {code, name} = data.account
            /*
            MxAjax.get('/api/logout', data => {
              console.log('******************')
              console.log(data)
            })
            */
            MxNotify.info(this.$t('rbac.message.logoutSuccess', {code, name}))
            this.$router.push('/login')
          }
        }
        this.logout({success})
      },
      handleShowUserInfo() {
        logger.debug('show user info: ' + this.loginUserName + '.')
      }
    },
    mounted() {
      if (this.authenticated) {
        let user = this.loginUser
        if (user && user.name && typeof user.name === 'string' && user.name.length > 0) {
          this.loginUserName = user.name
          this.role = user.role
          this.tools = user.tools
        } else {
          this.loginUserName = this.$t('rbac.common.fields.NA')
          this.role = 'guest'
          this.tools = []
        }
      }
    }
  }
</script>

<style rel="stylesheet/less" lang="less">
  @import "../style/base.less";
  @import "~mx-vue-el-utils/dist/style/less/button.less";
  @import "~mx-vue-el-utils/dist/style/less/layout-normal.less";
  @import "~mx-vue-el-utils/dist/style/less/dialog.less";
  @import "~mx-vue-el-utils/dist/style/less/tag.less";
  @import "~mx-vue-el-utils/dist/style/less/pagination.less";
  @import "~mx-vue-el-appcomps/dist/style/less/rbac.less";

  html {
    font-family: "Source Sans Pro", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    font-size: 16px;
    word-spacing: 1px;
    -ms-text-size-adjust: 100%;
    -webkit-text-size-adjust: 100%;
    -moz-osx-font-smoothing: grayscale;
    -webkit-font-smoothing: antialiased;
    box-sizing: border-box;
  }

  *, *:before, *:after {
    box-sizing: border-box;
    margin: 0;
  }

  @font-face {
    font-family: 'Material Icons';
    font-style: normal;
    font-weight: 400;
    src: url('../assets/iconfont/MaterialIcons-Regular.eot'); /* For IE6-8 */
    src: local('Material Icons'),
    local('MaterialIcons-Regular'),
    url('../assets/iconfont/MaterialIcons-Regular.woff2') format('woff2'),
    url('../assets/iconfont/MaterialIcons-Regular.woff') format('woff'),
    url('../assets/iconfont/MaterialIcons-Regular.ttf') format('truetype');
  }

  .material-icons {
    font-family: 'Material Icons';
    font-weight: normal;
    font-style: normal;
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

  .button--green {
    display: inline-block;
    border-radius: 4px;
    border: 1px solid #3b8070;
    color: #3b8070;
    text-decoration: none;
    padding: 10px 30px;
  }

  .button--green:hover {
    color: #fff;
    background-color: #3b8070;
  }

  .button--grey {
    display: inline-block;
    border-radius: 4px;
    border: 1px solid #35495e;
    color: #35495e;
    text-decoration: none;
    padding: 10px 30px;
    margin-left: 15px;
  }

  .button--grey:hover {
    color: #fff;
    background-color: #35495e;
  }
</style>
