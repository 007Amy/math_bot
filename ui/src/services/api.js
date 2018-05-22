import Vue from 'vue'
import vueResource from 'vue-resource'
import urlEncode from 'urlencode'

import CompilerWebSocket from './CompilerSocket'

Vue.use(vueResource)

export default {

  compilerWebSocket: null,

  getUserToken ({tokenId}, cb) {
    Vue.http.post('/api/token', JSON.stringify({token_id: tokenId}))
      .then(res => res.body)
      .then(token => {
        // console.log('GET TOKEN ~ ', token);
        this.compilerWebSocket = new CompilerWebSocket()
        cb(token)
      })
      .catch(console.error)
  },

  insertTokenForTesting () {
    const mToken = require('./mutated_token.json')
    Vue.http.post('/api/token/test', mToken)
      .then(res => res.body)
      .then(console.log)
      .catch(console.error)
  },

  /*
  * activateFunction moves function token from stagedFunction list to activeFunction list
  * @param tokenId = JWT token_id
  * @param stagedIndex = previous index of func token
  * @param activeIndex = new index of func token
  * @response.body = lambdas property of JWT
  * */
  activateFunction ({tokenId, stagedIndex, activeIndex}, cb) {
    Vue.http.get(`/api/token/activateFunction/${urlEncode(tokenId)}/${stagedIndex}/${activeIndex}`)
      .then(res => cb(res.body))
      .catch(console.error)
  },
  /*
  * putToken replaces JWT with updated JWT
  * !!This call is deprecated but may be useful at times. Updates entire JWT so is able to max server request!!
  * @param token = entire JWT
  * @response.body = lambdas property of JWT
  * */
  putToken (token, cb) {
    Vue.http.put('/api/token', token)
      .then(res => cb(res.body))
      .catch(console.error)
  },
  /*
  * putFunc updates the current function in the database
  * @param tokenId = JWT token_id
  * @param funcToken = current token
  * @response.body = lambdas property of JWT
  * */
  putFunc ({tokenId, funcToken, override}, cb) {
    Vue.http.put('/api/token/editLambdas', {tokenId, funcToken, override})
      .then(res => {
        cb(res.body)
      })
      .catch(console.error)
  },
  /*
  * statsWin updates JWT stats when the user wins.
  * @param tokenId = JWT token_id
  * @response.body = stats property of JWT
  * */
  statsWin ({tokenId}, cb) {
    Vue.http.put('/api/stats/win/' + urlEncode(tokenId), {})
      .then(res => cb(res))
      .catch(console.error)
  },
  /*
  * switchLevel switches current level in the JWT
  * @param tokenId = JWT token_id
  * @param level = level to switch to
  * @param equation = level step to switch to
  * @response.body = state property of JWT
  * */
  switchLevel ({tokenId, level, step}, cb) {
    Vue.http.get('/api/stats/switch/' + urlEncode(tokenId) + '/' + level + '/' + step)
      .then(res => cb(res))
      .catch(console.error)
  },

  getStats ({tokenId}, cb) {
    Vue.http.get('/api/stats/' + tokenId)
      .then(res => res.body)
      .then(cb)
      .catch(console.error)
  },

  /*
  * getStep gets game logic for a step
  * @param level = level
  * @param currentEquation = step
  * @response.body = grid, problem, tool tokens
  * */
  getStep ({tokenId, level, step}, cb) {
    Vue.http.get('/api/levels/getStep/' + level + '/' + step, {params: {tokenId: tokenId}})
      .then(res => cb(res))
      .catch(console.error)
  }
}
