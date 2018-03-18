<template>
  <div>
    <div v-if="headerMax">titled header</div>
    <div v-else>minimal header</div>
  </div>
</template>

<script>
  export default {
    name: 'mx-max-header',
    data () {
      return {
        headerMax: true
      }
    }
  }
</script>
