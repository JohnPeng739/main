<template>
  <mx-dict-manage :max-height="tableMaxHeight" module="department"></mx-dict-manage>
</template>

<script>
  import MxDictManage from '@/components/mx-dict-manage'

  export default {
    name: 'mx-department-manage',
    components: {MxDictManage},
    props: {
      tableMaxHeight: {
        type: Number,
        default: 540
      }
    }
  }
</script>
