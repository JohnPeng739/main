<style rel="stylesheet/less" lang="less" scoped>
  .gauge {
    width: 300px;
    height: 300px;
  }

  .pie {
    width: 1000px;
    height: 300px;
  }

  .graph {
    width: 1000px;
    height: 500px;
  }
</style>

<template>
  <div>
    <h1>仪表盘</h1>
    <div id="gauge" class="gauge"></div>
    <br/>
    <br/>
    <h1>饼图</h1>
    <div id="pie" class="pie"></div>
    <br/>
    <br/>
    <h1>图</h1>
    <div id="graph" class="graph"></div>
  </div>
</template>

<script>
  import echarts from 'echarts'
  import {round} from 'mx-app-utils'
  import EchartsUtils from '@/utils/echarts'

  export default {
    name: 'test-echarts-page',
    data () {
      return {
        interval: null
      }
    },
    methods: {
      fillGaugeValue (gauge, option) {
        let value = Math.random() * 1024
        option = EchartsUtils.freshGaugeValue(gauge, option, value, 3)
        gauge.setOption(option, false)
      },
      fillPieValue (pie, option) {
        let mem = []
        let cpu = []
        let net = []
        for (let index = 1; index < 5; index++) {
          mem.push({value: round(Math.random() * 1024, 3), name: 'mem' + index})
          cpu.push({value: round(Math.random() * 100, 2), name: 'cpu' + index})
          net.push({value: round(Math.random() * 100, 2), name: 'net' + index})
        }
        option = EchartsUtils.freshPieValue(pie, option, [mem, cpu, net], true)
        pie.setOption(option, false)
      },
      fillGraphValue (graph) {
        let nodes = ['node1', 'node2', 'node3', 'node4', 'node5', 'node6', 'node7', 'node8']
        let links = [{source: 'node1', target: 'node5', value: ''}, {source: 'node6', target: 'node5', value: ''}]
        let option = EchartsUtils.createGraphOption('样例图', nodes, links)
        graph.setOption(option, false)
      }
    },
    mounted () {
      let gauge = echarts.init(document.getElementById('gauge'))
      let gaugeOption = EchartsUtils.createGaugeOption(gauge, '资源', 0, 1024, 512, '内存', 'M')
      let pie = echarts.init(document.getElementById('pie'))
      let pieOption = EchartsUtils.createPieOption(pie, '资源', ['内存(M)', 'CPU(%)', '网络(%)'])
      let graph = echarts.init(document.getElementById('graph'))
      this.interval = setInterval(() => {
        this.fillGaugeValue(gauge, gaugeOption)
        this.fillPieValue(pie, pieOption)
        this.fillGraphValue(graph)
      }, 3000)
      this.fillGaugeValue(gauge, gaugeOption)
      this.fillPieValue(pie, pieOption)
      this.fillGraphValue(graph)
    }
  }
</script>
