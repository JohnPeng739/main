<template>
  <mx-normal-layout :title="title" :login-user="loginUser"
                    :navData="transformedNavData"
                    v-on:clickMenu="handleClickMenu" v-on:showNotice="handleShowNotice" v-on:goto="handleGoto">
    <router-view slot="content-body"></router-view>
  </mx-normal-layout>
</template>

<script>
  import { mapGetters, mapActions } from 'vuex'
  import { logger } from 'mx-app-utils'
  import { MxNotify } from 'mx-vue-el-utils'
  import { navData } from './router'

  export default {
    name: 'layout',
    data () {
      return {
        title: 'The Demo System'
      }
    },
    computed: {
      ...mapGetters({
        authenticated: 'authenticated',
        loginUser: 'loginUser'
      }),
      transformedNavData: {
        get () {
          return navData
        },
        set (val) {}
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
        if (this.authenticated) {
          let success = (data) => {
            if (data && data.account) {
              let {code, name} = data.account
              sessionStorage.removeItem('auth.user')
              MxNotify.info(this.$t('rbac.account.message.logoutSuccess', {code, name}))
              this.$router.push('/login')
            }
          }
          this.logout({success})
        }
      },
      handleClickMenu (menu) {
        switch (menu) {
          case 'logout':
            this.handleLogout()
            break
          case 'changePassword':
            this.$router.push('/personal/changePassword')
            break
          case 'mySetting':
            this.$router.push('/personal/mySetting')
            break
          default:
            logger.debug('Click the menu: %s.', menu)
            break
        }
      },
      handleShowNotice () {
        logger.debug('show notice page.')
      }
    }
  }
</script>
