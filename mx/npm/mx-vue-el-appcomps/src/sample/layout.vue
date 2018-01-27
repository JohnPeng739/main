<template>
  <mx-normal-layout :title="title" :loginUserName="loginUserName" :role="role" :tools="tools" :navData="navData"
                    v-on:logout="handleLogout" v-on:showUserInfo="handleShowUserInfo" v-on:goto="handleGoto">
    <router-view slot="content-body"></router-view>
  </mx-normal-layout>
</template>

<script>
  import { mapGetters, mapActions } from 'vuex'
  import { logger } from 'mx-app-utils'
  import { navData } from './router'

  export default {
    name: 'layout',
    data () {
      return {
        title: 'The Demo System',
        loginUserName: 'NA',
        role: 'admin',
        tools: [],
        navData: navData
      }
    },
    computed: {
      ...mapGetters(['authenticated', 'loginUser'])
    },
    methods: {
      ...mapActions(['logout']),
      handleGoto (path) {
        logger.debug('Router click: %s', path)
        this.$router.push(path)
      },
      handleLogout () {
        let success = (data) => {
          if (data && data.account) {
            let {code, name} = data.account
            this.$mxInfo(this.$t('rbac.message.logoutSuccess', {code, name}))
            this.$router.push('/login')
          }
        }
        this.logout({success})
      },
      handleShowUserInfo () {
        logger.debug('show user info: ' + this.loginUserName + '.')
      }
    },
    mounted () {
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
