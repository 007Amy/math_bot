import { robotImages } from '../assets/assets'

class Robot {
  constructor ({state, robotCarrying, robotFacing}) {
    this.state = state || 'home'
    this.robotCarrying = robotCarrying || []
    this.robotFacing = robotFacing || 0

    this.trash = []
  }

  _robotDirections = {
    '0': robotImages.robotUp,
    '90': robotImages.robotRight,
    '180': robotImages.robotDown,
    '270': robotImages.robotLeft
  }

  _robotSpeed = 0

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
    if (this._robotSpeed === this._robotSpeeds.length - 1) this._robotSpeed = 0
    else this._robotSpeed++
  }

  getSpeed () {
    return this._robotSpeeds[this._robotSpeed]
  }
}

export default Robot
