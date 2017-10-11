<style rel="stylesheet/less" lang="less" scoped>
  @import "../../../style/base.less";
  .menu-item {
    font-size: 16px;
    >div {
      font-size: 16px;
    }
  }
</style>

<template>
  <el-menu v-if="spanLeft == 4" :default-active="getActived" unique-opened @select="handleSelect"
           @open="handleOpen" @close="handleClose" theme="dark" router style="min-width: 200px;">
    <el-menu-item v-if="isRole(summaryNavData.role)" :index="summaryNavData.path" class="menu-item">
      <ds-icon :name="summaryNavData.icon" class="nav-icon"></ds-icon>{{summaryNavData.title}}
    </el-menu-item>
    <nav-sub-menu :subMenuData="taskNavData"></nav-sub-menu>
    <nav-sub-menu :subMenuData="manageNavData"></nav-sub-menu>
  </el-menu>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {logger} from 'dsutils'
  import DsIcon from '@/components/icon.vue'
  import NavSubMenu from './nav-sub-menu.vue'
  import {navData} from '../router'

  export default {
    name: 'NavMenu',
    components: {DsIcon, NavSubMenu},
    props: {
      spanLeft: {
        type: Number,
        default: 4
      }
    },
    data () {
      return {
        summaryNavData: navData.summaryNavData,
        manageNavData: navData.manageNavData,
        taskNavData: navData.taskNavData
      }
    },
    computed: {
      ...mapGetters(['roles']),
      getNavData () {
        return navData
      },
      getActived () {
        return this.$route.path.replace('/', '')
      }
    },
    methods: {
      ...mapActions(['setPathName']),
      isRole (role) {
        return this.roles.indexOf(role) !== -1 || role === 'all'
      },
      handleSelect(index, indexPath) {
        logger.debug('index: %s, path: %s.', index, indexPath)
        this.setPathName({path: index})
      },
      handleOpen (item) {
        logger.debug('open toggled')
        logger.debug(item)
      },
      handleClose (item) {
        logger.debug('close toggled')
        logger.debug(item)
      }
    }
  }
</script>
