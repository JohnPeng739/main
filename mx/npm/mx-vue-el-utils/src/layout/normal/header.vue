<template>
  <el-row type="flex" justify="center">
    <el-col :span="8" class="header-col1">
      <el-button type="text" @click="handleNavToggled">
        <icon name="apps" class="toggle-icon"></icon>
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
        <icon name="exit_to_app" class="logout-icon"></icon>
      </el-button>
    </el-col>
  </el-row>
</template>

<script>
  import Icon from '@/components/icon.vue'

  export default {
    name: 'layout-normal-header',
    components: {Icon},
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
