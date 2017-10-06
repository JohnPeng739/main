<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";

  .header-col1 {
    display: flex;
    align-items: center;
    min-width: 300px;
    .toggle-icon {
      color: @header-color;
      font-size: 36px;
    }
    .title {
      font-size: x-large;
      font-weight: 600;
      min-width: 200px;
      padding-left: 5px;
    }
  }

  .header-col2 {
    min-width: 400px;
  }

  .header-col3 {
    min-width: 100px;
    font-size: 8px;
    text-align: center;
    margin: 0px auto;
    padding-top: 10px;
    .account {
      color: mix(@header-color, lightblue);
      font-size: 12px;
      font-weight: lighter;
    }
    .logout-icon {
      font-size: 24px;
      margin: -6px auto;
      color: @header-color;
      &:hover {
        color: @header-hover-color;
      }
    }
  }
</style>

<template>
  <el-row type="flex" justify="center">
    <el-col :span="8" class="header-col1">
      <el-button type="text" @click="navToggled">
        <ds-icon class="toggle-icon" name="apps"></ds-icon>
      </el-button>
      <div class="title">{{title}}</div>
    </el-col>
    <el-col class="header-col2">
      <slot></slot>
    </el-col>
    <el-col :span="3" class="header-col3">
      <span class="account">{{loginUserName}}</span><br>
      <el-button type="text" @click="logout">
        <ds-icon name="exit_to_app" class="logout-icon"></ds-icon>
      </el-button>
    </el-col>
  </el-row>
</template>

<script>
  import DsIcon from '../../../components/icon.vue'

  export default {
    name: 'header',
    components: {DsIcon},
    props: {
      title: {
        type: String,
        default: 'XXXX系统'
      },
      loginUserName: {
        type: String,
        default: 'NA'
      }
    },
    computed: {
      toggleIconColor() {
        return 'white'
      }
    },
    methods: {
      navToggled() {
        this.$emit('navToggled')
      },
      logout() {
        this.$emit('logout')
      }
    }
  }
</script>
