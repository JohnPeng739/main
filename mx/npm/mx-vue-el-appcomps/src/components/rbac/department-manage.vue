<template>
  <page-common-dict :max-height="tableMaxHeight" module="department"></page-common-dict>
</template>

<script>
  import PageCommonDict from '@/components/common-dict-manage.vue'

  export default {
    name: 'page-department-manage',
    components: {PageCommonDict},
    props: {
      tableMaxHeight: {
        type: Number,
        default: 550
      }
    }
  }
</script>
