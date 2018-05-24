import api from './api'

class RunCompiled {
  constructor ({context}) {
    if (context) {
      this.context = context
      this.robotFrames = []
      this.$store = this.context.$store
      this.$router = this.context.$router
      this.robot = this.$store.getters.getRobot
      this.params = this.$store.getters.getStepData
      this.toolList = this.params.toolList

      this._askCompiler = this._askCompiler.bind(this)
      this._askCompiler()
      this._processFrames = this._processFrames.bind(this)
      setTimeout(this._processFrames, 500)
    }
  }

  /*
  * Below here for new ask compiler
  * */

  _success (frame) {
    return new Promise(resolve => {
      resolve(() => console.log('[SUCCESS]', frame))
    })
  }

  _failure (frame) {
    return new Promise(resolve => {
      resolve(() => console.log('[FAILURE]', frame))
    })
  }

  _running (frame) {
    return new Promise(resolve => {
      console.log('[RUNNING]', frame)
      setTimeout(() => resolve(this._processFrames), 1000)
    })
  }

  async _processFrames () {
    const current = this.robotFrames.shift()
    const last = this.robotFrames[this.robotFrames.length - 1]

    if (this.robotFrames.length && last.programState === 'running') {
      this._askCompiler()
    }

    const run = await this[`_${current.programState}`](current)
    run()
  }

  _askCompiler () {
    api.compilerWebSocket.compileWs({context: this, problem: this.params.problem.encryptedProblem}, (compiled) => {
      this.robotFrames = this.robotFrames.concat(compiled.frames)
    })
  }
}

export default RunCompiled
