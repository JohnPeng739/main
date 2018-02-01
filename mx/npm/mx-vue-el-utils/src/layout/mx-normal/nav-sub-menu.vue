<template>
  <el-submenu v-if="isRole(item)" :index="item.path">
    <template slot="title">
      <mx-icon :name="item.icon" class="menu-item"></mx-icon>
      <span slot="title" class="menu-item">{{item.name}}</span>
    </template>
    <el-menu-item v-for="(subItem) in item.children" :key="subItem.path" v-if="isRole(subItem)"
                  :index="subItem.path" class="menu-item">
      <mx-icon :name="subItem.icon" class="submenu-item"></mx-icon>
      <span slot="title" class="submenu-item">{{subItem.name}}</span>
    </el-menu-item>
  </el-submenu>
</template>

<script>
  import MxIcon from '../../components/mx-icon'

  export default {
    name: 'mx-normal-nav-sub-menu',
    components: {MxIcon},
    props: {
      item: {},
      role: String
    },
    methods: {
      isRole (item) {
        let role = this.role
        return (role && role === item.role) || !item.role
      }
    }
  }
</script>
