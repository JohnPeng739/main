import {department} from './value-object'

export default [{
  path: /\/rest\/departments(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    erroeMessage: '',
    'data|5-20': [department]
  }
}, {
  path: /\/rest\/departments(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    erroeMessage: '',
    pagination: {
      'total': 100,
      'size': 20,
      'page': 1
    },
    'data|5-20': [department]
  }
}, {
  path: /\/rest\/departments\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    data: department
  }
}, {
  path: /\/rest\/departments\/new(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    data: department
  }
}, {
  path: /\/rest\/departments\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'put',
  data: {
    errorCode: 0,
    data: department
  }
}]
