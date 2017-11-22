<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";

  @font-face {
    font-family: 'Material Icons';
    font-style: normal;
    font-weight: 400;
    src: url('../../../assets/iconfont/MaterialIcons-Regular.eot'); /* For IE6-8 */
    src: local('Material Icons'),
    local('MaterialIcons-Regular'),
    url('../../../assets/iconfont/MaterialIcons-Regular.woff2') format('woff2'),
    url('../../../assets/iconfont/MaterialIcons-Regular.woff') format('woff'),
    url('../../../assets/iconfont/MaterialIcons-Regular.ttf') format('truetype');
  }

  .material-icons {
    font-family: 'Material Icons';
    font-weight: normal;
    font-style: normal;
    font-size: 24px; /* Preferred icon size */
    display: inline-block;
    line-height: 1;
    text-transform: none;
    letter-spacing: normal;
    word-wrap: normal;
    white-space: nowrap;
    direction: ltr;
    /* Support for all WebKit browsers. */
    -webkit-font-smoothing: antialiased;
    /* Support for Safari and Chrome. */
    text-rendering: optimizeLegibility;
    /* Support for Firefox. */
    -moz-osx-font-smoothing: grayscale;
    /* Support for IE. */
    font-feature-settings: 'liga';
  }

  body {
    margin: 0;
  }

  .border {
    border: 1px solid red;
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
    margin-top: @header-height;
    width: 100%;
    height: 100%;
    z-index: 100;
    background-color: lighten(@header-bg-color, 50%);
    .layout-nav {
      background: @nav-color;
    }
    .nav-show {
      min-width: 200px;
    }
    .nav-hide {
      min-width: 0px;
    }
    .breadcrumb {
      padding: 20px 0 0 10px;
      .breadcrumb-item {
        color: @nav-color;
        font-weight: bolder;
      }
    }
    .content-body {
      min-height: @content-min-height;
      margin: 10px;
      overflow: hidden;
      background: @content-bg-color;
      border-radius: 4px;
      .layout-content-main {
        padding: 20px 10px;
      }
    }
  }
</style>

<template>
  <div class="layout-main">
    <div class="header-row">
      <nav-header :title="title" :loginUserName="name" v-on:navToggled="handleNavToggled"
                  v-on:logout="handleLogout" v-on:showUserInfo="handleShowUserInfo">
        <nav-tools :tools="tools"></nav-tools>
      </nav-header>
    </div>
    <el-row type="flex" class="content-row">
      <el-col :span="spanLeft" :class="['layout-nav', spanLeft !== 0 ? 'nav-show' : 'nav-hide']">
        <nav-menu :spanLeft="spanLeft"></nav-menu>
      </el-col>
      <el-col :span="spanRight">
        <el-breadcrumb>
          <el-breadcrumb-item class="breadcrumb">
            当前位置：
            <span class="breadcrumb-item">{{pathName}}</span>
          </el-breadcrumb-item>
        </el-breadcrumb>
        <div class="content-body">
          <transition name="el-zoom-in-top">
            <router-view></router-view>
          </transition>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import NavHeader from './header.vue'
  import NavMenu from './nav-menu.vue'
  import NavTools from './nav-tools.vue'
  import config from '../../config'
  import {getNavTitle} from '../router'

  export default {
    components: {NavHeader, NavMenu, NavTools},
    data () {
      return {
        spanLeft: 4,
        spanRight: 20,
        title: config.title
      }
    },
    computed: {
      ...mapGetters(['code', 'name', 'tools', 'authenticated', 'path', 'pathName']),
      getPathTitle () {
        return getNavTitle(this.$route.path)
      },
      success () {
        return this.authenticated
      }
    },
    methods: {
      ...mapActions(['logout', 'goto', 'cacheLoad']),
      handleNavToggled () {
        if (this.spanLeft === 4) {
          this.spanLeft = 0
          this.spanRight = 24
        } else {
          this.spanLeft = 4
          this.spanRight = 20
        }
      },
      handleLogout () {
        let success = () => {
          this.goto({owner: this, path: '/login'})
          this.$message('成功登出系统。')
        }
        this.logout({success})
      },
      handleShowUserInfo() {
        this.goto({owner: this, path: '/user-info/' + this.code, name: '用户信息'})
      }
    },
    mounted() {
      this.cacheLoad()
    }
  }
</script>
