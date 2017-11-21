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
    <div id="divTotal" class="divMap"></div>
    <div id="divError" class="divMap"></div>
  </div>
</template>

<script>
  import echarts from 'echarts'
  import 'echarts/map/js/province/shandong'

  export default {
    name: 'page-st-retl-statistic',
    data() {
      return {
        echartsTotal: null,
        echartsError: null,
        optionTotal: null,
        optionError: null,
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
          title: {text: '山东全省警情实时汇聚', subtext: subtext, left: 'center'},
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
      refreshData() {
        this.optionTotal.series[0].data = [
          {name: '济南市', value: '1500'},
          {name: '青岛市', value: '1300'},
          {name: '烟台市', value: '650'},
          {name: '德州市', value: '700'},
          {name: '泰安市', value: '340'},
          {name: '东营市', value: '1000'},
          {name: '临沂市', value: '900'}
        ]
        this.optionTotal.visualMap.min = 0
        this.optionTotal.visualMap.max = 1500
        this.optionError.series[0].data = [
          {name: '济南市', value: '20'},
          {name: '烟台市', value: '5'},
          {name: '临沂市', value: '0'},
          {name: '青岛市', value: '10'},
          {name: '东营市', value: '19'},
          {name: '德州市', value: '8'}
        ]
        this.optionError.visualMap.min = 0
        this.optionError.visualMap.max = 20

        this.echartsTotal.setOption(this.optionTotal, false)
        this.echartsError.setOption(this.optionError, false)
      }
    },
    mounted() {
      this.echartsTotal = echarts.init(document.getElementById('divTotal'))
      this.echartsError = echarts.init(document.getElementById('divError'))
      this.optionTotal = this.createOption('抽取数', 'green')
      this.optionError = this.createOption('错误数', 'red')
      this.interval = setInterval(_ => this.refreshData(), 3000)
      this.refreshData()
    },
    destroyed() {
      if (this.interval) {
        clearInterval(this.interval)
      }
    }
  }
</script>
