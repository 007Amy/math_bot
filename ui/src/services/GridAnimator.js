class GridAnimator {
  $store = null
  robot = null
  robotState = null

  _moveRobot () {
    return new Promise(resolve => {
      console.log('--MOVE')
      this.robot.updateRobot(this.robotState)
      setTimeout(resolve, 500)
    })
  }

  async initializeAnimation (store, frame, done) {
    this.$store = store
    this.robotState = frame.robotState
    this.robot = this.$store.getters.getRobot

    await this._moveRobot()
    return new Promise(resolve => resolve(done))
  }
}

export default GridAnimator
