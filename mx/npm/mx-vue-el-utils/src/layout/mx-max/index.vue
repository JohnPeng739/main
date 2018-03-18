<template>
  <el-container>
    <el-header :height="headerHeight" class="layout-max__header">
      <mx-max-header v-model="headerHidden" :title="title" :login-user="loginUser" :nav-data="navData" :roles="roles"
                     :tools="favoriteTools" :notice-value="noticeValue" v-on:changeLocale="handleChangeLocale"
                     v-on:clickpersonalMenu="handleClickPersonalMenu" v-on:showNotice="handleShowNotice"></mx-max-header>
    </el-header>
    <el-main class="layout-max__main">
      <div class="content-body">
        <transition name="el-zoom-in-top">
          <slot name="content-body"></slot>
        </transition>
      </div>
    </el-main>
  </el-container>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import MxMaxHeader from './header.vue'

  export default {
    name: 'mx-max-layout',
    components: {MxMaxHeader},
    props: {
      title: {
        type: String,
        default: 'Application title'
      },
      loginUser: {
        type: Object,
        default: undefined
      },
      navData: {
        type: Array,
        default: []
      },
      noticeValue: {
        type: Number,
        deault: 0
      }
    },
    data () {
      return {
        headerHidden: false
      }
    },
    computed: {
      headerHeight () {
        return (this.headerHidden ? 10 : 56) + 'px'
      },
      roles () {
        let user = this.loginUser
        if (user && user.roles && user.roles instanceof Array && user.roles.length > 0) {
          return user.roles
        } else {
          return []
        }
      },
      isRole (item) {
        let roles = this.roles
        return (roles && roles instanceof Array && roles.indexOf(item.role) >= 0) || !item.role
      },
      favoriteTools () {
        let user = this.loginUser
        let favorite = []
        if (user && user.favoriteTools && user.favoriteTools instanceof Array && user.favoriteTools.length > 0) {
          let tools = user.favoriteTools
          tools.forEach(tool => {
            let item = this.getMenuItem(tool)
            if (item) {
              favorite.push(item)
            }
          })
        }
        return favorite
      }
    },
    methods: {
      findMenuItem (path, items) {
        let found
        if (items && items.length > 0) {
          for (let index = 0; index < items.length; index++) {
            let item = items[index]
            if (path === item.path) {
              found = item
            }
            if (found) {
              break
            }
            if (item.children && item.children.length > 0) {
              found = this.findMenuItem(path, item.children)
            }
          }
        }
        return found
      },
      getMenuItem (path) {
        return this.findMenuItem(path, this.navData)
      },
      handleChangeLocale (lang) {
        logger.debug('change locale: %s.', lang)
        this.$emit('changeLocale', lang)
      },
      handleClickPersonalMenu (menu) {
        this.$emit('clickPersonalMenu', menu)
      },
      handleShowNotice () {
        this.$emit('showNotice')
      }
    }
  }
</script>
