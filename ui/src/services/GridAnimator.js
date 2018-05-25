class GridAnimator {
  _robotTurn (frame) {
    return new Promise(resolve => {
      console.log('--ANIMATE TURN')
      setTimeout(resolve, 1000)
    })
  }

  _robotWalk (frame) {
    return new Promise(resolve => {
      console.log('--ANIMATE MOVE')
      setTimeout(resolve, 1000)
    })
  }

  async initializeAnimation (frame, done) {
    await this._robotTurn(frame)
    await this._robotWalk(frame)
    return new Promise(resolve => resolve(done))
  }
}

export default GridAnimator
