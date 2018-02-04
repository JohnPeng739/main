<template>
  <div>
    <mx-normal-layout v-if="authenticated" :title="title" :loginUserName="loginUserName" :role="role" :tools="tools"
                      :navData="transformedNavData" v-on:logout="handleLogout" v-on:showUserInfo="handleShowUserInfo"
                      v-on:goto="handleGoto">
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
        title: this.$t('title'),
        role: 'admin'
      }
    },
    computed: {
      ...mapGetters({
        authenticated: 'authenticated',
        loginUser: 'loginUser'
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
