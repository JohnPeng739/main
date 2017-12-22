import {accredit} from './value-object'

export default [{
  path: /\/rest\/accredits(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    erroeMessage: '',
    'data|5-20': [accredit]
  }
}, {
  path: /\/rest\/accredits(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    erroeMessage: '',
    pagination: {
      'total': 100,
      'size': 20,
      'page': 1
    },
    'data|5-20': [accredit]
  }
}, {
  path: /\/rest\/accredits\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    data: accredit
  }
}, {
  path: /\/rest\/accredits\/new(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    data: accredit
  }
}, {
  path: /\/rest\/accredits\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'delete',
  data: {
    errorCode: 0,
    data: accredit
  }
}]
