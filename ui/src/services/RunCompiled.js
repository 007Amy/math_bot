import api from './api'
import Robot from './RobotState'
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
      this._showBridgeScreen = this._showBridgeScreen.bind(this)

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
    this.$store.dispatch('updateRobot', new Robot(frame.stepData.initialRobotState))
  }

  _toggleBridge = (which, bool) => this.$store.dispatch(`toggle${which}`, bool)

  _showBridgeScreen (frame) {
    return new Promise(resolve => {
      if (frame.programState === 'failure') this._toggleBridge('TryAgain', true)
      else this._toggleBridge('Congrats', true)
      setTimeout(() => {
        this._toggleBridge('Congrats', false)
        this._toggleBridge('TryAgain', false)
        resolve()
      }, 3000)
    })
  }

  _success (frame) {
    return this.initializeAnimation(this.$store, frame, async () => {
      await this._showBridgeScreen(frame)
      this._initializeStep(frame)
    })
  }

  _failure (frame) {
    return this.initializeAnimation(this.$store, frame, async () => {
      await this._showBridgeScreen(frame)
      this._initializeStep(frame)
    })
  }

  _running (frame) {
    return this.initializeAnimation(this.$store, frame, this._processFrames)
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
