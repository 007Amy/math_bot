import urlEncode from 'urlencode'
import Vue from 'vue'

class Ws {
  constructor () {
    this._ws = null
  }

  /*
  * checkWsCapable - tests that the browser is able to handle websockets
  * - may be able to switch user to http route instead if not - not being used yet
  * */
  _checkWsCapable (cb) {
    if ('WebSocket' in window) {
      // console.log("WebSocket is supported by your Browser!");
      cb()
    } else {
      console.log('WebSocket NOT supported by your Browser!')
    }
  }

  /*
  * getWsPath - is called to get the correct ws path from the server
  * */
  _getWsPath (tokenId, cb) {
    Vue.http.get('/api/wsPath/' + urlEncode(tokenId))
      .then(res => res.data)
      .then(path => cb(path))
      .catch(console.error)
  }

  _wsOnMessage (cb) {
    this._ws.onmessage = (msg) => {
      const compiled = JSON.parse(msg.data)
      // console.log('compiled ~>', JSON.stringify(compiled, null, 4));
      if (compiled === 'Socket connections must be of the same origin!') { console.error(compiled) } else { cb(compiled) }
    }
  }

  _send (options) {
    this._ws.send(options)
  }
}

export default Ws
