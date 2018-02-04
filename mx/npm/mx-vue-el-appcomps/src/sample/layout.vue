<template>
  <mx-normal-layout :title="title" :loginUserName="loginUserName" :roles="roles" :tools="tools"
                    :navData="transformedNavData"
                    v-on:logout="handleLogout" v-on:showNotice="handleShowNotice" v-on:goto="handleGoto">
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
          this.transformNavData(navData)
          return navData
        },
        set (val) {}
      },
      loginUserName: {
        get () {
          return this.authenticated ? this.loginUser.name : $t('NA')
        },
        set (val) {
        }
      },
      tools: {
        get () {
          return this.loginUser && this.loginUser.favorityTools ? this.loginUser.favorityTools : []
        },
        set (val) {}
      },
      roles: {
        get () {
          return this.authenticated ? this.loginUser.roles : []
        },
        set (val) {
        }
      }
    },
    methods: {
      ...mapActions({
        logout: 'logout'
      }),
      transformNavData (list) {
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
      handleGoto (path) {
        logger.debug('Router click: %s', path)
        this.$router.push(path)
      },
      handleLogout () {
        if (this.authenticated) {
          let success = (data) => {
            if (data && data.account) {
              let {code, name} = data.account
              localStorage.removeItem('auth.user')
              console.log(this.$t('rbac.account.module'))
              console.log(this.$t('rbac.account.message.logoutSuccess', {code, name}))
              MxNotify.info(this.$t('rbac.account.message.logoutSuccess', {code, name}))
              this.$router.push('/login')
            }
          }
          this.logout({success})
        }
      },
      handleShowNotice () {
        logger.debug('show notice page.')
      }
    }
  }
</script>
