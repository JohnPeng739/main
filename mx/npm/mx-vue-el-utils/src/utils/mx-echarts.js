import { round } from 'mx-app-utils'
import {MxLocale} from './mx-locale'

const i18n = MxLocale.i18n()

class MxEcharts {
  /**
   * 创建一个仪表盘配置信息对象
   * @param title 仪表盘标题
   * @param min 最小值，默认为0
   * @param max 最大值，默认为100
   * @param value 当前值， 默认为1-100的随机数
   * @param valueTitle 值的标题
   * @param unitTitle 值的单位
   * @returns {{title: {text: *, top: string, left: string}, tooltip: {formatter: string}, toolbox: {feature: {restore: {}, saveAsImage: {}}}, series: [null]}} 创建的配置信息对象
   */
  static createGaugeOption (gauge, title, min, max, value, valueTitle, unitTitle) {
    min = min || 0
    max = max || 100
    value = round(value || (Math.random() * 100), 0)
    valueTitle = valueTitle || i18n.t('common.value')
    unitTitle = unitTitle || ''
    return {
      title: {text: title, top: 'bottom', left: 'center'},
      tooltip: {formatter: '{a} <br/>{b} : {c}'},
      toolbox: {feature: {restore: {}, saveAsImage: {}}},
      series: [{
        name: title,
        type: 'gauge',
        min: min,
        max: max,
        splitNumber: 4,
        axisLine: {lineStyle: {color: [[0.25, 'green'], [0.75, 'blue'], [1, 'red']], width: 15}},
        splitLine: {length: 20, lineStyle: {width: 1}},
        axisTick: {splitNumber: 1},
        pointer: {length: '80%', width: 4},
        detail: {formatter: '{value}' + unitTitle, offsetCenter: [0, '25%'], fontSize: 20},
        data: [{value: value, name: valueTitle}]
      }]
    }
  }

  /**
   * 刷新并设置仪表盘的值
   * @param option 仪表盘配置信息对象
   * @param value 当前值
   * @param valueDigits 当前值要保留的小数位数，默认为0位
   * @param min 最小值
   * @param max 最大值
   * @returns {*} 设置好值的配置信息对象
   */
  static freshGaugeValue (gauge, option, value, valueDigits, min, max) {
    if (!option) {
      throw new Error('The gauge is not be initialized.')
    }
    if (value && typeof value === 'number') {
      option.series[0].data[0].value = round(value, valueDigits || 0)
    }
    if (min && typeof min === 'number') {
      option.series[0].min = min
    }
    if (max && typeof max === 'number') {
      option.series[0].max = max
    }
    return option
  }

  static createPieOption (pie, title, items) {
    let dataLegend = []
    let series = []
    if (items && items.length > 0) {
      let width = pie.getWidth() / (items.length * 2)
      items.forEach((item, index) => {
        dataLegend.push(item)
        series.push({
          name: item,
          type: 'pie',
          radius: [34, 80],
          center: [width + index * 2 * width, '50%'],
          roseType: 'radius',
          label: {normal: {show: true}, emphasis: {show: true}},
          labelLine: {normal: {show: true, length: 5, length2: 5}, emphasis: {show: true, length: 5, length2: 5}},
          itemStyle: {normal: {label: {show: true, formatter: '{c} ({d}%)'}, labelLine: {show: false}}},
          data: [{value: 10, name: 'item1'}, {value: 30, name: 'item2'}, {value: 50, name: 'item3'}]
        })
      })
    }
    return {
      title: {text: title, top: 'bottom', left: 'center'},
      tooltip: {formatter: '{a} <br/>{b} : {c}'},
      legend: {left: 'center', bottom: '30', data: dataLegend},
      toolbox: {feature: {restore: {}, saveAsImage: {}}},
      calculable: true,
      series: series
    }
  }

  static freshPieValue (pie, option, dataItems, sumTotal) {
    if (!option) {
      throw new Error('The pie is not be initialized.')
    }
    if (dataItems && dataItems.length > 0) {
      let width = pie.getWidth() / (dataItems.length * 2)
      let height = pie.getHeight() / 2
      dataItems.forEach((item, index) => {
        let total = 0
        let data = []
        if (item && item.length > 0) {
          item.forEach(i => {
            if (i && i.value && typeof i.value === 'number' && i.name && typeof i.name === 'string') {
              total += i.value
              data.push(i)
            }
          })
        } else if (item) {
          total = item.value
          data.push(item)
        }
        option.series[index].data = data
        if (sumTotal) {
          option.series[index].markPoint = {
            data: [{
              symbol: 'circle',
              symbolSize: 65,
              name: i18n.t('common.total'),
              x: width + index * 2 * width,
              y: height,
              value: round(total),
              itemStyle: {normal: {color: 'green'}},
              label: {normal: {formatter: '{a}\n\n{c}'}}
            }]
          }
        }
      })
    }
    return option
  }

  static createGraphOption (title, nodes, links, type) {
    let nodesData = []
    let linksData = []
    if (nodes && nodes.length > 0) {
      nodes.forEach(row => {
        if (row && row.name && row.value) {
          nodesData.push({name: row.name, value: row.value, x: null, y: null})
        } else if (row) {
          nodesData.push({name: row, value: 1, x: null, y: null})
        }
      })
    } else {
      throw new Error('Not set the graph\'s node data.')
    }
    if (links && links.length > 0) {
      links.forEach(row => {
        if (row) {
          let {source, target, value} = row
          linksData.push({source, target, value, label: {normal: {show: true, formatter: '{c}'}}})
        }
      })
    }
    if (!(type === 'none' || type === 'force' || type === 'circular')) {
      type = 'circular'
    }
    return {
      title: {text: title || '', top: 'bottom', left: 'center'},
      tooltip: {},
      toolbox: {feature: {restore: {}, saveAsImage: {}}},
      animationDurationUpdate: 1500,
      animationEasingUpdate: 'quinticInOut',
      series: [{
        type: 'graph',
        layout: type,
        symbolSize: 30,
        roam: true,
        label: {normal: {show: true}},
        edgeSymbol: ['circle', 'arrow'],
        edgeSymbolSize: [6, 12],
        edgeLabel: {normal: {textStyle: {fontSize: 12}}},
        draggable: true,
        data: nodesData,
        links: linksData,
        lineStyle: {normal: {opacity: 0.9, width: 2, curveness: 0}}
      }]
    }
  }
}

export default MxEcharts

export { MxEcharts }
