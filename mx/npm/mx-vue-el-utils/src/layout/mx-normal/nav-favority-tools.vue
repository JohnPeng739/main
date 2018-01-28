<template>
  <div>
    <el-tooltip v-for="(item, index) in favorityTools" v-if="isRole(item) && index < 6" :key="item.path"
                effect="dark" placement="bottom">
      <div slot="content">
        <span>{{item.name}}</span>
      </div>
      <el-button @click="handleGoto(item.path)" type="text" class="tools">
        <mx-icon :name="item.icon" class="tool-icon"></mx-icon>
      </el-button>
    </el-tooltip>
    <el-tooltip effect="dark" placement="bottom">
      <div slot="content">
        <span>{{t('button.notice')}}</span>
      </div>
      <el-button @click="handleShowNotice" type="text" class="tools">
        <el-badge v-if="showNotice" :value="noticeValue" class="badge-item">
          <mx-icon name="event_note" class="tool-icon"></mx-icon>
        </el-badge>
        <div v-else>
          <mx-icon name="event_note" class="tool-icon"></mx-icon>
        </div>
      </el-button>
    </el-tooltip>
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
        this.$emit('goto', path)
      },
      handleShowNotice () {
        this.$emit('showNotice')
      }
    }
  }
</script>
