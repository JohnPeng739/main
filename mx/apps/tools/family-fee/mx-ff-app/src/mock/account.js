import {loginHistory} from './value-object'

export default [{
  path: /\/rest\/signUp(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    data: loginHistory
  }
}, {
  path: /\/rest\/login(\?\w=\w(&\w=\w)*)?/,
  type: 'post',
  data: {
    errorCode: 0,
    data: loginHistory
  }
}, {
  path: /\/rest\/logout\/\w(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    data: loginHistory
  }
}]
