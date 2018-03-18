<template>
  <div>
    <h1>测试首页</h1>
    <img src="../assets/logo.png">
    <br/><br/>
    some material icon:
    <mx-icon></mx-icon>
    <mx-icon name="home"></mx-icon>
    <mx-icon name="build"></mx-icon>
    <mx-icon name="3d_rotation"></mx-icon>
    <br/><br/>
    <span>选择语言：</span>
    <el-select v-model="lang" @change="handleChangeLanguage">
      <el-option value="en" label="英语"></el-option>
      <el-option value="zh-CN" label="简体中文"></el-option>
    </el-select>
  </div>
</template>

<script>
  import {MxLocale} from '../../utils/mx-locale'

  export default {
    name: 'home-page',
    data () {
      return {
        lang: this.$i18n.locale
      }
    },
    methods: {
      handleGoto (path) {
        this.$router.push(path)
      },
      handleChangeLanguage (lang) {
        MxLocale.changeLanguage(lang)
      }
    }
  }
</script>
