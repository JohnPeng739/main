<style rel="stylesheet/less" lang="less" scoped>
  @import "../../../style/base.less";

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
        padding-top: 4px;
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
    <el-button v-for="item in tools" :key="item" @click="goto(item)" type="text" class="tools">
      <ds-icon :name="getMenuIcon(item)" class="tool-icon"></ds-icon>
      <div class="tool-title">{{getMenuTitle(item)}}</div>
    </el-button>
  </div>
</template>

<script>
  import DsIcon from '@/components/icon.vue'
  import {getNavTitle, getNavIcon} from '../router'

  export default {
    name: 'NavTools',
    components: {DsIcon},
    props: {
      tools: {
        type: Array,
        default: () => {
          return []
        }
      }
    },
    data () {
      return {}
    },
    methods: {
      getMenuTitle (path) {
        return getNavTitle(path)
      },
      getMenuIcon (path) {
        return getNavIcon(path)
      },
      goto (path) {
        this.$router.push(path)
      }
    }
  }
</script>
