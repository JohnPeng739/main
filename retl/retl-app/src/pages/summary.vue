<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";
</style>

<template>
  <div>
    <el-row type="flex">
      <el-col :span="12">
        <pane-echarts ref="paneEcharts1" paneId="paneEchart1" :options="options">
          <div id="paneEchart1"></div>
        </pane-echarts>
      </el-col>
      <el-col :span="12">
        <pane-echarts ref="paneEcharts2" paneId="paneEchart2" :options="options">
          <div id="paneEchart2"></div>
        </pane-echarts>
      </el-col>
    </el-row>
    <el-button @click="handleClicked">更新数据</el-button>
  </div>
</template>

<script>
  import echarts from 'echarts'
  import PaneEcharts from '../components/pane-echarts.vue'

  export default {
    name: 'page-summary',
    components: {PaneEcharts},
    data() {
      return {
        options: {
          title: {text: 'ECharts 入门示例'},
          tooltip: {},
          xAxis: {
            data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
          },
          yAxis: {},
          series: [{
            name: '销量',
            type: 'bar',
            data: [5, 20, 36, 10, 10, 20]
          }]
        }
      }
    },
    methods: {
      handleClicked() {
        let options = {
          title: {text: ''},
          tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)"
          },
          legend: {
            orient: 'vertical',
            x: 'left',
            data:['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']
          },
          series: [
            {
              name:'访问来源',
              type:'pie',
              radius: ['50%', '70%'],
              avoidLabelOverlap: false,
              label: {
                normal: {
                  show: false,
                  position: 'center'
                },
                emphasis: {
                  show: true,
                  textStyle: {
                    fontSize: '30',
                    fontWeight: 'bold'
                  }
                }
              },
              labelLine: {
                normal: {
                  show: false
                }
              },
              data:[
                {value:335, name:'直接访问'},
                {value:310, name:'邮件营销'},
                {value:234, name:'联盟广告'},
                {value:135, name:'视频广告'},
                {value:1548, name:'搜索引擎'}
              ]
            }
          ]
        }
        this.$refs['paneEcharts1'].setOptions(options)
      }
    },
    mounted() {
      let options = {
        title: {text: 'ECharts 入门示例'},
        tooltip: {},
        xAxis: {
          data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
        },
        yAxis: {},
        series: [{
          name: '销量',
          type: 'bar',
          data: [5, 20, 36, 10, 10, 20]
        }]
      }
      this.options = options
    }
  }
</script>
