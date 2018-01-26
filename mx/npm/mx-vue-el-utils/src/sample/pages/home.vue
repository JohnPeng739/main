<template>
  <div>
    <h1>测试首页</h1>
    <img src="../assets/logo.png">
    <br/><br/>
    some material icon:
    <mx-icon></mx-icon>
    <mx-icon name="home"></mx-icon>
    <mx-icon name="build"></mx-icon>
    <mx-icon name="3d_rotation"></mx-icon>
    <br/><br/>
    <el-button @click="handleGoto('/tests/notify')">Notify</el-button>
    <el-button @click="handleGoto('/tests/tag')">Tag</el-button>
    <el-button @click="handleGoto('/tests/page')">Page</el-button>
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
