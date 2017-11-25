<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .chartContainer {
    width: 100%;
    height: 500px;
  }
</style>

<template>
  <div class="chartContainer">
    <slot></slot>
    <!-- div ref="paneChart" id="paneChart" :style="chartStyle"></div-->
  </div>
</template>

<script>
  import echarts from 'echarts'

  export default {
    name: 'pane-echarts',
    props: ['paneId', 'options'],
    data() {
      return {
        myChart: null,
        mainChart: null,
        chartWidth: 300,
        chartHeight: 300,
        timer: false
      }
    },
    computed: {
      chartStyle() {
        let {chartWidth, chartHeight} = this

        return 'width:' + chartWidth + 'px;' + 'height:' + chartHeight + 'px;'
      }
    },
    methods: {
      resize() {
        if (this.mainChart) {
          let chartBox = document.getElementsByClassName('chartContainer')[0]
          this.chartWidth = chartBox.clientWidth
          this.chartHeight = chartBox.clientHeight
          this.myChart.style = this.chartStyle
          this.$nextTick(_ => this.mainChart.resize())
        }
      },
      setOptions(options) {
        if (options && this.mainChart) {
          this.mainChart.setOption(options)
        }
      }
    },
    mounted() {
      this.$nextTick(_ => {
        this.myChart = document.getElementById(this.paneId)
        this.resize()
        this.mainChart = echarts.init(this.myChart)
        this.mainChart.setOption(this.options)
        window.onresize = _ => {
          return (_ => {
            if (!this.timer) {
              this.timer = true
              setTimeout(_ => {
                this.resize()
                this.timer = false
              }, 400)
            }
          })()
        }
      })
    }
  }
</script>
