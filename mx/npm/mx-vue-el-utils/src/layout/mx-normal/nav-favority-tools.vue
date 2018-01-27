<template>
  <div>
    <el-button v-for="(item, index) in favorityTools" v-if="isRole(item) && index < 4" :key="item.path"
               @click="handleGoto(item.path)"
               type="text" class="tools">
      <mx-icon :name="item.icon" class="tool-icon"></mx-icon>
      <div v-if="showTitle" class="tool-title">{{item.name}}</div>
    </el-button>
    <el-button @click="handleShowNotice" type="text" class="tools">
      <el-badge v-if="showNotice" :value="noticeValue" class="badge-item">
        <mx-icon name="event_note" class="tool-icon"></mx-icon>
        <div v-if="showTitle" class="tool-title">{{t('button.notice')}}</div>
      </el-badge>
      <div v-else>
        <mx-icon name="event_note" class="tool-icon"></mx-icon>
        <div v-if="showTitle" class="tool-title">{{t('button.notice')}}</div>
      </div>
    </el-button>
  </div>
</template>

<script>
  import MxIcon from '@/components/mx-icon'
  import { t } from '@/locale'

  export default {
    name: 'mx-normal-favority-tools',
    components: {MxIcon},
    props: {
      role: String,
      favorityTools: Array,
      noticePath: String,
      noticeValue: Number,
      showTitle: {type: Boolean, default: true},
      loginUserName: {type: String, default: ''}
    },
    data () {
      return {
        t: t
      }
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
