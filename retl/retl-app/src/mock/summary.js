export default [{
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
