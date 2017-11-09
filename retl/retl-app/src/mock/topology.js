import {topologySample} from '../modules/manage/store/modules/topology'

let topology = {
  'id': '@guid',
  'name': '@title',
  'updatedTime|+1': 34523323,
  'submitted|5-5': true,
  'submittedTime|+1': 63452345234,
  'submitInfo': '@cparagraph',
  'operator': '@ctitle',
  'topologyId': '@id',
  'topologyContent': _ => JSON.stringify(topologySample())
}

export default [{
  path: /\/rest\/topologies(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    'data|5-100': [topology]
  }
}, {
  path: /\/rest\/topologies(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    'pagination': {total: 60, size: 20, page: 1},
    'data|5-100': [topology]
  }
}, {
  path: /\/rest\/topology\?topoogyId=\w(&\w=\w)*/,
  type: 'get',
  data: {
    data: topology
  }
}, {
  path: /\/rest\/topologies\/realStatus\?topologyIds=\w(&\w=\w)*/,
  type: 'get',
  data: {
    'data|1-5': [{
      'id': '@guid',
      'name': '@title',
      'status|1': ['active', 'inactive', 'na']
    }]
  }
}, {
  path: /\/rest\/topology\/delete\?userCode=\w&topologyId=\w(&\w=\w)*/,
  type: 'get',
  data: {
    'data|1': true
  }
}, {
  path: /\/rest\/topology\/kill\?userCode=\w&topologId=\w(&\w=\w)*/,
  type: 'get',
  data: {
    'data': topology
  }
}, {
  path: /\/rest\/topology\/submit\?userCode=\w&topologyId=\w(&\w=\w)*/,
  type: 'post',
  data: {
    data: topology
  }
}, {
  path: /\/rest\/topology\/save\?userCode=\w&topologyId=\w(&\w=\w)*/,
  type: 'post',
  data: {
    data: topology
  }
}, {
  path: /\/rest\/topology\/submit\/\w\?userCode=\w&simulation=\w(&\w=\w)*/,
  type: 'get',
  data: {
    data: topology
  }
}, {
  path: /\/rest\/topology\/validate(\?type=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    'data|1': true
  }
}, {
  path: /\/rest\/topology\/supported(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    data: {
      topologyTypes: [
        {label: '1. 数据抽取、校验、转换', value: 'retl'},
        {label: '2. 数据存储', value: 'persist'}
      ],
      spoutTypes: [
        {label: '1. 推送型JMS', value: 'jms'},
        {label: '2. 拉取型JMS（推荐使用）', value: 'jmsPull'},
        {label: '3. 关系型数据库', value: 'jdbc'}
      ],
      boltType: [
        {label: '1. 归一化处理', value: 'structure'},
        {label: '2. 数据校验', value: 'validate'},
        {label: '3. 数据转换', value: 'transform'},
        {label: '4. 错误处理', value: 'error'},
        {label: '5. JMS存储', value: 'jms'},
        {label: '6. 关系型数据库存储', value: 'jdbc'}
      ],
      jdbcDriverTypes: [
        {label: '1. MySQL', value: 'com.mysql.jdbc.Driver'},
        {label: '2. Oracle', value: 'oracle.jdbc.driver.OracleDriver'},
        {label: '3. H2', value: 'org.h2.Driver'}
      ],
      validateTypes: [
        {label: '1. 空值校验', value: 'NullValidate'},
        {label: '2. 类型校验', value: 'TypeValidate'},
        {label: '3. 长度校验', value: 'LengthValidate'},
        {label: '4. 数据范围校验', value: 'RangeValidate'},
        {label: '5. 正则校验', value: 'RegExpValidate'},
        {label: '7. 被包含（存在性）校验', value: 'InValidate'}
      ],
      validateRuleTypes: [
        {label: '1. 字符串类型', value: 'STRING'},
        {label: '2. 时间类型', value: 'DATE'},
        {label: '3. 整数类型', value: 'INT'},
        {label: '4. 小数类型', value: 'DECIMAL'},
        {label: '5. 布尔类型', value: 'BOOL'}
      ],
      transformTypes: [
        {label: '2. 四则计算转换', value: 'FormulaTransform'},
        {label: '1. 字段合并转换', value: 'MergeTransform'},
        {label: '6. 字符串子串截取转换', value: 'SubStringTransform'}
      ],
      jmsTypes: [
        {label: 'ACTIVEMQ', value: 'ACTIVEMQ'},
        {label: 'JNDI', value: 'JNDI'}
      ]
    }
  }
}]
