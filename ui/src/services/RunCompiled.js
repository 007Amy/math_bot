import { _ } from 'underscore'
import api from './api'
import Promise from 'bluebird'

class RunCompiled {
  constructor ({context}) {
    if (context) {
      this.context = context
      this.frames = frames
      this.$store = this.context.$store
      this.$router = this.context.$router
      this.robot = this.$store.getters.getRobot
      this.params = this.$store.getters.getStepData
      this.toolList = this.params.toolList

      this._initiateCompile = this._initiateCompile.bind(this)
      this._initiateCompile()
    }
  }

  clearRobot () {
    this.params.gridMap = _.map(this.params.gridMap, (row) => {
      return _.map(row, (square) => {
        square.robotSpot = false
        return square
      })
    })
  }

  animate ({ele, animation}, cb) {
    const $ele = $(ele)
    const robot = this.robot

    const animationList = {
      bumped ($ele) {
        const orientation = robot.robotFacing
        $ele.stop().effect('shake', {
          distance: 5,
          direction: orientation === '0' || orientation === '180' ? 'up' : 'left'
        }, 200, cb)
      }
    }

    if (animation !== null) animationList[animation]($ele)
    else cb()
  }

  moveRobot ({x, y, orientation}, animation) {
    this.clearRobot()
    this.animate({ele: '.robot', animation: animation}, () => {
      this.robot.robotFacing = orientation
      this.robot.whereIsRobot = [x, y]
      this.params.gridMap[x][y].robotSpot = true
    })
    return 'moveRobot DONE!'
  }

  updateCells (grid) {
    _.each(grid.cells, cell => {
      const x = cell.location.x
      const y = cell.location.y
      if (y > 0) {
        this.params.gridMap[x][y].tools = _.map(cell.items, item => {
          return this.toolList[item]
        }).reverse()
      }
    })
  }

  updateRobotHolding (holding) {
    this.robot.robotCarrying = holding
  }

  _toggleCongrats = bool => this.$store.dispatch('toggleCongrats', bool)

  win () {
    api.getStats({tokenId: this.$store.getters.getTokenId}, stats => {
      const stepToken = stats.levels[stats.level][stats.step]

      this.$store.dispatch('updateStats', {stats,
        cb: () => {
          this._toggleCongrats(true)

          setTimeout(() => {
            if (this.params.step === stepToken.name) {
              this.$router.push({path: 'profile'})
            } else {
              this.$store.dispatch('updateStepData')
            }
            this._toggleCongrats(false)
          }, 4000)
        }
      })
    })
  }

  _toggleTryAgain = bool => this.$store.dispatch('toggleTryAgain', bool)

  lost (showMessage) {
    const time = showMessage ? 1000 : 100
    setTimeout(() => {
      if (showMessage) this._toggleTryAgain(true)
      setTimeout(() => {
        this.$store.dispatch('updateStepData')
        this.robot.robotCarrying = []
      }, time)
    }, time)
  }

  updateRobotState (programState) {
    this.robot.state = programState
  }

  _haltSocket () {
    api.compilerWebSocket.stopProgram()
  }

  processFrames () {
    if (this.robot.state === 'paused') return
    else if (this.robot.state === 'stop') {
      this.frames = []
      this.lost()
      this._haltSocket()
      return
    }

    const current = this.frames.shift()
    const robotState = current.robotState

    // console.log('Step Data ', this.params);
    // console.log('RobotState ', robotState);

    new Promise(resolve => resolve())
      .then(_ => new Promise(resolve => resolve(this.updateRobotState(current.programState))))
      .then(_ => new Promise(resolve => resolve(this.updateCells(robotState.grid))))
      .then(_ => new Promise(resolve => resolve(this.moveRobot(Object.assign(robotState.location, {orientation: robotState.orientation}), robotState.animation))))
      .then(_ => new Promise(resolve => resolve(this.updateRobotHolding(robotState.holding))))
      .done(_ => {
        const robotSpeed = this.$store.getters.getRobotSpeed

        if (!this.frames.length) {
          console.log('[ProgramState, last frame] ', current.programState)
          if (current.programState === 'success') {
            this.win()
            return
          } else if (current.programState === 'failure') {
            this.lost(true)
            return
          } else {
            this._initiateCompile()
          }
          this.robot.state = 'home'
          this.$store.dispatch('deactivateRobot')
        } else {
          setTimeout(() => this.processFrames(), robotSpeed)
        }
        setTimeout(() => this.processFrames(), this.robot.getSpeed().speed)
      })
  }

  _initiateCompile () {
    api.compilerWebSocket.compileWs({context: this, problem: this.params.problem.encryptedProblem}, (compiled) => {
      const frames = compiled.frames
      console.log('[FRAMES] ', frames)
      if (frames !== undefined && frames.length) {
        this.frames = frames
        this.processFrames()
      } else {
        console.log('NO FRAMES')
      }
    })
  }
}

export default RunCompiled
