let localServerInfo = {
  'machineName': '@word',
  'machineIp': '@ip',
  'zookeeper': {
    'cluster|1': true,
    'serverNo|+1': 1,
    'dataDir': '@title',
    'servers|1': [['server1'], ['server1', 'server2'], ['server1', 'server2', 'server3'], ['server1', 'server2', 'server3', 'server4'], ['server1', 'server2', 'server3', 'server4', 'server5']]
  },
  'storm': {
    'services|1': [['nimbus'], ['nimbus', 'ui'], ['nimbus', 'ui', 'supervisor'], ['nimbus', 'ui', 'supervisor', 'logviewer']],
    'zookeepers|1': [['storm1'], ['storm1', 'storm2'], ['storm1', 'storm2', 'storm3']],
    'nimbuses|1': [['storm1'], ['storm1', 'storm2'], ['storm1', 'storm2', 'storm3']],
    'dataDir': '@title',
    'slots|10-50': 20,
    'startPort': 6700
  }
}

export default [{
  path: /\/rest\/servers(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    'data|1-10': [localServerInfo]
  }
}, {
  path :/\/rest\/servers(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    'pagination': {total: 60, size: 20, page: 1},
    'data|1-20': [localServerInfo]
  }
}, {
  path: /\/rest\/server\/service\?cmd=\w&service=\w&machineIp=\w(&\w=\w)*/,
  type: 'get',
  data: {
    'data|1': true
  }
}, {
  path: /\/rest\/server/,
  type: 'get',
  data: {
    data: localServerInfo
  }
}, {
  path: /\/rest\/server\?userCode=\w/,
  type: 'post',
  data: {
    data: localServerInfo
  }
}, {
  path: /\/rest\/server\/status\?machineIp=\w/,
  type: 'get',
  data: {
    data: {
      'zookeeper': {'enabled|1': true, 'active|1': true},
      'storm': {'enabled|1': true, 'active|1': true}
    }
  }
}, {
  path: '/api/v1/cluster/summary',
  type: 'get',
  data: {
    data: {
      "stormVersion": "1.1.1",
      "supervisors": 2,
      "topologies": 3,
      "slotsTotal": 10,
      "slotsUsed": 4,
      "slotsFree": 6,
      "executorsTotal": 28,
      "tasksTotal": 28,
      "schedulerDisplayResource": true,
      "totalMem": 4096.0,
      "totalCpu": 400.0,
      "availMem": 1024.0,
      "availCpu": 250.0,
      "memAssignedPercentUtil": 75.0,
      "cpuAssignedPercentUtil": 37.5
    }
  }
}]
