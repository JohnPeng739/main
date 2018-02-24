<template>
  <el-menu :default-active="defaultActive" :collapse="toggled" unique-opened @select="handleClickMenu" class="nav-menu">
    <template v-for="item in navData">
      <nav-sub-menu v-if="item.children && isRole(item)" :roles="roles" :item="item"></nav-sub-menu>
      <el-menu-item v-else-if="isRole(item)" :index="item.path" class="menu-item">
        <mx-icon :name="item.icon" class="menu-item"></mx-icon>
        <span slot="title" class="menu-item">{{$t(item.name)}}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script>
  import MxIcon from '../../components/mx-icon'
  import NavSubMenu from './nav-sub-menu.vue'

  export default {
    name: 'mx-normal-nav-menu',
    components: {MxIcon, NavSubMenu},
    props: ['toggled', 'roles', 'navData'],
    computed: {
      defaultActive () {
        return this.$route.path
      }
    },
    methods: {
      isRole (item) {
        let roles = this.roles
        return (roles && roles instanceof Array && roles.indexOf(item.role) >= 0) || !item.role
      },
      handleClickMenu (index) {
        this.$emit('goto', index)
      }
    }
  }
</script>
