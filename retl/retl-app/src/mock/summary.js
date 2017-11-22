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
  path: /\/api\/v1\/cluster\/summary(\?userCode=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    data: {
      "stormVersion": "1.1.1",
      "supervisors|1-3": 2,
      "topologies|1-5": 3,
      "slotsTotal|50-100": 10,
      "slotsUsed|2-50": 4,
      "slotsFree|2-10": 6,
      "executorsTotal|20-30": 28,
      "tasksTotal|20-30": 28,
      "schedulerDisplayResource": true,
      "totalMem|4096-8192": 4096.0,
      "totalCpu|20-400": 400.0,
      "availMem|0-4096": 1024.0,
      "availCpu|0-200": 200.0,
      "memAssignedPercentUtil|0-100": 75.0,
      "cpuAssignedPercentUtil|0-100": 37.5
    }
  }
}, {
  path: /\/api\/v1\/topology\/summary(\?userCode=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    data: {
      topologies: [{
        'name': '@title',
        'tasksTotal|1-10': 5,
        'assignedCpu|5-50': 30,
        'assignedTotalMem|512-2048': 1024
      }, {
        'name': '@title',
        'tasksTotal|1-10': 5,
        'assignedCpu|5-50': 30,
        'assignedTotalMem|512-2048': 1024
      }, {
        'name': '@title',
        'tasksTotal|1-10': 5,
        'assignedCpu|5-50': 30,
        'assignedTotalMem|512-2048': 1024
      }, {
        'name': '@title',
        'tasksTotal|1-10': 5,
        'assignedCpu|5-50': 30,
        'assignedTotalMem|512-2048': 1024
      }]
    }
  }
}, {
  path: /\/rest\/retl-statistic(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    data: {
      'total': [{name: '济南市', 'value|1000-5000': 3000}, {name: '青岛市', 'value|1000-5000': 3000}],
      'error': [{name: '济南市', 'value|5-50': 30}, {name: '青岛市', 'value|3-20': 13}]
    }
  }
}]
