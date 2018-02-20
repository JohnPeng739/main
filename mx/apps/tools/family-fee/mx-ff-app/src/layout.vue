<template>
  <div>
    <mx-normal-layout v-if="authenticated" :title="title" :login-user="loginUser" :navData="navData"
                      v-on:clickMenu="handleClickMenu" v-on:showNotice="handleShowNotice" v-on:goto="handleGoto">
      <div v-if="loginTime > 0" slot="account-info" class="account-info">
        <div class="title">{{$t('pages.layout.loginTime')}}</div>
        <div class="loginTime">{{formatter.formatDatetime(loginTime)}}</div>
      </div>
      <router-view slot="content-body"></router-view>
    </mx-normal-layout>
    <router-view v-else></router-view>
  </div>
</template>

<script>
  import { mapGetters, mapActions } from 'vuex'
  import { logger, formatter } from 'mx-app-utils'
  import { MxNotify, MxNormalLayout } from 'mx-vue-el-utils'
  import { navData } from './router'

  export default {
    name: 'layout',
    components: {MxNormalLayout},
    data () {
      return {
        formatter: formatter,
        navData: navData,
        title: this.$t('title')
      }
    },
    computed: {
      ...mapGetters({
        authenticated: 'authenticated',
        loginUser: 'loginUser'
      }),
      loginTime () {
        let time = sessionStorage.getItem('auth.time')
        if (time > 0) {
          return time * 1
        } else {
          return 0
        }
      }
    },
    methods: {
      ...mapActions({
        logout: 'logout'
      }),
      handleGoto (path) {
        logger.debug('Router click: %s', path)
        this.$router.push(path)
      },
      handleLogout () {
        let success = (data) => {
          if (data && data.account) {
            let {code, name} = data.account
            sessionStorage.removeItem('auth.user')
            sessionStorage.removeItem('auth.time')
            MxNotify.info(this.$t('rbac.account.message.logoutSuccess', {code, name}))
            this.$router.push('/login')
          }
        }
        this.logout({success})
      },
      handleClickMenu (menu) {
        switch (menu) {
          case 'logout':
            this.handleLogout()
            break
          case 'changePassword':
          case 'mySetting':
            let user = this.loginUser
            if (user && user.code === 'admin') {
              MxNotify.warn(this.$t('pages.layout.message.adminEditPermissionDenied'))
              return
            }
            this.handleGoto(menu === 'mySetting' ? '/personal/mySetting' : '/personal/changePassword')
            break
          default:
            logger.debug('Click a unsupported menu: %s.', menu)
            break
        }
      },
      handleShowNotice () {
        logger.debug('show notify.')
      }
    }
  }
</script>

<style rel="stylesheet/less" lang="less" scoped>
  @import "./assets/style/base.less";

  .account-info {
    color: @nav-color;
    .title {
      padding: 0 0 0 5px;
      font-weight: 600;
    }
    .loginTime {
      float: right;
      padding: 10px 0 0 0;
    }
  }
</style>
