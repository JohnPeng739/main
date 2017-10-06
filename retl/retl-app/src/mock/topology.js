export default [{
  path: /\/rest\/topology\/validates\/supported(\?\w=\w)*/,
  type: 'get',
  data: {
    data: [{
      label: '空值校验',
      value: 'NullValidate'
    }, {
      label: '类型校验',
      value: 'TypeValidate'
    }, {
      label: '长度校验',
      value: 'LengthValidate'
    }, {
      label: '数据范围校验',
      value: 'RangeValidate'
    }, {
      label: '正则表达式校验',
      value: 'RegExpValidate'
    }]
  }
}, {
  path: /\/rest\/topology\/transforms\/supported(\?\w=\w)*/,
  type: 'get',
  data: {
    data: [{
      label: '四则计算(Formula)',
      value: 'FormulaTransform'
    }, {
      label: '字段合并(Merge)',
      value: 'MergeTransform'
    }]
  }
}, {
  path: /\/rest\/topology\/validate\/type-validate\/types(\?\w=\w)*/,
  type: 'get',
  data: {
    data: [{
      label: '字符串类型',
      value: 'STRING'
    }, {
      label: '时间类型',
      value: 'DATE'
    }, {
      label: '整数类型',
      value: 'INT'
    }, {
      label: '小数类型',
      value: 'DECIMAL'
    }, {
      label: '布尔类型',
      value: 'BOOL'
    }]
  }
}, {
  path: /\/rest\/topology\/submit(\?\w=\w)*/,
  type: 'post',
  data: {
    'data|5-5': true
  }
}]
