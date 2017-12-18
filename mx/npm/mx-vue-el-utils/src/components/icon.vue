<template>
  <i class="material-icons">
    {{this.name}}
  </i>
</template>

<script>
  import 'element-ui/'

  export default {
    name: 'DsIcon',
    data () {
      return {
        active: false
      }
    },
    props: {
      name: {
        type: String,
        default: 'apps'
      }
    }
  }
</script>
