<template>
  <div>
    <el-button type="text" class="toggle-button" @click="handleNavToggled">
      <mx-icon name="apps" class="toggle-icon"></mx-icon>
    </el-button>
    <span class="app-title hidden-xs-only">{{title}}</span>
    <el-popover ref="popoverAccount" placement="bottom" width="200" trigger="click">
      <el-row type="flex" justify="center" class="popover-account">
        <el-col :span="24"><div class="title">{{loginUserName}}</div></el-col>
      </el-row>
      <el-row v-if="authenticated" type="flex" justify="center">
        <el-col :span="24"><slot name="account-info"></slot></el-col>
      </el-row>
      <el-row v-if="authenticated" type="flex" justify="center" class="popover-account">
        <el-col :span="24">
          <div class="popover-menu" @click="handleClickMenu('changePassword')">
            <mx-icon name="lock" class="icon"></mx-icon><span class="item">{{$t('button.changePassword')}}</span>
          </div>
        </el-col>
      </el-row>
      <el-row v-if="authenticated" type="flex" justify="center" class="popover-account">
        <el-col :span="24">
          <div class="popover-menu" @click="handleClickMenu('mySetting')">
            <mx-icon name="favorite" class="icon"></mx-icon><span class="item">{{$t('button.mySetting')}}</span>
          </div>
        </el-col>
      </el-row>
      <el-row v-if="authenticated" type="flex" justify="center" class="popover-account">
        <el-col :span="24">
          <div class="popover-menu" @click="handleClickMenu('logout')">
            <mx-icon name="exit_to_app" class="icon"></mx-icon><span class="item">{{$t('button.logout')}}</span>
        </div></el-col>
      </el-row>
    </el-popover>
    <el-button class="account" type="text" v-popover:popoverAccount>
      <mx-icon name="account_circle" class="account-icon"></mx-icon>
    </el-button>
    <slot name="favority-tools"></slot>
  </div>
</template>

<script>
  import MxIcon from '../../components/mx-icon'

  // TODO 添加对登录账户本身操作功能，比如：账户详情、修改密码、修改个性化信息等

  export default {
    name: 'mx-normal-header',
    components: {MxIcon},
    props: ['title', 'loginUser'],
    data () {
      return {}
    },
    computed: {
      authenticated () {
        let user = this.loginUser
        return user && user.code && user.name
      },
      loginUserName () {
        let user = this.loginUser
        if (user && user.name) {
          return this.$t('welcome') + user.name
        } else {
          return this.$t('notLogin')
        }
      }
    },
    methods: {
      handleNavToggled () {
        this.$emit('navToggled')
      },
      handleClickMenu (menu) {
        this.$emit('clickMenu', menu)
      }
    }
  }
</script>
