<template>
  <div>
    <el-tooltip v-for="(item, index) in tools" v-if="isRole(item) && index < 6" :key="item.path"
                effect="dark" placement="bottom">
      <div slot="content">
        <span>{{$t(item.name)}}</span>
      </div>
      <el-button @click="handleGoto(item.path)" type="text" class="tools">
        <mx-icon :name="item.icon" class="tool-icon"></mx-icon>
      </el-button>
    </el-tooltip>
    <el-tooltip effect="dark" placement="bottom">
      <div slot="content">
        <span>{{$t('button.notice')}}</span>
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
  import MxIcon from '../../components/mx-icon'

  export default {
    name: 'mx-normal-favority-tools',
    components: {MxIcon},
    props: ['roles', 'tools', 'noticeValue'],
    data () {
      return {}
    },
    computed: {
      showNotice () {
        let {noticeValue} = this
        return noticeValue && noticeValue > 0
      }
    },
    methods: {
      isRole (item) {
        let roles = this.roles
        return (roles && roles instanceof Array && roles.indexOf(item.role) >= 0) || !item.role
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
