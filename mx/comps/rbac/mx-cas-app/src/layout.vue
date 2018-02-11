<template>
  <div>
    <mx-normal-layout v-if="authenticated" :title="title" :login-user="loginUser" :navData="navData"
                      v-on:clickMenu="handleClickMenu" v-on:showNotice="handleShowNotice" v-on:goto="handleGoto">
      <div slot="account-info" style="color:red;">登录时间： Now()</div>
      <router-view slot="content-body"></router-view>
    </mx-normal-layout>
    <router-view v-else></router-view>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {logger} from 'mx-app-utils'
  import {MxNotify} from 'mx-vue-el-utils'
  import {navData} from './router'

  export default {
    name: 'layout',
    data() {
      return {
        navData: navData,
        title: this.$t('title')
      }
    },
    computed: {
      ...mapGetters({
        authenticated: 'authenticated',
        loginUser: 'loginUser'
      })
    },
    methods: {
      ...mapActions({
        logout: 'logout'
      }),
      handleGoto(path) {
        logger.debug('Router click: %s', path)
        this.$router.push(path)
      },
      handleLogout() {
        let success = (data) => {
          if (data && data.account) {
            let {code, name} = data.account
            sessionStorage.removeItem('auth.user')
            MxNotify.info(this.$t('rbac.account.message.logoutSuccess', {code, name}))
            this.$router.push('/login')
          }
        }
        this.logout({success})
      },
      handleClickMenu(menu) {
        switch (menu) {
          case 'logout':
            this.handleLogout()
            break
          case 'changePassword':
            this.handleGoto('/personal/changePassword')
            break
          case 'mySetting':
            this.handleGoto('/personal/mySetting')
            break
          default:
            logger.debug('Click a unsupported menu: %s.', menu)
            break
        }
      },
      handleShowNotice() {
        logger.debug('show notify.')
      }
    }
  }
</script>
