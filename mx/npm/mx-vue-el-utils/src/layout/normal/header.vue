<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .header-col1 {
    display: flex;
    align-items: center;
    min-width: 300px;
    .toggle-icon {
      margin-left: 10px;
      color: @header-color;
      font-size: 36px;
    }
    .title {
      font-size: x-large;
      font-weight: 600;
      min-width: 500px;
      padding-left: 15px;
    }
  }

  .header-col2 {
    min-width: 400px;
  }

  .header-col3 {
    min-width: 100px;
    font-size: 8px;
    margin: 0 auto;
    .button {
      display: block;
      .account {
        color: mix(@header-color, indianred);
        font-size: 12px;
        font-weight: lighter;
      }
      .logout-icon {
        font-size: 24px;
        margin: -15px auto;
        padding-left: 10px;
        color: @header-color;
        &:hover {
          color: @header-hover-color;
        }
      }
    }
  }
</style>

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
    props: ['title', 'loginUserName'],
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
