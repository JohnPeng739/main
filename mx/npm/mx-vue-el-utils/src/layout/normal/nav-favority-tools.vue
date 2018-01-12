<template>
  <div class="toolbar">
    <el-button v-for="item in favorityTools" v-if="isRole(item)" :key="item.path" @click="handleGoto(item.path)"
               type="text" class="tools">
      <icon :name="item.icon" class="tool-icon"></icon>
      <div v-if="showTitle" class="tool-title">{{item.name}}</div>
    </el-button>
    <el-button @click="handleShowNotice" type="text" class="tools">
      <el-badge v-if="showNotice" :value="noticeValue" class="badge-item">
        <icon name="event_note" class="tool-icon"></icon>
      </el-badge>
      <icon v-else name="event_note" class="tool-icon"></icon>
      <div v-if="showTitle" class="tool-title">{{$t('button.notice')}}</div>
    </el-button>
  </div>
</template>

<script>
  import Icon from '@/components/icon.vue'

  export default {
    name: 'layout-normal-favority-tools',
    components: {Icon},
    props: {
      role: String,
      favorityTools: Array,
      noticePath: String,
      noticeValue: Number,
      showTitle: {type: Boolean, default: true}
    },
    data () {
      return {}
    },
    computed: {
      showNotice () {
        let {noticePath, noticeValue} = this
        return noticePath && typeof noticePath === 'string' && noticePath.length > 0 && noticeValue && noticeValue > 0
      }
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
      },
      handleShowNotice () {
        this.$emit('showNotice')
      }
    }
  }
</script>
