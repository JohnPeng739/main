import {user, account} from './value-object'

export default [{
  path: /\/rest\/users(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    erroeMessage: '',
    'data|5-20': [user]
  }
}, {
  path: /\/rest\/users(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    erroeMessage: '',
    pagination: {
      'total': 100,
      'size': 20,
      'page': 1
    },
    'data|5-20': [user]
  }
}, {
  path: /\/rest\/users\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    data: user
  }
}, {
  path: /\/rest\/users\/new(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    data: user
  }
}, {
  path: /\/rest\/users\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'put',
  data: {
    errorCode: 0,
    data: user
  }
}, {
  path: /\/rest\/users\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'delete',
  data: {
    errorCode: 0,
    data: user
  }
}, {
  path: /\/rest\/users\/\w\/allocate(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    data: account
  }
}]
