export const topologyTypes = [{
  value: 'RETL',
  label: '数据抽取、校验、转换'
}, {
  value: 'PERSIST',
  label: '数据存储'
}]

let findTypeLabel = (types, value) => {
  let label = ''
  types.forEach(type => {
    if (value === type.value) {
      label = type.label
    }
  })
  return label
}

export function topologyTypeLabel(value) {
  return findTypeLabel(topologyTypes, value)
}

export const spoutTypes = [{
  value: 'JMS',
  label: '推送型JMS'
}, {
  value: 'JMS_PULL',
  label: '拉取型JMS（推荐使用）'
}, {
  value: 'JDBC',
  label: '关系型数据库'
}]

export function spoutTypeLabel(value) {
  return findTypeLabel(spoutTypes, value)
}

export const boltTypes = [{
  value: 'STRUCTURE',
  label: '归一化处理'
}, {
  value: 'VALIDATE',
  label: '数据校验'
}, {
  value: 'TRANSFORM',
  label: '数据转换'
}, {
  value: 'ERROR',
  label: '错误处理'
}, {
  value: 'JMS',
  label: '消息驱动JMS'
}, {
  value: 'JDBC',
  label: '关系型数据库'
}]

export function boltTypeLabel(value) {
  return findTypeLabel(boltTypes, value)
}

export const jmsProtocolTypes = [{
  value: 'nio://',
  label: 'nio://'
}, {
  value: 'tcp://',
  label: 'tcp://'
}, {
  value: 'http://',
  label: 'http://'
}]

export const jdbcDrivers = [
  'com.mysql.jdbc.Driver'
]
