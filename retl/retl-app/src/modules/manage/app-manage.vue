<template>
  <div id="manage">
    <router-view></router-view>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'

  export default {
    name: 'manage',
    methods: {
      ...mapActions(['loadTypes'])
    },
    mounted() {
      this.loadTypes()
    }
  }
</script>
