import {privilege} from './value-object'

export default [{
  path: /\/rest\/privileges(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    erroeMessage: '',
    'data|5-20': [privilege]
  }
}, {
  path: /\/rest\/privileges(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    erroeMessage: '',
    pagination: {
      'total': 100,
      'size': 20,
      'page': 1
    },
    'data|5-20': [privilege]
  }
}, {
  path: /\/rest\/privileges\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    data: privilege
  }
}, {
  path: /\/rest\/privileges\/new(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    data: privilege
  }
}, {
  path: /\/rest\/privileges\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'put',
  data: {
    errorCode: 0,
    data: privilege
  }
}, {
  path: /\/rest\/privileges\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'delete',
  data: {
    errorCode: 0,
    data: privilege
  }
}]
