import { robotImages } from '../assets/assets'

class Robot {
  constructor ({context, state, holding, orientation, location, robotSpeed}) {
    this.context = context
    this.$store = this.context.$store
    this.state = state || 'home'
    this.robotCarrying = holding || []
    this.robotFacing = orientation || 0
    this.robotLocation = location || {x: 2, y: 2}
    this.robotSpeed = robotSpeed || this._robotSpeeds[0]

    this.trash = []
  }

  _robotDirections = {
    '0': robotImages.robotUp,
    '90': robotImages.robotRight,
    '180': robotImages.robotDown,
    '270': robotImages.robotLeft
  }

  _robotSpeedIndex = 0

  _robotSpeeds = [
    {
      display: '1x',
      speed: 400
    },
    {
      display: '2x',
      speed: 200
    },
    {
      display: '3x',
      speed: 100
    }
  ]

  _updateSelf () {
    this.$store.dispatch('updateRobot', this)
  }

  setState (state) {
    this.state = state
    this._updateSelf()
  }

  adjustSpeed () {
    if (this._robotSpeedIndex === this._robotSpeeds.length - 1) this._robotSpeedIndex = 0
    else this._robotSpeedIndex++
    this.robotSpeed = this._robotSpeeds[this._robotSpeedIndex]
  }

  /*
  * While robot is animating use this function
  * 'initialRobotState' and 'robotState' can be desctructured into this app
  * */
  updateRobot (robotState) {
    this.robotLocation = robotState.location
    this.robotCarrying = robotState.holding
    this.robotFacing = robotState.orientation
  }
}

export default Robot
