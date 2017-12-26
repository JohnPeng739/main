<template>
  <page-common-dict :max-height="tableMaxHeight" module="role"></page-common-dict>
</template>

<script>
  import PageCommonDict from '@/components/common-dict-manage.vue'

  export default {
    name: 'page-role-manage',
    components: {PageCommonDict},
    props: {
      tableMaxHeight: {
        type: Number,
        default: 540
      }
    }
  }
</script>
