<template>
  <rbac-layout :title="title" :navData="navData" v-on:changeLocale="handleChangeLocale"
               v-on:showNotice="handleShowNotice"></rbac-layout>
</template>

<script>
  import {logger} from 'mx-app-utils'
  import {navData} from './router'
  import {changeLanguage} from './locale/index'

  export default {
    data() {
      return {
        navData: navData,
        title: this.$t('cas.title')
      }
    },
    methods: {
      handleChangeLocale (lang) {
        changeLanguage(lang)
      },
      handleShowNotice() {
        logger.debug('show notify.')
      }
    }
  }
</script>
