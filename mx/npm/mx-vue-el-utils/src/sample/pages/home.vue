<template>
  <div>
    <h1>测试首页</h1>
    <img src="../assets/logo.png">
    <br/><br/>
    some material icon:
    <icon></icon>
    <icon name="home"></icon>
    <icon name="build"></icon>
    <icon name="3d_rotation"></icon>
    <br/><br/>
    <el-button @click="handleGoto('/notify')">Notify</el-button>
    <el-button @click="handleGoto('/tag')">Tag</el-button>
  </div>
</template>

<script>
  import Icon from '@/components/icon'

  export default {
    name: 'home-page',
    components: {Icon},
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
