<template>
  <el-menu :default-active="defaultActive" :collapse="toggled" unique-opened router class="nav-menu">
    <template v-for="item in navData">
      <nav-sub-menu v-if="item.children && isRole(item)" :role="role" :item="item"></nav-sub-menu>
      <el-menu-item v-else-if="isRole(item)" :index="item.path" class="menu-item">
        <icon :name="item.icon" class="nav-icon"></icon>
        <span slot="title" class="menu-item">{{item.name}}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script>
  import Icon from '@/components/icon.vue'
  import NavSubMenu from './nav-sub-menu.vue'

  export default {
    name: 'layout-normal-nav-menu',
    components: {Icon, NavSubMenu},
    props: ['toggled', 'navData', 'role'],
    computed: {
      defaultActive () {
        return this.$route.path
      }
    },
    methods: {
      isRole (item) {
        let role = this.role
        return (role && role === item.role) || !item.role
      }
    }
  }
</script>
