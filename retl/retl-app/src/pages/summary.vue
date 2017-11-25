<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .layout-col {
    text-align: center;
  }

  .divGauge {
    display: inline-block;
    width: 300px;
    height: 240px;
  }

  .divPieTopologies {
    display: inline-block;
    width: 950px;
    height: 400px;
  }
</style>

<template>
  <div>
    <el-row type="flex" justify="center">
      <el-col :span="24" class="layout-col">
        <div id="divSlots" class="divGauge"></div>
        <div id="divCpu" class="divGauge"></div>
        <div id="divMem" class="divGauge"></div>
      </el-col>
    </el-row>
    <el-row type="flex" justify="center">
      <el-col :span="24" class="layout-col">
        <div id="divTopologies" class="divPieTopologies"></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import echarts from 'echarts'
  import {logger} from 'mx-app-utils'
  import {get} from '../assets/ajax'
  import {createGaugeOption, createPieOption} from '../assets/echarts-utils'

  export default {
    name: 'page-summary',
    data() {
      return {
        interval: null
      }
    },
    methods: {
      refreshData(echartSlots, slotsOption, echartCpu, cpuOption, echartMem, memOption, echartTopologies, topologiesOption) {
        let url = '/api/v1/cluster/summary'
        logger.debug('send GET "%s"', url)
        get(url, data => {
          logger.debug('Response data: %j.', data)
          if (data) {
            this.refreshGauge(echartSlots, slotsOption, data.slotsTotal, data.slotsUsed)
            this.refreshGauge(echartCpu, cpuOption, data.totalCpu, data.totalCpu - data.availCpu)
            this.refreshGauge(echartMem, memOption, data.totalMem, data.totalMem - data.availMem)
          }
        })
        url = '/api/v1/topology/summary'
        logger.debug('send GET "%s"', url)
        get(url, data => {
          logger.debug('Response data: %j.', data)
          if (data && data.topologies && data.topologies.length > 0) {
            let legendData = []
            let taskTotal = 0
            let tasks = []
            let cpuTotal = 0
            let cpus = []
            let memTotal = 0
            let mems = []
            data.topologies.forEach(row => {
              let {name, tasksTotal, assignedCpu, assignedTotalMem} = row
              legendData.push(name)
              taskTotal += tasksTotal
              tasks.push({value: tasksTotal, name})
              cpuTotal += assignedCpu
              cpus.push({value: assignedCpu, name})
              memTotal += assignedTotalMem
              mems.push({value: assignedTotalMem, name})
            })
            topologiesOption.series[0].markPoint.data[0].value = taskTotal
            topologiesOption.series[0].data = tasks
            topologiesOption.series[1].markPoint.data[0].value = cpuTotal
            topologiesOption.series[1].data = cpus
            topologiesOption.series[2].markPoint.data[0].value = memTotal
            topologiesOption.series[2].data = mems
            topologiesOption.legend.data = legendData
            echartTopologies.setOption(topologiesOption, false)
          }
        })
      },
      refreshGauge(echart, option, total, used) {
        option.series[0].max = total
        option.series[0].data[0].value = used
        echart.setOption(option, false)
      }
    },
    mounted() {
      let slotsOption = createGaugeOption('插槽数量', 0, 100, 50, '已用')
      let cpuOption = createGaugeOption('CPU数量', 0, 50, 25, '已用')
      let memOption = createGaugeOption('内存数量', 0, 100, 50, '已用', 'MB')
      let dataItems = [
        {name: '任务数量', total: {title: '总数', x: 200}},
        {name: 'CPU分配', total: {title: 'CPU', x: 450}},
        {name: '内存分配', total: {title: '内存', x: 700}}
      ]
      let topologiesOption = createPieOption('拓扑情况', dataItems)
      let echartSlots = echarts.init(document.getElementById('divSlots'))
      let echartCpu = echarts.init(document.getElementById('divCpu'))
      let echartMem = echarts.init(document.getElementById('divMem'))
      let echartTopologies = echarts.init(document.getElementById('divTopologies'))
      this.interval = setInterval(_ => {
        this.refreshData(echartSlots, slotsOption, echartCpu, cpuOption, echartMem, memOption, echartTopologies, topologiesOption)
      }, 5000)
      this.refreshData(echartSlots, slotsOption, echartCpu, cpuOption, echartMem, memOption, echartTopologies, topologiesOption)
    },
    destroyed() {
      if (this.interval) {
        clearInterval(this.interval)
      }
    }
  }
</script>
