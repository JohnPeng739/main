import {topologySample} from '../modules/manage/store/modules/topology'

export default [{
  path: /\/rest\/topology\/types(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    data: {
      topologyTypes: {
        'retl': '数据抽取、校验、转换拓扑',
        'persist': '数据存储拓扑'
      },
      spoutTypes: {
        'jms': '推送型JMS',
        'jmsPull': '拉取型JMS（推荐使用）',
        'jdbc': '轮询关系型数据库'
      },
      boltTypes: {
        'structure': '归一化处理',
        'validate': '数据校验',
        'transform': '数据转换',
        'error': '错误处理',
        'jms': 'JMS存储',
        'jdbc': 'JDBC存储'
      },
      jdbcDriverTypes: {
        'mysql': 'com.mysql.jdbc.Driver',
        'h2': 'org.h2.Driver'
      }
    }
  }
}, {
  path: /\/rest\/topology\/supported(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    data: {
      jmsTypes: ['ACTIVEMQ', 'JNDI'],
      validateTypes: [{
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
      }],
      validateRuleTypes: [{
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
      }],
      transformTypes: [{
        label: '四则计算(Formula)',
        value: 'FormulaTransform'
      }, {
        label: '字段合并(Merge)',
        value: 'MergeTransform'
      }, {
        label: '子串截取(SubString)',
        value: 'SubStringTransform'
      }]
    }
  }
}, {
  path: /\/rest\/topologies(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    'pagination': {total: 60, size: 20, page: 1},
    'data|5-100': [{
      'id': '@id',
      'name': '@title',
      'updatedTime': '@integer',
      'submitted|5-5': true,
      'submittedTime': '@integer',
      'operator': '@ctitle',
      'topologyContent': JSON.stringify(topologySample)
    }]
  }
}, {
  path: /\/rest\/topology\/submit(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    'data|5-5': true
  }
}, {
  path: /\/rest\/topology\/validate(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    'data|5-5': true
  }
}]
