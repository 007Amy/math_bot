import api from './api'
import Robot from './Robot'
import InitFocus from './InitFocus'

class StepData {
  constructor (tokenId, {level, step}) {
    this.tokenId = tokenId
    this.level = level
    this.step = step

    this.robot = null
    this.params = null
    this.lambdas = null
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

  _runInitFocus = () => new InitFocus(this.params).init()

  /*
  * Gets step data from server
  * */
  getStep (updateLambdas) {
    api.getStep({tokenId: this.tokenId, level: this.level, step: this.step}, res => {
      // console.log(`${this.level}/${this.step}:\n`, res.body)
      this.params = res.body
      // We must reverse the order of the tools for rendering
      this.params.gridMap = this._reverseTools(this.params.gridMap)
      // Reset robot in the state
      this.robot = new Robot({robotFacing: this.params.robotOrientation})
      // store lambdas so state can replace the ones in the user token
      // WIP: going to seprated lambdas from user token
      this.lambdas = this.params.lambdas
      updateLambdas(this.lambdas)
    })
  }
}

export default StepData
