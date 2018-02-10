<style rel="stylesheet/less" lang="less" scoped>
  .layout {
    width: 80%;
    margin: 50px auto;
  }
</style>

<template>
  <div class="layout">
    <mx-change-password></mx-change-password>
  </div>
</template>

<script>
  import MxChangePassword from '../../components/rbac/mx-change-password'

  export default {
    components: {MxChangePassword}
  }
</script>
