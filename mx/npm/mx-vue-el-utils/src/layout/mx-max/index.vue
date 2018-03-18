<template>
  <el-container>
    <el-header class="layout-max__header">
      <mx-max-header :title="title" :login-user="loginUser" :nav-data="navData" v-on:changeLocale="handleChangeLocale"
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
  import MxMaxHeader from './header.vue'

  export default {
    name: 'mx-max-layout',
    components: {MxMaxHeader},
    data () {
      return {}
    }
  }
</script>
