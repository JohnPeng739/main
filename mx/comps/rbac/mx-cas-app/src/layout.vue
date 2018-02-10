<template>
  <div>
    <mx-normal-layout v-if="authenticated" :title="title" :login-user="loginUser" :navData="transformedNavData"
                      v-on:clickMenu="handleClickMenu" v-on:showUserInfo="handleShowUserInfo" v-on:goto="handleGoto">
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
      }),
      transformedNavData: {
        get() {
          this.transformNavData(this.navData)
          return this.navData
        },
        set(val) {
        }
      }
    },
    methods: {
      ...mapActions({
        logout: 'logout'
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
            sessionStorage.removeItem('auth.user')
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
          case 'changePassword':
            this.handleGoto('/personal/changePassword')
          case 'mySetting':
            this.handleGoto('/personal/mySetting')
          default:
            logger.debug('Click a unsupported menu: %s.', menu)
        }
      },
      handleShowUserInfo() {
        logger.debug('show user info: ' + this.loginUserName + '.')
      }
    }
  }
</script>
