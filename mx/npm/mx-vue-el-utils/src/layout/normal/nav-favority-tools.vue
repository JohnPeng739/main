<template>
  <div class="toolbar">
    <el-button v-for="item in favorityTools" v-if="isRole(item)" :key="item.path" @click="handleGoto(item.path)"
               type="text" class="tools">
      <icon :name="item.icon" class="tool-icon"></icon>
      <div class="tool-title">{{item.name}}</div>
    </el-button>
  </div>
</template>

<script>
  import Icon from '@/components/icon.vue'

  export default {
    name: 'layout-normal-favority-tools',
    components: {Icon},
    props: ['role', 'favorityTools'],
    data () {
      return {}
    },
    methods: {
      isRole (item) {
        let role = this.role
        return (role && role === item.role) || !item.role
      },
      handleGoto (path) {
        // if (path && typeof path === 'string') {
        //  this.$router.push(path)
        // }
        this.$emit('goto', path)
      }
    }
  }
</script>
