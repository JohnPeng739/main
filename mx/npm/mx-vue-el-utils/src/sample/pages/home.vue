<template>
  <div>
    <h1>测试首页</h1>
    <img src="../assets/logo.png">
    <br/>
    <el-button @click="handleGoto('/notify')">Notify</el-button>
  </div>
</template>

<script>
  export default {
    name: 'home-page',
    data () {
      return {}
    },
    methods: {
      handleGoto (path) {
        this.$router.push(path)
      }
    }
  }
</script>
