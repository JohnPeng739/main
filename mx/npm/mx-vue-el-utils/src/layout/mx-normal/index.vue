<template>
  <el-container>
    <el-header height="10vh" class="layout-header">
      <layout-header :title="title" :login-user="loginUser" :nav-data="navData"
                     v-on:navToggled="handleNavToggled" v-on:clickMenu="handleClickMenu">
        <div slot="account-info"><slot name="account-info"></slot></div>
        <layout-nav-favority-tools slot="favority-tools" class="favority-tools hidden-xs-only hidden-sm-only"
                                   :roles="roles"
                                   :tools="favorityTools"
                                   :notice-value="noticeValue" v-on:goto="handleGoto"
                                   v-on:showNotice="handleShowNotice">
        </layout-nav-favority-tools>
      </layout-header>
    </el-header>
    <el-main class="layout-main">
      <layout-nav-menu :toggled="toggled()" :roles="roles" :nav-data="navData" v-on:goto="handleGoto" class="layout-nav">
      </layout-nav-menu>
      <div class="layout-right">
        <el-breadcrumb>
          <el-breadcrumb-item>
            {{$t('message.title.current')}}
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
  import LayoutHeader from './header.vue'
  import LayoutNavFavorityTools from './nav-favority-tools.vue'
  import LayoutNavMenu from './nav-menu.vue'

  export default {
    name: 'mx-normal-layout',
    components: {LayoutHeader, LayoutNavFavorityTools, LayoutNavMenu},
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
      favorityTools () {
        let user = this.loginUser
        let favority = []
        if (user && user.favorityTools && user.favorityTools instanceof Array && user.favorityTools.length > 0) {
          let tools = user.favorityTools
          tools.forEach(tool => {
            let item = this.getMenuItem(tool)
            if (item) {
              favority.push(item)
            }
          })
        }
        return favority
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
        this.$emit('showNotice')
      },
      handleGoto (path) {
        this.$emit('goto', path)
      },
      handleNavToggled () {
        logger.debug('click the nav toggle, old: %s.', this.toggleState)
        this.toggleState = !this.toggleState
      },
      handleClickMenu (menu) {
        this.$emit('clickMenu', menu)
      }
    }
  }
</script>
