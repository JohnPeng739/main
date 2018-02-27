<template>
  <div>
    <el-button type="text" class="toggle-button" @click="handleNavToggled">
      <mx-icon name="apps" class="toggle-icon"></mx-icon>
    </el-button>
    <span class="app-title hidden-xs-only">{{title}}</span>
    <el-popover ref="popoverAccount" v-model="popoverVisible" placement="bottom" width="200" trigger="click">
      <el-row type="flex" justify="center" class="popover-account">
        <el-col :span="24">
          <div class="title">{{loginUserName}}</div>
        </el-col>
      </el-row>
      <el-row v-if="authenticated" type="flex" justify="center">
        <el-col :span="24">
          <slot name="account-info"></slot>
        </el-col>
      </el-row>
      <el-row type="flex" justify="center">
        <el-col :span="24">
          <el-select v-model="locale" @change="handleSwitchLocale" size="mini" class="change-locale-menu">
            <el-option v-for="item in locales" :key="item.id" :value="item.id" :label="$t('locale.' + item.id)">
              <img :src="item.img" class="locale-image">
              <span class="locale-text">
                <mx-icon class="locale-icon" v-if="locale === item.id" name="checked"></mx-icon>
                {{$t('locale.' + item.id)}}
              </span>
            </el-option>
          </el-select>
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
  import MxIcon from '../../components/mx-icon'
  import imgEn from '../../assets/locale/en.png'
  import imgZhCN from '../../assets/locale/zh-CN.png'

  export default {
    name: 'mx-normal-header',
    components: {MxIcon},
    props: ['title', 'loginUser'],
    data () {
      return {
        locales: [{
          id: 'en',
          img: imgEn
        }, {
          id: 'zh-CN',
          img: imgZhCN
        }],
        locale: 'en',
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
          this.$emit('changeLocale', lang)
        }
      },
      handleClickPersonalMenu (menu) {
        this.popoverVisible = false
        this.$emit('clickPersonalMenu', menu)
      }
    },
    mounted () {
      if (window && window.localStorage) {
        let locale = window.localStorage.getItem('locale')
        if (locale && typeof locale === 'string' && locale.length > 0) {
          this.locale = locale
        }
      }
    }
  }
</script>
