<template>
  <div class="total-grid">
    <transition
      name="custom-classes-transition"
      enter-active-class="animated zoomIn"
      leave-active-class="hidden"
    >
      <congrats v-if="congratsShowing"></congrats>
      <tryagain v-else-if="tryAgainShowing"></tryagain>
      <div v-else-if="gridMap" class="grid">
        <div
          class="grid-row animated"
          v-for="(row, rInd) in gridMap"
          :key="'row:' + rInd"
        >
          <div
            class="grid-space animated"
            v-for="(space, sInd) in row"
            :class="'grid-space-' + space.name.replace(/ /g, '-')"
            :key="'space:' + rInd + ':' + sInd"
          >
            <span v-if="space.name === 'final answer'"
                  class="problem single-digit-problem">{{singleDigitProblem(problem)}}</span>
            <img
              v-if="space.name === 'final answer'"
              class="portal glyphicon"
              :src="permanentImages.blackHole" />
            <img
              v-if="space.tools.length"
              class="tool animated zoomIn"
              v-for="(tool, tInd) in space.tools"
              :key="'tool:' + tInd + ':' + rInd + ':' + sInd"
              :src="toolImages[tool.image]" />
            <img
              class="robot animated"
              v-if="robot.robotLocation.x === rInd && robot.robotLocation.y === sInd"
              :key="'ROBOT'"
              :src="robot._robotDirections[robotOrientation]" />
          </div>
        </div>
      </div>
    </transition>
    <robotcarrying></robotcarrying>
  </div>
</template>

<script>
import assets from '../assets/assets'
import Congrats from './Congrats'
import Tryagain from './Try_again'
import Robotcarrying from './Robot_carrying'

export default {
  computed: {
    problem () {
      return this.currentStepData.problem.problem
    },
    currentStepData () {
      return this.$store.getters.getStepData
    },
    level () {
      return this.currentStepData.level
    },
    step () {
      return this.currentStepData.step
    },
    gridMap () {
      console.log(this.currentStepData.gridMap)
      return this.currentStepData.gridMap
    },
    robotOrientation () {
      return this.robot.robotFacing
    },
    toolImages () {
      return assets.tools
    },
    robotDeactivated () {
      return this.$store.getters.getRobotDeactivated
    },
    robot () {
      return this.$store.getters.getRobot
    },
    congratsShowing () {
      return this.$store.getters.getCongratsShowing
    },
    tryAgainShowing () {
      return this.$store.getters.getTryAgainShowing
    },
    permanentImages () {
      return this.$store.getters.getPermanentImages
    },
    messageShowing () {
      if (this.congratsShowing) {
        return true
      } else return !!this.tryAgainShowing
    },
    currentPaused () {
      return this.$store.getters.getPaused
    },
    mode () {
      return this.$store.getters.getMode
    },
    stepData () {
      return this.$store.getters.getStepData
    }
  },
  data () {
    return {
      runCompiled: {},
      showSpeech: true
    }
  },
  methods: {
    singleDigitProblem (problem) {
      const pNumber = Number(problem)
      if (!isNaN(pNumber) && pNumber > 0) {
        return problem
      }
    },
    pause () {
      this.robot.state = 'paused'
    },
    convertToImgName (spaceName) {
      switch (spaceName) {
        case 'wall':
          return spaceName
        default:
          return 'floor'
      }
    }
  },
  components: {
    Congrats,
    Tryagain,
    Robotcarrying
  }
}
</script>

<style scoped src="../css/scoped/grid.scss" lang="scss"></style>
