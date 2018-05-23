import api from './api'
import Robot from './Robot'
import InitFocus from './InitFocus'

class StepData {
  constructor (context) {
    this.$store = context.$store
    this.tokenId = this.$store.getters.getTokenId
    this.level = this.$store.getters.getLevel
    this.step = this.$store.getters.getStep

    this.params = null

    this._toggleSplashScreen(true)
    this._getStep()
  }

  /*
  * Tools must be reversed for rendering
  * */
  _reverseTools (gridMap) {
    return gridMap.map(row => {
      return row.map(cell => {
        cell.tools = cell.tools.reverse()
        return cell
      })
    })
  }

  _toggleSplashScreen = bool => this.$store.dispatch('updateSplashScreenShowing', bool)

  _toggleCongrats = bool => this.$store.dispatch('toggleCongrats', bool)

  _toggleTryAgain = bool => this.$store.dispatch('toggleTryAgain', bool)

  _updateRobot = robot => this.$store.dispatch('updateRobot', robot)

  _runInitFocus = () => new InitFocus(this.params).init()

  /*
  * Gets step data from server
  * */
  _getStep () {
    api.getStep({tokenId: this.tokenId, level: this.level, step: this.step}, res => {
      console.log(`${this.level}/${this.step}:\n`, res.body)
      this.params = res.body
      // We must reverse the order of the tools for rendering
      this.params.gridMap = this._reverseTools(this.params.gridMap)
      // Control bridge screens
      this._toggleCongrats(false)
      this._toggleTryAgain(false)
      // Reset robot in the state
      this._updateRobot(new Robot({orientation: this.params.robotOrientation}))
      // Update lambdas to the correct lambdas for this level/step
      this.$store.dispatch('updateLambdas', {lambdas: this.params.lambdas})
      // Run init focus
      this._runInitFocus()
      this._toggleSplashScreen(false)
    })
  }
}

export default StepData
