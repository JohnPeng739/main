<template>
  <div>
    <div v-if="headerMax" class="layout-max__header1">
      <el-row type="flex" align="center">
        <el-col :span="8">
          <div v-for="item in navData" :key="item.path" class="nav-menu">
            <div v-if="item.children && item.children.length > 0 && isRole(item)" class="menu-item">
              <el-dropdown @command="handleClickMenu">
                <div>
                  <mx-icon :name="item.icon" class="nav-icon"></mx-icon>
                  <div class="nav-text">{{item.name}}</div>
                </div>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item v-for="sub in item.children" :key="sub.path" :command="sub.path">
                    {{sub.name}}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
            <div v-else-if="isRole(item)" class="menu-item" @click="handleClickMenu(item.path)">
              <mx-icon :name="item.icon" class="nav-icon"></mx-icon>
              <div class="nav-text">{{item.name}}</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="title" @dblclick="handleToggleHidden()">{{title}}</div>
        </el-col>
        <el-col :span="8">
          <div>
            <mx-account :loginUser="loginUser" v-on:changeLocale="handleChangeLocale"
                        v-on:clickPersonalMenu="handleClickPersonalMenu"></mx-account>
            <mx-favorite-tools slot="favorite-tools" class="favorite-tools hidden-xs-only hidden-sm-only"
                               :roles="roles"
                               :tools="favoriteTools"
                               :notice-value="noticeValue" v-on:goto="handleClickMenu"
                               v-on:showNotice="handleShowNotice">
            </mx-favorite-tools>
          </div>
        </el-col>
      </el-row>
    </div>
    <div v-else>minimal header</div>
  </div>
</template>

<script>
  import MxAccount from '../common/account.vue'
  import MxFavoriteTools from '../common/favorite-tools.vue'

  export default {
    name: 'mx-max-header',
    props: ['value', 'title', 'roles', 'navData', 'loginUser', 'favoriteTools', 'noticeValue'],
    components: {MxAccount, MxFavoriteTools},
    data () {
      return {}
    },
    computed: {
      headerMax () {
        return !this.value
      }
    },
    methods: {
      isRole (item) {
        let roles = this.roles
        return (roles && roles instanceof Array && roles.indexOf(item.role) >= 0) || !item.role
      },
      handleClickMenu (path) {
        this.$router.push(path)
      },
      handleToggleHidden () {
        this.$emit('input', !this.value)
      },
      handleChangeLocale (lang) {
        this.$emit('changeLocale', lang)
      },
      handleClickPersonalMenu (menu) {
        this.$emit('clickPersonalMenu', menu)
      },
      handleShowNotice () {
        this.$emit('showNotice')
      }
    }
  }
</script>
