<template>
  <el-menu :default-active="defaultActive" :collapse="toggled" unique-opened router class="nav-menu">
    <template v-for="item in navData">
      <nav-sub-menu v-if="item.children && isRole(item)" :role="role" :item="item"></nav-sub-menu>
      <el-menu-item v-else-if="isRole(item)" :index="item.path" class="menu-item">
        <mx-icon :name="item.icon" class="menu-item"></mx-icon>
        <span slot="title" class="menu-item">{{item.name}}</span>
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
    props: {
      toggled: Boolean,
      navData: Array,
      role: String
    },
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
