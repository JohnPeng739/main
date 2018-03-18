<template>
  <div>
    <div v-if="headerMax" class="layout-max__header1">
      <el-row type="flex" align="center">
        <el-col :span="8">
          <div v-for="item in navData" :key="item.path" class="nav-menu">
            <div v-if="item.children && item.children.length > 0" class="menu-item">
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
            <div v-else class="menu-item" @click="handleClickMenu(item.path)">
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
          </div>
        </el-col>
      </el-row>
    </div>
    <div v-else>minimal header</div>
  </div>
</template>

<script>
  import MxAccount from '../common/account.vue'

  export default {
    name: 'mx-max-header',
    props: {
      value: false,
      title: {
        type: String,
        default: 'Application title'
      },
      loginUser: {
        type: Object,
        default: undefined
      },
      navData: {
        type: Array,
        default: []
      },
      noticeValue: {
        type: Number,
        deault: 0
      }
    },
    components: {MxAccount},
    data () {
      return {
      }
    },
    computed: {
      headerMax () {
        return !this.value
      }
    },
    methods: {
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
      }
    }
  }
</script>
