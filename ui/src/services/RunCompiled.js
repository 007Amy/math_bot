import { _ } from 'underscore'
import api from './api'
import Promise from 'bluebird';

class RunCompiled {
  constructor({context, frames}) {
    this.context = context;
    this.frames = frames;
    this.$store = this.context.$store;
    this.$router = this.context.$router;
    this.robot = this.$store.getters.getRobot;
    this.stepData = this.$store.getters.getCurrentStepData;
    this.toolList= this.stepData.toolList

    if (this.frames !== undefined && this.frames.length) this.processFrames();
    else console.log('NO FRAMES');
  }

  clearRobot() {
    this.stepData.gridMap = _.map(this.stepData.gridMap, (row) => {
      return _.map(row, (square) => {
        square.robotSpot = false;
        return square;
      });
    });
  }

  animate({ele, animation}, cb) {
    const $ele = $(ele);
    const robot = this.robot;

    const animationList = {
      bumped($ele) {
        const orientation = robot.robotFacing;
        $ele.stop().effect('shake', {
          distance: 5,
          direction: orientation === '0' || orientation === '180' ? 'up' : 'left'
        }, 200, cb);
      }
    };

    if (animation !== null) animationList[animation]($ele);
    else cb()
  }

  moveRobot({x, y, orientation}, animation) {
    this.clearRobot();
    this.animate({ele: '.robot', animation: animation}, () => {
      this.robot.robotFacing = orientation;
      this.robot.whereIsRobot = [x, y];
      this.stepData.gridMap[x][y].robotSpot = true;
    });
    return 'moveRobot DONE!';
  }

  updateCells(grid) {
    _.each(grid.cells, cell => {
      const x = cell.location.x;
      const y = cell.location.y;
      if (y > 0) {
        this.stepData.gridMap[x][y].tools = _.map(cell.items, item => {
          return this.toolList[item]
        });
      }
    });
  }

  updateRobotHolding(holding) {
    this.robot.robotCarrying = holding;
  }

  win() {

    this.$store.dispatch('showCongrats');

    api.getStats({tokenId: this.$store.getters.getTokenId}, stats => {
      const stepToken = stats.levels[stats.level][stats.step];
      setTimeout(() => {
        if (stepToken.nextLevel === 'None' && stepToken.nextStep === 'None') {
          this.$router.push({path: 'profile'});
        } else {
          this.$store.dispatch('updateStats', {stats, cb: () => this.$store.dispatch('initNewGame', this.context)});
        }
        this.$store.dispatch('hideCongrats')
      }, 4000)
    })
  }

  lost(showMessage) {
    const time = showMessage ? 1000 : 100
    setTimeout(() => {
      if (showMessage) this.$store.dispatch('showTryAgain');
      setTimeout(() => {
        const currentEquation = this.$store.getters.getCurrentEquation;
        const level = this.$store.getters.getLevel;
        this.$store.dispatch('initNewGame', this.context);
        this.robot.robotCarrying = [];
      }, time);
    }, time);
  }

  updateRobotState(programState) {
    this.robot.state = programState;
  }


  processFrames() {
    if (this.robot.state === 'paused') return;
    else if (this.robot.state === 'stop') {
      this.frames = [];
      this.lost();
      return;
    }

    const current = this.frames.shift();
    const robotState = current.robotState;

    // console.log('Step Data ', this.stepData);
    // console.log('RobotState ', robotState);

    new Promise(resolve => resolve())
      .then(_ => new Promise(resolve => resolve(this.updateRobotState(current.programState))))
      .then(_ => new Promise(resolve => resolve(this.updateCells(robotState.grid))))
      .then(_ => new Promise(resolve => resolve(this.moveRobot(Object.assign(robotState.location, {orientation: robotState.orientation}), robotState.animation))))
      .then(_ => new Promise(resolve => resolve(this.updateRobotHolding(robotState.holding))))
      .done(_ => {
        if (!this.frames.length) {
          if (current.programState === 'success') {
            this.win();
          } else {
            this.lost(true);
          }
          this.robot.state = 'home';
          this.$store.dispatch('deactivateRobot');
          return;
        }
        setTimeout(() => this.processFrames(), this.$store.getters.getRobotSpeed);
      })
  }
}

export default RunCompiled;
