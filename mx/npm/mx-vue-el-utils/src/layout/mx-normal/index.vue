<template>
  <el-container>
    <el-header height="10vh" class="layout-normal__header">
      <mx-normal-header :title="title" :login-user="loginUser" :nav-data="navData" v-on:changeLocale="handleChangeLocale"
                     v-on:navToggled="handleNavToggled" v-on:clickPersonalMenu="handleClickPersonalMenu">
        <div slot="account-info"><slot name="account-info"></slot></div>
        <mx-normal-nav-favorite-tools slot="favorite-tools" class="favorite-tools hidden-xs-only hidden-sm-only"
                                   :roles="roles"
                                   :tools="favoriteTools"
                                   :notice-value="noticeValue" v-on:goto="handleGoto"
                                   v-on:showNotice="handleShowNotice">
        </mx-normal-nav-favorite-tools>
      </mx-normal-header>
    </el-header>
    <el-main class="layout-normal__main">
      <mx-normal-nav-menu :toggled="toggled()" :roles="roles" :nav-data="navData" v-on:goto="handleGoto" class="layout-nav">
      </mx-normal-nav-menu>
      <div class="layout-right">
        <el-breadcrumb>
          <el-breadcrumb-item>
            {{$t('common.current')}}
            <span class="breadcrumb-item">{{pathName}}</span>
          </el-breadcrumb-item>
        </el-breadcrumb>
        <div class="content-body">
          <transition name="el-zoom-in-top">
            <slot name="content-body"></slot>
          </transition>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import MxNormalHeader from './header.vue'
  import MxNormalNavFavoriteTools from './nav-favorite-tools.vue'
  import MxNormalNavMenu from './nav-menu.vue'

  export default {
    name: 'mx-normal-layout',
    components: {MxNormalHeader, MxNormalNavFavoriteTools, MxNormalNavMenu},
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
        toggleState: false
      }
    },
    computed: {
      pathName () {
        let path = this.$route.path
        if (path) {
          let item = this.getMenuItem(path)
          if (item) {
            return this.$t(item.name)
          }
        }
        return 'NA'
      },
      roles () {
        let user = this.loginUser
        if (user && user.roles && user.roles instanceof Array && user.roles.length > 0) {
          return user.roles
        } else {
          return []
        }
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
      toggled () {
        let clientWidth = 1024
        if (this.$el) {
          clientWidth = this.$el.clientWidth
        }
        return this.toggleState || clientWidth < 640
      },
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
      handleShowNotice () {
        logger.debug('Click the show notice tool.')
        this.$emit('showNotice')
      },
      handleGoto (path) {
        logger.debug('Goto page: %s.', path)
        this.$emit('goto', path)
      },
      handleChangeLocale (lang) {
        logger.debug('change locale: %s.', lang)
        this.$emit('changeLocale', lang)
      },
      handleNavToggled () {
        logger.debug('click the nav toggle, old: %s.', this.toggleState)
        this.toggleState = !this.toggleState
      },
      handleClickPersonalMenu (menu) {
        this.$emit('clickPersonalMenu', menu)
      }
    }
  }
</script>
