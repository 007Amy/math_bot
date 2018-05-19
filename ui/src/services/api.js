import Vue from 'vue'
import vueResource from 'vue-resource'
import urlEncode from 'urlencode'

Vue.use(vueResource)

export default {

  compilerWs: null,

  checkWsCapable (cb) {
    if ('WebSocket' in window) {
      // console.log("WebSocket is supported by your Browser!");
      cb()
    } else {
      console.log('WebSocket NOT supported by your Browser!')
    }
  },

  getWsPath (tokenId, cb) {
    Vue.http.get('/api/wsPath/' + urlEncode(tokenId))
      .then(res => cb(res.data))
      .catch(console.error)
  },

  openCompilerWs ({tokenId}, cb) {
    this.checkWsCapable(() => {
      this.getWsPath(urlEncode(tokenId), path => {
        const COMPILER_SOCKET = path
        this.compilerWs = new WebSocket(COMPILER_SOCKET)
        // console.log(path)
        // console.log(this.compilerWs)
        this.compilerWs.onopen = () => {
          console.log('COMPILER WS OPEN')
          if (cb) cb()
        }
        this.compilerWs.onerror = (err) => { console.error('COMPILER WS FAILED', err) }
        this.compilerWs.onclose = () => { console.log('COMPILER WS CLOSED') }
      })
    })
  },

  getUserToken ({tokenId}, cb) {
    Vue.http.post('/api/token', JSON.stringify({token_id: tokenId}))
      .then(res => res.body)
      .then(token => {
        // console.log('GET TOKEN ~ ', token)
        this.openCompilerWs({tokenId: token.token_id})
        cb(token)
      })
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

  wsOnMessage (cb) {
    this.compilerWs.onmessage = (msg) => {
      const compiled = JSON.parse(msg.data)
      // console.log('compiled ~>', JSON.stringify(compiled, null, 4));
      if (compiled === 'Socket connections must be of the same origin!') { console.error(compiled) } else { cb(compiled) }
    }
  },

  compileWs ({context, problem}, cb) {
    const tokenId = context.$store.getters.getToken.token_id
    // console.log('problem ~>', problem);
    // console.log('tokenId ~>', tokenId);
    if (this.compilerWs.readyState !== 1) {
      this.openCompilerWs({tokenId: tokenId}, () => {
        this.wsOnMessage(cb)
        this.compilerWs.send(JSON.stringify({steps: 600, program: {problem: problem}, halt: false}))
      })
    } else {
      this.wsOnMessage(cb)
      this.compilerWs.send(JSON.stringify({steps: 600, program: {problem: problem}, halt: false}))
    }
  },

  stopProgram ({context}) {
    this.compilerWs.send(JSON.stringify({steps: 0, program: {problem: ''}, halt: true}))
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
