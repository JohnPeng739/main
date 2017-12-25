<template>
  <i class="material-icons">
    {{this.name}}
  </i>
</template>

<script>
  export default {
    name: 'material-icon',
    props: {
      name: {
        type: String,
        default: 'apps'
      }
    }
  }
</script>
