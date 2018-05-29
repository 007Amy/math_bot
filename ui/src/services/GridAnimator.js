class GridAnimator {
  constructor () {
    this._updateGrid = this._updateGrid.bind(this)
  }

  $store = null
  robot = null
  grid = null
  robotState = null
  toolList = null
  robotSpeed = null
  animations = {
    bumped: 'robot-shake'
  }
  $robot = null

  _animate () {
    const animation = this.animations[this.robotState.animation] || 'walk'
    this.$robot.addClass(animation)
  }

  _removeAnimations () {
    Object.keys(this.animations).forEach(key => {
      this.$robot.removeClass(this.animations[key])
    })
  }

  _updateGrid () {
    this.robotState.grid.cells.forEach(cell => {
      this.grid[cell.location.x][cell.location.y].tools = cell.items.map(name => this.toolList[name])
    })
  }

  _moveRobot () {
    return new Promise(resolve => {
      this._animate()
      this.robot.updateRobot(this.robotState)
      this._updateGrid()
      setTimeout(() => {
        this._removeAnimations()
        resolve()
      }, this.robotSpeed.speed)
    })
  }

  async initializeAnimation (store, frame, done) {
    this.$store = store
    this.frame = frame
    this.robotState = frame.robotState
    this.robot = this.$store.getters.getRobot
    this.grid = this.$store.getters.getGrid
    this.toolList = this.$store.getters.getStepData.toolList
    this.robotSpeed = this.robot.robotSpeed
    this.$robot = $('.robot')

    await this._moveRobot()
    await this._updateGrid()
    return new Promise(resolve => {
      resolve(done)
    })
  }
}

export default GridAnimator
