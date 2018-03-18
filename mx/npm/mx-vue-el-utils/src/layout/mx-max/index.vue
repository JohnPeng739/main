<template>
  <el-container>
    <el-header :height="headerHeight" class="layout-max__header">
      <mx-max-header v-model="headerHidden" :title="title" :login-user="loginUser" :nav-data="navData"
                     v-on:changeLocale="handleChangeLocale"
                     v-on:clickpersonalMenu="handleClickPersonalMenu"></mx-max-header>
    </el-header>
    <el-main class="layout-max__main">
      <div class="content-body">
        <transition name="el-zoom-in-top">
          <slot name="content-body"></slot>
        </transition>
      </div>
    </el-main>
  </el-container>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import MxMaxHeader from './header.vue'

  export default {
    name: 'mx-max-layout',
    components: {MxMaxHeader},
    props: {
      title: {
        type: String,
        default: 'Application title'
      },
      loginUser: {
        type: Object,
        default: undefined
      },
      navData: {
        type: Array,
        default: []
      },
      noticeValue: {
        type: Number,
        deault: 0
      }
    },
    data () {
      return {
        headerHidden: false
      }
    },
    computed: {
      headerHeight () {
        return (this.headerHidden ? 10 : 56) + 'px'
      }
    },
    methods: {
      handleChangeLocale (lang) {
        logger.debug('change locale: %s.', lang)
        this.$emit('changeLocale', lang)
      },
      handleClickPersonalMenu (menu) {
        this.$emit('clickPersonalMenu', menu)
      }
    }
  }
</script>
