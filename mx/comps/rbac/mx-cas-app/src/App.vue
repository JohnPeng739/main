<template>
  <router-view/>
</template>

<script>
  export default {
    name: 'App'
  }
</script>

<style rel="stylesheet/less" lang="less">
  @import "element-ui/lib/theme-chalk/display.css";
  @import "./assets/style/base.less";
  @import "~mx-vue-el-appcomps/dist/style/less/rbac.less";
  @import "~mx-vue-el-utils/dist/style/less/button.less";
  @import "~mx-vue-el-utils/dist/style/less/dialog.less";
  @import "~mx-vue-el-utils/dist/style/less/tag.less";
  @import "~mx-vue-el-utils/dist/style/less/pagination.less";
  @import "~mx-vue-el-utils/dist/style/less/layout-normal.less";

  html {
    font-family: "Source Sans Pro", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    font-size: 16px;
    word-spacing: 1px;
    -ms-text-size-adjust: 100%;
    -webkit-text-size-adjust: 100%;
    -moz-osx-font-smoothing: grayscale;
    -webkit-font-smoothing: antialiased;
    box-sizing: border-box;
  }

  *, *:before, *:after {
    box-sizing: border-box;
    margin: 0;
  }

  @font-face {
    font-family: 'Material Icons';
    font-style: normal;
    font-weight: 400;
    src: url('./assets/iconfont/MaterialIcons-Regular.eot'); /* For IE6-8 */
    src: local('Material Icons'),
    local('MaterialIcons-Regular'),
    url('./assets/iconfont/MaterialIcons-Regular.woff2') format('woff2'),
    url('./assets/iconfont/MaterialIcons-Regular.woff') format('woff'),
    url('./assets/iconfont/MaterialIcons-Regular.ttf') format('truetype');
  }

  .material-icons {
    font-family: 'Material Icons';
    font-weight: normal;
    font-style: normal;
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

  .body {
    margin: 0;
    padding: 0;
  }
</style>
