<style rel="stylesheet/less" lang="less" scoped>
  @font-face {
    font-family: 'Material Icons';
    font-style: normal;
    font-weight: 400;
    src: url('../assets/iconfont/MaterialIcons-Regular.eot'); /* For IE6-8 */
    src: local('Material Icons'),
    local('MaterialIcons-Regular'),
    url('../assets/iconfont/MaterialIcons-Regular.woff2') format('woff2'),
    url('../assets/iconfont/MaterialIcons-Regular.woff') format('woff'),
    url('../assets/iconfont/MaterialIcons-Regular.ttf') format('truetype');
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
</style>

<template>
  <i class="material-icons">
    {{this.name}}
  </i>
</template>

<script>
  import 'element-ui/'

  export default {
    name: 'DsIcon',
    data () {
      return {
        active: false
      }
    },
    props: {
      name: {
        type: String,
        default: 'apps'
      }
    }
  }
</script>
