<template>
  <div>
    <el-button type="text" class="toggle-button" @click="handleNavToggled">
      <mx-icon name="apps" class="toggle-icon"></mx-icon>
    </el-button>
    <span class="app-title hidden-xs-only">{{title}}</span>
    <el-tooltip effect="dark" placement="bottom">
      <div slot="content">
        <span>{{loginUserName}}</span>
      </div>
      <el-button class="account" type="text" @click="handleLogout">
        <mx-icon name="account_circle" class="account-icon" @click="handleLogout"></mx-icon>
      </el-button>
    </el-tooltip>
    <slot name="favority-tools"></slot>
  </div>
</template>

<script>
  import MxIcon from '../../components/mx-icon'

  // TODO 添加对登录账户本身操作功能，比如：账户详情、修改密码、修改个性化信息等

  export default {
    name: 'mx-normal-header',
    components: {MxIcon},
    props: {
      title: String,
      loginUserName: {type: String, default: ''}
    },
    data () {
      return {}
    },
    methods: {
      handleNavToggled () {
        this.$emit('navToggled')
      },
      handleLogout () {
        this.$emit('logout')
      }
    }
  }
</script>
