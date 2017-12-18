<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .nav-icon {
     font-size: 20px;
     padding-right: 10px;
   }

  .sub-nav-icon {
    font-size: 18px;
    padding-right: 10px;
  }

  .menu-item {
    font-size: 16px;
  }

  .submenu-item {
    font-size: 14px;
  }
</style>

<template>
  <el-submenu v-if="isRole(item)" :index="item.path">
    <template slot="title">
      <icon :name="item.icon" class="nav-icon"></icon>
      <span slot="title" class="menu-item">{{item.name}}</span>
    </template>
    <el-menu-item v-for="(subItem) in item.children" :key="subItem.path" v-if="isRole(subItem)"
                  :index="subItem.path" class="menu-item">
      <icon :name="subItem.icon" class="sub-nav-icon"></icon>
      <span slot="title" class="submenu-item">{{subItem.name}}</span>
    </el-menu-item>
  </el-submenu>
</template>

<script>
  import Icon from '@/components/icon.vue'

  export default {
    name: 'layout-normal-nav-sub-menu',
    components: {Icon},
    props: ['item', 'role'],
    methods: {
      isRole (item) {
        let role = this.role
        return (role && role === item.role) || !item.role
      }
    }
  }
</script>
