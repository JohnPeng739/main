<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .toolbar {
    float: right;
    padding-right: 20px;
    .tools {
      text-align: center;
      width: @button-width - 30px;
      color: @header-color;
      &:hover {
        color: @header-hover-color;
      }
      .tool-icon {
        font-size: 24px;
        padding-top: 10px;
      }
      .tool-title {
        font-size: 12px;
        padding-top: 2px;
      }
    }
  }
</style>

<template>
  <div class="toolbar">
    <el-button v-for="item in favorityTools" v-if="isRole(item)" :key="item.path" @click="handleGoto(item.path)"
               type="text" class="tools">
      <icon :name="item.icon" class="tool-icon"></icon>
      <div class="tool-title">{{item.name}}</div>
    </el-button>
  </div>
</template>

<script>
  import Icon from '@/components/icon.vue'

  export default {
    name: 'layout-normal-favority-tools',
    components: {Icon},
    props: ['role', 'favorityTools'],
    data () {
      return {}
    },
    methods: {
      isRole (item) {
        let role = this.role
        return (role && role === item.role) || !item.role
      },
      handleGoto (path) {
        // if (path && typeof path === 'string') {
        //  this.$router.push(path)
        // }
        this.$emit('goto', path)
      }
    }
  }
</script>
