<template>
  <div>
    <mx-normal-layout v-if="authenticated" :title="title" :login-user="loginUser" :navData="navData"
                      v-on:clickPersonalMenu="handleClickPersonalMenu" v-on:showNotice="handleShowNotice"
                      v-on:changeLocale="handleChangeLocale" v-on:goto="handleGoto">
      <div v-if="loginTime > 0" slot="account-info" class="account-info">
        <div class="title">{{$t('common.loginTime')}}</div>
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
  import { MxNotify } from 'mx-vue-el-utils'

  export default {
    name: 'rbac-layout',
    props: ['title', 'navData'],
    data () {
      return {
        formatter: formatter
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
        this.$router.push(path)
      },
      handleChangeLocale (lang) {
        this.$emit('changeLocale', lang)
      },
      handleLogout () {
        let success = (data) => {
          if (data && data.account) {
            let {code, name} = data.account
            sessionStorage.removeItem('auth.user')
            sessionStorage.removeItem('auth.time')
            MxNotify.info(this.$t('pages.account.message.logoutSuccess', [code, name]))
            this.$router.push('/login')
          }
        }
        this.logout({success})
      },
      handleClickPersonalMenu (menu) {
        switch (menu) {
          case 'logout':
            this.handleLogout()
            break
          case 'changePassword':
          case 'mySetting':
            let user = this.loginUser
            if (user && user.code === 'admin') {
              MxNotify.warn(this.$t('pages.account.message.adminEditPermissionDenied'))
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
        this.emit('showNotice')
      }
    }
  }
</script>
