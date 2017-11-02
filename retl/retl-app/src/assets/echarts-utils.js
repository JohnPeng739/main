const createGaugeOption = function (title, min, max, value, valueTitle, unitTitle) {
  if (!unitTitle) {
    unitTitle = ''
  }
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

const createDefaultPieSerie = function () {
  return {
    name: '任务数量',
    type: 'pie',
    radius: [34, 80],
    center: [200, '50%'],
    roseType: 'radius',
    label: {normal: {show: true}, emphasis: {show: true}},
    labelLine: {normal: {show: true, length: 5, length2: 5}, emphasis: {show: true, length: 5, length2: 5}},
    itemStyle: {normal: {label: {show: true, formatter: '{c} ({d}%)'}, labelLine: {show: false}}},
    markPoint: {data: [
      {symbol: 'circle', symbolSize: 65, name: '总数', x: 200, y: 200, value: 100,
        itemStyle: {normal: {color: 'green'}}, label: {normal: {formatter: '{a}\n\n{c}'}}}
    ]},
    data: [{value: 10, name: 'item1'}, {value: 30, name: 'item2'}, {value: 50, name: 'item3'}]
  }
}

const createPieOption = function (title, dataItems) {
  let series = []
  if (dataItems && dataItems.length > 0) {
    dataItems.forEach(row => {
      let serie = createDefaultPieSerie()
      serie.name = row.name
      serie.center[0] = row.total.x
      serie.markPoint.data[0].name = row.total.title
      serie.markPoint.data[0].x = row.total.x
      series.push(serie)

    })
  } else {
    series.push(createDefaultPieSerie())
  }
  return {
    title: {text: title, top: 'bottom', left: 'center'},
    tooltip: {formatter: '{a} <br/>{b} : {c}'},
    legend: {left: 'center', bottom: '30', data: ['item1', 'item2', 'item3']},
    toolbox: {feature: {restore: {}, saveAsImage: {}}},
    calculable: true,
    series: series
  }
}

const etlBolts = [
  {name: '归一化', x: 300, y: 200, value: 1},
  {name: '校验', x: 500, y: 100, value: 1},
  {name: '转换', x: 700, y: 100, value: 1},
  {name: 'JMS缓存', x: 900, y: 200, value: 1},
  {name: '错误处理', x: 600, y: 300, value: 1}
]

const etlRelations = [
  {source: '归一化', target: '校验', value: 'default', label: {normal: {show: true, formatter: '{c}'}}},
  {source: '校验', target: '转换', value: 'default', label: {normal: {show: true, formatter: '{c}'}}},
  {source: '转换', target: 'JMS缓存', value: 'default', label: {normal: {show: true, formatter: '{c}'}}},
  {source: '归一化', target: '错误处理', value: 'error-stream', label: {normal: {show: true, formatter: '{c}'}}},
  {source: '校验', target: '错误处理', value: 'error-stream', label: {normal: {show: true, formatter: '{c}'}}},
  {source: '转换', target: '错误处理', value: 'error-stream', label: {normal: {show: true, formatter: '{c}'}}},
  {source: '错误处理', target: 'JMS缓存', value: 'default', label: {normal: {show: true, formatter: '{c}'}}}
]

const createGraphOption = function (title, topology) {
  let data = []
  let links = []
  if (topology) {
    let {type, spouts} = topology
    let spoutTarget = (type === 'retl' ? '归一化' : 'JDBC持久化')
    let maxParallelism = 1
    if (spouts && spouts.length > 0) {
      for (let index = 0; index < spouts.length; index ++) {
        let {name, parallelism} = spouts[index]
        data.push({name, x: 100, y: 100 + index * 200, value: parallelism})
        maxParallelism = Math.max(maxParallelism, parallelism)
        links.push({source: name, target: spoutTarget, value: 'default', label: {normal: {show: true, formatter: '{c}'}}})
      }
    }
    let y = (spouts.length) * 200 / 2
    if (type === 'retl') {
      for (let index = 0; index < etlBolts.length; index ++) {
        if ('归一化' === etlBolts[index].name || 'JMS缓存' === etlBolts[index].name) {
          etlBolts[index].y = y
        }
        if ('错误处理' === etlBolts[index].name) {
          etlBolts[index].y = Math.max(100 + (spouts.length - 1) * 200, 300)
        }
        etlBolts[index].value = maxParallelism
      }
      data = data.concat(etlBolts)
      links = links.concat(etlRelations)
    } else {
      data.push({name: 'JDBC持久化', x: 900, y, value: maxParallelism})
    }
  }
  return {
    title: {text: title, top: 'bottom', left: 'center'},
    tooltip: {},
    toolbox: {feature: {restore: {}, saveAsImage: {}}},
    animationDurationUpdate: 1500,
    animationEasingUpdate: 'quinticInOut',
    series: [{
      type: 'graph',
      layout: 'none',
      symbolSize: 100,
      roam: false,
      label: {normal: {show: true}},
      edgeSymbol: ['circle', 'arrow'],
      edgeSymbolSize: [6, 12],
      edgeLabel: {normal: {textStyle: {fontSize: 12}}},
      data: data,
      links: links,
      lineStyle: {normal: {opacity: 0.9, width: 2, curveness: 0}}
    }]
  }
}

export {createGaugeOption, createPieOption, createGraphOption}
