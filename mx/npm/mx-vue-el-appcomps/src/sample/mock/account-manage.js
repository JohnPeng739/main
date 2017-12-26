import {log, account, loginHistory} from './value-object'

export default [{
  path: /\/rest\/logs(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    'data|5-20': [log]
  }
}, {
  path: /\/rest\/logs(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    erroeMessage: '',
    pagination: {
      'total': 100,
      'size': 20,
      'page': 1
    },
    'data|5-20': [log]
  }
}, {
  path: /\/rest\/loginHistories(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    'data|5-20': [loginHistory]
  }
}, {
  path: /\/rest\/loginHistories(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    erroeMessage: '',
    pagination: {
      'total': 100,
      'size': 20,
      'page': 1
    },
    'data|5-20': [loginHistory]
  }
}, {
  path: /\/rest\/accounts(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    'data|5-20': [account]
  }
}, {
  path: /\/rest\/accounts(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    erroeMessage: '',
    pagination: {
      'total': 100,
      'size': 20,
      'page': 1
    },
    'data|5-20': [account]
  }
}, {
  path: /\/rest\/accounts\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    data: account
  }
}, {
  path: /\/rest\/accounts\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'delete',
  data: {
    errorCode: 0,
    data: account
  }
}, {
  path: /\/rest\/accounts\/\w\/password\/change(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    data: account
  }
}, {
  path: /\/rest\/accounts\/login(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    data: loginHistory
  }
}, {
  path: /\/rest\/accounts\/\w\/logout(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    data: loginHistory
  }
}]
