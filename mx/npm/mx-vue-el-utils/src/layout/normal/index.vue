<template>
  <div class="layout-main">
    <div class="header-row">
      <layout-header :title="title" :loginUserName="loginUserName" :navData="navData"
                     v-on:navToggled="handleNavToggled" v-on:logout="handleLogout"
                     v-on:showUserInfo="handleShowUserInfo">
        <layout-nav-favority-tools :role="role" :favorityTools="favorityTools" v-on:goto="handleGoto"></layout-nav-favority-tools>
      </layout-header>
    </div>
    <div type="flex" class="content-row">
      <layout-nav-menu :toggled="toggled" :role="role" :navData="navData" v-on:goto="handleGoto" class="layout-nav"></layout-nav-menu>
      <div class="layout-right">
        <el-breadcrumb>
          <el-breadcrumb-item>
            {{$t('message.title.current')}}
            <span class="breadcrumb-item">{{pathName}}</span>
          </el-breadcrumb-item>
        </el-breadcrumb>
        <div class="content-body">
          <transition name="el-zoom-in-top">
            <router-view></router-view>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import LayoutHeader from './header.vue'
  import LayoutNavFavorityTools from './nav-favority-tools.vue'
  import LayoutNavMenu from './nav-menu.vue'

  export default {
    name: 'layout-normal',
    components: {LayoutHeader, LayoutNavFavorityTools, LayoutNavMenu},
    props: ['title', 'loginUserName', 'role', 'tools', 'navData'],
    data () {
      return {
        toggled: false
      }
    },
    computed: {
      pathName () {
        let path = this.$route.path
        if (path) {
          let item = this.getMenuItem(path)
          if (item) {
            return item.name
          }
        }
        return 'NA'
      },
      favorityTools () {
        let tools = this.tools
        let navData = this.navData
        let favority = []
        if (tools && navData) {
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
      findMenuItem (path, items) {
        if (items && items.length > 0) {
          for (let index = 0; index < items.length; index++) {
            let item = items[index]
            if (path === item.path) {
              return item
            }
            if (item.children && item.children.length > 0) {
              return this.findMenuItem(path, item.children)
            }
          }
        }
        return undefined
      },
      getMenuItem (path) {
        return this.findMenuItem(path, this.navData)
      },
      handleGoto (path) {
        this.$emit('goto', path)
      },
      handleNavToggled () {
        logger.debug('click the nav toggle, old: %s.', this.toggled)
        this.toggled = !this.toggled
      },
      handleLogout () {
        this.$emit('logout')
      },
      handleShowUserInfo () {
        this.$emit('showUserInfo')
      }
    }
  }
</script>
