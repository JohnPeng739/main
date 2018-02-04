import express from 'express'

const router = express.Router()

let app = express()
router.use((req, res, next) => {
  Object.setPrototypeOf(req, app.request)
  Object.setPrototypeOf(res, app.response)
  req.res = res
  res.req = req
  next()
})

router.post('login', (req, res) => {
  let data = req.body
  if (data && data.id && data.code && data.name) {
    req.session.authUser = req.body
    return res.json({errorCode: 0, data: true})
  } else {
    return res.json({errorCode: 401, errorMessage: 'Bad credentials.'})
  }
})

router.get('/logout', (req, res) => {
  delete req.session.authUser
  res.json({errorCode: 0, data: true})
})

export default {
  path: '/api',
  handler: router
}
