import { robotImages } from '../assets/assets'

class Robot {
  constructor ({state, holding, orientation, location, robotSpeed}) {
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
    this.constructor(Object.assign({robotSpeed: this.robotSpeed, state: this.state}, robotState))
  }
}

export default Robot
