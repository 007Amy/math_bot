import api from './api'
import Robot from './Robot'
import GridAnimator from './GridAnimator'

class RunCompiled extends GridAnimator {
  constructor ({context}) {
    super()
    if (context) {
      this.context = context
      this.robotFrames = []
      this.$store = this.context.$store
      this.$router = this.context.$router
      this.robot = this.$store.getters.getRobot
      this.params = this.$store.getters.getStepData
      this.toolList = this.params.toolList

      console.clear()

      this._askCompiler = this._askCompiler.bind(this)
      this._processFrames = this._processFrames.bind(this)
      this._initializeStep = this._initializeStep.bind(this)

      this._askCompiler()
      setTimeout(this._processFrames, 500)
    }
  }

  /*
  * Below here for new ask compiler
  * */

  _initializeStep (frame) {
    this.$store.dispatch('updateStats', frame.stats)
    this.$store.dispatch('updateStepData', frame.stepData)
    this.$store.dispatch('updateLambdas', frame.stepData.lambdas)
    this.$store.dispatch('updateRobot', new Robot({robotFacing: frame.stepData.robotOrientation}))
  }

  _success (frame) {
    console.log('[SUCCESS]', frame)
    return this.initializeAnimation(frame, this._initializeStep)
  }

  _failure (frame) {
    console.log('[FAILURE]', frame)
    return this.initializeAnimation(frame, this._initializeStep)
  }

  _running (frame) {
    console.log('[RUNNING]', frame)
    return this.initializeAnimation(frame, this._processFrames)
  }

  async _processFrames (_) {
    const current = this.robotFrames.shift()
    const last = this.robotFrames[this.robotFrames.length - 1]

    if (this.robotFrames.length && last.programState === 'running') {
      this._askCompiler()
    }

    const run = await this[`_${current.programState}`](current)
    run(current)
  }

  _askCompiler () {
    api.compilerWebSocket.compileWs({context: this, problem: this.params.problem.encryptedProblem}, (compiled) => {
      this.robotFrames = this.robotFrames.concat(compiled.frames)
    })
  }
}

export default RunCompiled
