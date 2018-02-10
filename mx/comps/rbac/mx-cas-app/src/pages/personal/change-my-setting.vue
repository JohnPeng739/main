<style rel="stylesheet/less" lang="less" scoped>
  .layout {
    width: 80%;
    margin: 50px auto;
  }
</style>

<template>
  <div class="layout">
    <mx-account-setting :nav-data="navData"></mx-account-setting>
  </div>
</template>

<script>
  import {navData} from '../../router'

  export default {
    data () {
      return {
        navData: navData
      }
    }
  }
</script>
