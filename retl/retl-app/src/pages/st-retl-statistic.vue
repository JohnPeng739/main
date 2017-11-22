<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .content {
    margin: 30px auto;
    width: 100%;
    height: 100%;
    text-align: center;
    .divMap {
      display: inline-block;
      width: 550px;
      height: 400px;
      padding: 0 5px;
    }
  }
</style>

<template>
  <div class="content">
    <div id="divTotal" class="divMap broder"></div>
    <div id="divError" class="divMap"></div>
  </div>
</template>

<script>
  import echarts from 'echarts'
  import {logger} from 'mx-app-utils'
  import {get} from '../assets/ajax'
  import 'echarts/map/js/province/shandong'
  import 'echarts/map/json/province/shandong.json'

  export default {
    name: 'page-st-retl-statistic',
    data() {
      return {
        interval: null
      }
    },
    methods: {
      formatter(params) {
        if (params && params.value) {
          return params.name + '\n' + params.value
        } else {
          return params.name
        }
      },
      createOption(subtext, color) {
        return {
          title: {text: subtext, left: 'center'},
          visualMap: {min: 0, max: 2500, left: 'left', text: ['高', '低'], calculable: true, show: false,
          inRange: {color: ['lightgray', color]}},
          series: [{
            name: '抽取量',
            type: 'map',
            mapType: '山东',
            roam: false,
            label: {normal: {show: true, formatter: this.formatter}, emphasis: {show: true}},
            data: []
          }]
        }
      },
      refreshData(echartsTotal, echartsError, optionTotal, optionError) {
        let url = '/rest/retl-statistic'
        logger.debug('send GET "%s"', url)
        get(url, data => {
          if (data && data.total) {
            let max = 0
            data.total.forEach(row => max = Math.max(max, row.value))
            optionTotal.series[0].data = data.total
            optionTotal.visualMap.max = max
          }
          if (data && data.error) {
            let max = 0
            data.total.forEach(row => max = Math.max(max, row.value))
            optionError.series[0].data = data.error
            optionError.visualMap.max = max
          }
          echartsTotal.setOption(optionTotal, false)
          echartsError.setOption(optionError, false)
        })
      }
    },
    mounted() {
      let echartsTotal = echarts.init(document.getElementById('divTotal'))
      let echartsError = echarts.init(document.getElementById('divError'))
      let optionTotal = this.createOption('抽取数', 'green')
      let optionError = this.createOption('错误数', 'red')
      this.interval = setInterval(_ => this.refreshData(echartsTotal, echartsError, optionTotal, optionError), 5000)
      this.$nextTick(_ => this.refreshData(echartsTotal, echartsError, optionTotal, optionError))
    },
    destroyed() {
      if (this.interval) {
        clearInterval(this.interval)
      }
    }
  }
</script>
