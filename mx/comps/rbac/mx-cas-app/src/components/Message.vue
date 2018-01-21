<template>
  <v-snackbar :color="color" :timeout="timeout" :top="top" :bottom="bottom" :right="right" :left="left" v-model="visible">{{text}}</v-snackbar>
</template>

<script>
  export default {
    props: {
      position: {
        type: String,
        default: 'bottom'
      },
      timeout: {
        type: Number,
        default: 3000
      }
    },
    data() {
      return {
        visible: false,
        text: '',
        color: 'info'
      }
    },
    computed: {
      top: () => this.position === 'top',
      bottom: () => this.position === 'bottom',
      right: () => this.position === 'right',
      left: () => this.position === 'left'
    },
    methods: {
      show(text) {
        this.text = text
        this.visible = true
      },
      info(text) {
        this.color = 'info'
        this.show(text)
      },
      warning(text) {
        this.color = 'warning'
        this.show(text)
      },
      error(text) {
        this.color = 'error'
        this.show(text)
      },
      success(text) {
        this.color = 'success'
        this.show(text)
      }
    }
  }
</script>
