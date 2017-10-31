<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .button {
    text-align: center;
    padding-top: 15px;
  }
</style>

<template>
  <div>
    <el-row type="flex">
      <el-col :span="24">配置详细内容</el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-input type="textarea" v-model="result" :autosize="{minRows: 6, maxRows: 21}"
                  :readonly="true"></el-input>
      </el-col>
    </el-row>
    <el-row type="flex" justify="center">
      <el-col :span="24" class="button">
        <el-button type="primary" @click="handleSubmit('save')">保存计算拓扑</el-button>
        <!--el-button type="primary" @click="handleSubmit('submit')">保存&提交计算拓扑</el-button-->
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {logger} from 'dsutils'
  import {info} from '../../assets/notify'
  import {post} from '../../assets/ajax'

  export default {
    name: 'topology-submit',
    data() {
      return {
      }
    },
    computed: {
      ...mapGetters(['topology']),
      result() {
        return JSON.stringify(this.topology, null, '    ')
      }
    },
    methods: {
      ...mapActions(['cacheClean', 'goto']),
      handleSubmit(type) {
        let topology = this.topology
        let id = topology.id
        delete topology.id
        let url = '/rest/topology/' + (type === 'save' ? 'save' : 'submit')
        if (id) {
          url += '?topologyId=' + id
        }
        logger.debug('send POST "%s"', url)
        post(url, topology, data => {
          if (data) {
            this.goto({owner: this, path: '/tasks/list'})
            this.cacheClean()
            info( '提交计算拓扑成功。')
          }
        })
      }
    }
  }
</script>
