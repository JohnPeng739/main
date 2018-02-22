import {family} from './value-object'

export default [{
  path: /\/rest\/family(\?\w=\w(&\w=\w)*)?/,
  type: 'get',
  data: {
    errorCode: 0,
    'data|1': [family, null]
  }
}]
