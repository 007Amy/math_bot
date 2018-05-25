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

  _updateGrid () {
    this.robotState.grid.cells.forEach(cell => {
      this.grid[cell.location.x][cell.location.y].tools = cell.items.map(name => this.toolList[name])
    })
  }

  _moveRobot () {
    return new Promise(resolve => {
      this.robot.updateRobot(this.robotState)
      this._updateGrid()
      setTimeout(resolve, this.robotSpeed.speed)
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

    await this._moveRobot()
    await this._updateGrid()
    return new Promise(resolve => resolve(done))
  }
}

export default GridAnimator
