<template>
  <rbac-layout :title="title" :navData="navData" v-on:showNotice="handleShowNotice"></rbac-layout>
</template>

<script>
  import {logger} from 'mx-app-utils'
  import {navData} from './router'

  export default {
    data() {
      return {
        navData: navData,
        title: this.$t('title')
      }
    },
    methods: {
      handleShowNotice() {
        logger.debug('show notify.')
      }
    }
  }
</script>
