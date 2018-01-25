<template>
  <i class="material-icons">
    {{this.name}}
  </i>
</template>

<script>
  export default {
    name: 'mx-icon',
    props: {
      name: {
        type: String,
        default: 'apps'
      }
    }
  }
</script>
