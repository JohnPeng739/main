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
  import MxAccountSetting from '../../components/rbac/mx-account-setting'
  import { navData } from '../router'

  export default {
    components: {MxAccountSetting},
    data () {
      return {
        navData: navData
      }
    }
  }
</script>
