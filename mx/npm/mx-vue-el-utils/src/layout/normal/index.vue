<style rel="stylesheet/less" lang="less">
  @import "../../style/base.less";

  body {
    margin: 0;
  }

  .border {
    border: 1px solid @content-color;
  }

  .layout-main {
    overflow: hidden;
  }

  .header-row {
    background-color: @header-bg-color;
    color: @header-color;
    width: 100%;
    height: @header-height;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 999;
  }

  .content-row {
    overflow: hidden;
    margin-top: @header-height;
    width: 100%;
    height: 100%;
    z-index: 100;
    background-color: lighten(@header-bg-color, 50%);
    .layout-nav {
      float: left;
      vertical-align: top;
      min-height: @content-min-height;
    }
    .layout-right {
      margin-top: 10px;
      overflow: hidden;
      .breadcrumb-item {
        color: @content-color;
        font-style: italic;
      }
      .content-body {
        min-height: @content-min-height - 45px;
        margin: 10px;
        background: @content-bg-color;
        border-radius: 4px;
      }
    }
  }
</style>

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
            当前位置：
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
