<style rel="stylesheet/less" lang="less" scoped>
  @import "../../../style/base.less";
</style>

<template>
  <el-submenu v-if="isRole(subMenuData.role)" :index="subMenuData.path" class="menu-item">
    <template slot="title">
      <ds-icon :name="subMenuData.icon" class="nav-icon"></ds-icon>
      {{subMenuData.title}}
    </template>
    <el-menu-item v-for="(item, index) in subMenuData.children" :key="item.path" v-if="isRole(item.role)"
                  :index="item.path" class="menu-item">
      <ds-icon :name="item.icon" class="nav-icon"></ds-icon>{{item.title}}
    </el-menu-item>
  </el-submenu>
</template>

<script>
  import {mapGetters} from 'vuex'
  import DsIcon from '@/components/icon.vue'

  export default {
    name: 'nav-sub-menu',
    components: {DsIcon},
    props: {
      subMenuData: {
        required: true
      }
    },
    computed: {
      ...mapGetters(['roles'])
    },
    methods: {
      isRole (role) {
        return this.roles.indexOf(role) !== -1 || role === 'all'
      }
    }
  }
</script>
