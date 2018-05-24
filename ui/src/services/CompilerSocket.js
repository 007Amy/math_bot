import Ws from './Ws'
import urlEncode from 'urlencode'

class CompilerSocket extends Ws {
  constructor () {
    super()
    this._compilerTake = 4
  }

  /*
  * openCompilerWs - opens socket connection for compiler
  * */
  _openCompilerWs ({tokenId}, cb) {
    this._checkWsCapable(() => {
      this._getWsPath(urlEncode(tokenId), path => {
        this._ws = new WebSocket(path)
        // console.log(path)
        // console.log(this.compilerWs)
        this._ws.onopen = () => {
          console.log('COMPILER WS OPEN')
          if (cb) cb()
        }
        this._ws.onerror = (err) => { console.error('COMPILER WS FAILED', err) }
        this._ws.onclose = () => { console.log('COMPILER WS CLOSED') }
      })
    })
  }

  /*
  * compilerWs - call to compiler for compilation
  * @param context - Vue instance context (this)
  * @param problem - Encrypted problem for step
  * */
  compileWs ({context, problem}, cb) {
    const tokenId = context.$store.getters.getToken.token_id
    // console.log('problem ~>', problem);
    // console.log('tokenId ~>', tokenId);
    if (this._ws === null || this._ws.readyState !== 1) { // if socket closed open new connection
      this._openCompilerWs({tokenId: tokenId}, () => { // open connection
        this._wsOnMessage(cb) // make connection on message the callback
        this._compilerSend(problem, false)
      })
    } else { // else just update this connections on message method
      this._wsOnMessage(cb) // make connection on message the callback
      this._compilerSend(problem, false)
    }
  }

  stopProgram () {
    this._compilerSend('', true)
  }

  _compilerSend (problem, halt) {
    this._send(JSON.stringify({steps: this._compilerTake, problem: problem, halt: halt}))
  }
}

export default CompilerSocket
