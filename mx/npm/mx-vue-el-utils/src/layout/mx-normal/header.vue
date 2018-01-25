<template>
  <el-row type="flex" justify="center">
    <el-col :span="8" class="header-col1">
      <el-button type="text" @click="handleNavToggled">
        <mx-icon name="apps" class="toggle-icon"></mx-icon>
      </el-button>
      <div class="title">{{title}}</div>
    </el-col>
    <el-col class="header-col2">
      <slot></slot>
    </el-col>
    <el-col :span="3" class="header-col3">
      <el-button class="button" type="text" @click="handleShowUserInfo">
        <span class="account">{{loginUserName}}</span>
      </el-button>
      <el-button class="button" type="text" @click="handleLogout">
        <mx-icon name="exit_to_app" class="logout-icon"></mx-icon>
      </el-button>
    </el-col>
  </el-row>
</template>

<script>
  import MxIcon from '@/components/mx-icon'

  export default {
    name: 'mx-normal-header',
    components: {MxIcon},
    props: {
      title: String,
      loginUserName: String
    },
    data () {
      return {}
    },
    methods: {
      handleNavToggled () {
        this.$emit('navToggled')
      },
      handleShowUserInfo () {
        this.$emit('showUserInfo')
      },
      handleLogout () {
        this.$emit('logout')
      }
    }
  }
</script>
