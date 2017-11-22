<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .content {
    margin: 30px auto;
    text-align: center;
    .divMap {
      display: inline-block;
      height: 400px;
      padding: 0 5px;
    }
  }
</style>

<template>
  <div class="content">
    <el-row type="flex">
      <el-col :span="24">
        <div id="divTotal" class="divMap"></div>
        <div id="divError" class="divMap"></div>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-table :data="tableData">
          <el-table-column v-for="item in columns" :key="item" :prop="item" :label="item" align="left"></el-table-column>
        </el-table>
      </el-col>
    </el-row>
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
        columns: [],
        tableData: [],
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
          let columns = ['地市']
          let tableData = [{'地市': '抽取数'}, {'地市':'错误数'}]
          if (data && data.cities) {
            let maxTotal = 0
            let maxError = 0
            let totals = []
            let errors = []
            data.cities.forEach(row => {
              let {name, total, error} = row
              columns.push(name)
              maxTotal = Math.max(maxTotal, total)
              maxError = Math.max(maxError, error)
              totals.push({name, value: total})
              errors.push({name, value: error})
              tableData[0][name] = total
              tableData[1][name] = error
            })
            optionTotal.series[0].data = totals
            optionTotal.visualMap.max = maxTotal
            optionError.series[0].data = errors
            optionError.visualMap.max = maxError
          }
          echartsTotal.setOption(optionTotal, false)
          echartsError.setOption(optionError, false)
          this.columns = columns
          this.tableData = tableData
        })
      },
      resizeChart(echart, parent) {
        let {clientWidth, clientHeight} = parent
        echart.style.width = (clientWidth / 2 - 20) + 'px'
      }
    },
    mounted() {
      let divParent = document.getElementsByClassName('content')[0]
      let divTotal = document.getElementById('divTotal')
      let divError = document.getElementById('divError')
      this.resizeChart(divTotal, divParent)
      this.resizeChart(divError, divParent)
      let echartsTotal = echarts.init(divTotal)
      let echartsError = echarts.init(divError)
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
