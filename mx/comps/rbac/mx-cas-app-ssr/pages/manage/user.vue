<template>
  <mx-user-manage :stations="stations"></mx-user-manage>
</template>

<script>
  export default {
    middleware: 'authority',
    data () {
      return {
        stations: ['a', 'b', 'c']
      }
    }
  }
</script>
