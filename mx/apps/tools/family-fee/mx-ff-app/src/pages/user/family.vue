<style rel="stylesheet/less" lang="less" scoped>
  @import "../../assets/style/base.less";

  .image {
    width: 128px;
    height: 86px;
  }

  .text {
    text-align: center;
    margin: 3px;
  }

  .layout-pane {
    font-size: @content-text-font-size;
    .pane {
      width: 80%;
      margin: 20px;
      .title {
        font-size: @content-text-font-size * 1.3;
        font-weight: 600;
        margin: 20px 0;
      }
      .nofamily {
        color: red;
      }
      .owner {
        width: 140px;
        height: 140px;
        margin: 0 20px;
      }
      .member {
        display: inline-block;
        width: 140px;
        height: 140px;
        margin: 0 20px;
        .text1 {
          font-size: @content-text-font-size * 1.1;
          font-weight: 500;
          text-align: center;
        }
      }
    }
  }
</style>

<template>
  <div class="layout-pane">
    <div v-if="family" class="pane">
      <div class="title">{{family.name}}</div>
      <div class="title">{{$t('pages.family.title.owner')}}</div>
      <el-card class="owner" :body-style="{padding: '5px'}">
        <img src="../../assets/logo.png" class="image">
        <div class="text">{{family.owner.name}}</div>
      </el-card>
      <div class="title">{{$t('pages.family.title.members')}}</div>
      <el-card v-for="item in family.members" :key="item.id" class="member" :body-style="{padding: '5px'}">
        <img src="../../assets/logo.png" class="image">
        <div class="text1">{{item.memberRole}}</div>
        <div class="text">{{item.ffeeAccount.name}}</div>
      </el-card>
    </div>
    <div v-else class="pane">
      <div class="title nofamily">{{$t('pages.family.message.noFamily')}}</div>
      <div class="title">
        <el-button @click="handleCreateFamily">{{$t('pages.family.button.create')}}</el-button>
        <el-button @click="handleJoinFamily">{{$t('pages.family.button.join')}}</el-button>
      </div>
    </div>
  </div>
</template>

<script>
  import { mapGetters } from 'vuex'
  import { logger } from 'mx-app-utils'
  import { MxAjax } from 'mx-vue-el-utils'

  export default {
    data () {
      return {
        family: null
      }
    },
    computed: {
      ...mapGetters(['loginUser'])
    },
    methods: {
      handleCreateFamily () {
        // TODO
        logger.debug('create an new family')
      },
      handleJoinFamily () {
        // TODO
        logger.debug('join the family')
      }
    },
    mounted () {
      let url = '/rest/family?userId=' + this.loginUser.id
      logger.debug('send GET "%s".', url)
      let fnSuccess = (data) => {
        if (data && data.name) {
          this.family = data
        }
      }
      MxAjax.get({url, fnSuccess})
    }
  }
</script>
