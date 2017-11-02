<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .divTopology {
    width: 1000px;
    height: 500px;
    margin: 0 auto;
  }
</style>

<template>
  <el-form class="layout-form" label-width="80px">
    <el-row type="flex">
      <el-col :span="12">
        <el-form-item label="名称">
          <el-input v-model="topology.name" :readonly="true"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="操作员">
          <el-input v-model="topology.operator" :readonly="true"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="6">
        <el-form-item label="是否提交">
          <el-switch v-model="topology.submitted" :disabled="true"></el-switch>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="提交时间">
          <span>{{topology.submitted ? longDate(topology.submittedTime) : 'NA'}}</span>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="拓扑ID">
          <el-input v-model="topology.topologyId" :readonly="true"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <div id="divTopology" class="divTopology"></div>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="配置信息">
          <el-input type="textarea" :value="prettyJson(topology.topologyContent)"
                    :rows="15" :readonly="true"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="提交信息">
          <el-input type="textarea" :value="topology.submitInfo"
                    :rows="15" :readonly="true"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import echarts from 'echarts'
  import {logger} from 'dsutils'
  import {get} from '../../assets/ajax'
  import {createGraphOption} from '../../assets/echarts-utils'
  import {formatDateTime} from '../../assets/date-utils'

  export default {
    name: 'page-task-detail',
    data() {
      return {
        topology: {name: '', topologyId: '', submitted: false, submittedTime: 0, operator: ''}
      }
    },
    methods: {
      longDate(time) {
        if (time) {
          let datetime = new Date(time)
          return formatDateTime(datetime)
        } else {
          return ''
        }
      },
      prettyJson(content) {
        if (content) {
          let json = JSON.parse(content)
          return JSON.stringify(json, null, '    ')
        }
      },
      setTopology(topology) {
        this.topology = topology
        let data = JSON.parse(topology.topologyContent)
        let {type, spouts} = data
        let option = createGraphOption('拓扑结构图', {type, spouts})
        let echartsTopology = echarts.init(document.getElementById('divTopology'))
        echartsTopology.setOption(option, false)
      }
    },
    mounted() {
      let topologyId = this.$route.params.topologyId
      let url = '/rest/topology?topologyId=' + topologyId
      logger.debug('send GET "%s"', url)
      get(url, data => this.setTopology(data))
    }
  }
</script>
