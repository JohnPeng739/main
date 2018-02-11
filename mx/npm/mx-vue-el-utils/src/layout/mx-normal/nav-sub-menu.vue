<template>
  <el-submenu v-if="isRole(item)" :index="item.path">
    <template slot="title">
      <mx-icon :name="item.icon" class="menu-item"></mx-icon>
      <span slot="title" class="menu-item">{{$t(item.name)}}</span>
    </template>
    <el-menu-item v-for="(subItem) in item.children" :key="subItem.path" v-if="isRole(subItem)"
                  :index="subItem.path" class="menu-item">
      <mx-icon :name="subItem.icon" class="submenu-item"></mx-icon>
      <span slot="title" class="submenu-item">{{$t(subItem.name)}}</span>
    </el-menu-item>
  </el-submenu>
</template>

<script>
  import MxIcon from '../../components/mx-icon'

  export default {
    name: 'mx-normal-nav-sub-menu',
    components: {MxIcon},
    props: ['item', 'roles'],
    methods: {
      isRole (item) {
        let roles = this.roles
        return (roles && roles instanceof Array && roles.indexOf(item.role) >= 0) || !item.role
      }
    }
  }
</script>
