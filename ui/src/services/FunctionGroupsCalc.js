import utils from './utils';
import {_} from 'underscore';

class FunctionGroupsCalc {
  constructor ({eleRobot, eleFunctions, functions}) {
    this.activeFunctions = functions;
    this.functionGroups = [];
  }

  calcIterations(functionsPerGroup) {
    return Math.ceil(this.activeFunctions.length / functionsPerGroup);
  }


  updateFunctionGroups({iterations, functionsPerGroup}) {
    if (iterations !== Infinity) {
      for (let i = 0; i < iterations; i++) {
        let group = this.activeFunctions.slice(functionsPerGroup * i, (functionsPerGroup * (i + 1)));
        if (group.length) {
          this.functionGroups.push(group);
        }
      }
    }
  }

  /*
    calculateFunctionGroups calculates how many function buttons can fit in the available space in .function-store
  **/
  calculateFunctionGroups() {
    const functionsPerGroup = 16;
    const iterations = this.calcIterations(functionsPerGroup);
    this.updateFunctionGroups({iterations: iterations, functionsPerGroup: functionsPerGroup});
  }
}

export default FunctionGroupsCalc;
