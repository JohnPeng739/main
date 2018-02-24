<template>
  <rbac-layout :title="title" :navData="transformedNavData" v-on:showNotice="handleShowNotice"></rbac-layout>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import {RbacLayout} from '../index'
  import { navData } from './router'

  export default {
    name: 'layout',
    components: {RbacLayout},
    data () {
      return {
        title: 'The Demo System'
      }
    },
    computed: {
      transformedNavData: {
        get () {
          return navData
        },
        set (val) {}
      }
    },
    methods: {
      handleShowNotice () {
        logger.debug('show notice page.')
      }
    }
  }
</script>
