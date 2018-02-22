<style rel="stylesheet/less" lang="less">
  @import "element-ui/lib/theme-chalk/display.css";
  @import "./style/base.less";
  /*
  @import "../../dist/style/less/button.less";
  @import "../../dist/style/less/layout-normal.less";
  @import "../../dist/style/less/dialog.less";
  @import "../../dist/style/less/tag.less";
  @import "../../dist/style/less/pagination.less";
  */
  @import "../style/button.less";
  @import "../style/password.less";
  @import "../style/layout-normal.less";
  @import "../style/dialog.less";
  @import "../style/tag.less";
  @import "../style/pagination.less";

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

  body {
    margin: 0;
  }
</style>

<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
  export default {
    name: 'application'
  }
</script>
