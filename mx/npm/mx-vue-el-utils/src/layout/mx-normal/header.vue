<template>
  <div>
    <el-button type="text" class="toggle-button" @click="handleNavToggled">
      <mx-icon name="apps" class="toggle-icon"></mx-icon>
    </el-button>
    <span class="app-title hidden-xs-only">{{title}}</span>
    <el-popover ref="popoverAccount" v-model="popoverVisible" placement="bottom" width="200" trigger="click">
      <el-row type="flex" justify="center" class="popover-account">
        <el-col :span="24">
          <el-dropdown @command="handleSwitchLocale" class="change-locale-menu">
            <div>
              <mx-icon name="language" class="icon"></mx-icon>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="en"><img src="../../assets/locale/en.png" class="locale-image">English</el-dropdown-item>
              <el-dropdown-item command="zh-CN"><img src="../../assets/locale/zh-CN.png" class="locale-image">简体中文</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <div class="title login-user-name">{{loginUserName}}</div>
        </el-col>
      </el-row>
      <el-row v-if="authenticated" type="flex" justify="center">
        <el-col :span="24">
          <slot name="account-info"></slot>
        </el-col>
      </el-row>
      <el-row v-if="authenticated" type="flex" justify="center" class="popover-account">
        <el-col :span="24">
          <div class="popover-menu" @click="handleClickPersonalMenu('changePassword')">
            <mx-icon name="lock" class="icon"></mx-icon>
            <span class="item">{{$t('common.changePassword')}}</span>
          </div>
        </el-col>
      </el-row>
      <el-row v-if="authenticated" type="flex" justify="center" class="popover-account">
        <el-col :span="24">
          <div class="popover-menu" @click="handleClickPersonalMenu('mySetting')">
            <mx-icon name="settings" class="icon"></mx-icon>
            <span class="item">{{$t('common.mySetting')}}</span>
          </div>
        </el-col>
      </el-row>
      <el-row v-if="authenticated" type="flex" justify="center" class="popover-account">
        <el-col :span="24">
          <div class="popover-menu" @click="handleClickPersonalMenu('logout')">
            <mx-icon name="exit_to_app" class="icon"></mx-icon>
            <span class="item">{{$t('common.logout')}}</span>
          </div>
        </el-col>
      </el-row>
    </el-popover>
    <el-button class="account" type="text" v-popover:popoverAccount>
      <mx-icon name="account_circle" class="account-icon"></mx-icon>
    </el-button>
    <slot name="favorite-tools"></slot>
  </div>
</template>

<script>
  import Vue from 'vue'
  import MxIcon from '../../components/mx-icon'
  import MxLocale from '../../utils/mx-locale'
  import MxVueElUtils from '../../index'

  export default {
    name: 'mx-normal-header',
    components: {MxIcon},
    props: ['title', 'loginUser'],
    data () {
      return {
        popoverVisible: false
      }
    },
    computed: {
      authenticated () {
        let user = this.loginUser
        return user && user.code && user.name
      },
      loginUserName () {
        let user = this.loginUser
        if (user && user.name) {
          return this.$t('common.welcome') + ' ' + user.name
        } else {
          return this.$t('common.notLogin')
        }
      }
    },
    methods: {
      handleNavToggled () {
        this.$emit('navToggled')
      },
      handleSwitchLocale (lang) {
        if (lang) {
          MxLocale.setLanguage(lang)
          Vue.use(MxVueElUtils, {locale: lang})
        }
      },
      handleClickPersonalMenu (menu) {
        this.popoverVisible = false
        this.$emit('clickPersonalMenu', menu)
      }
    }
  }
</script>
