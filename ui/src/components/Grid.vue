<template>
  <div class="total-grid">
    <transition
      name="custom-classes-transition"
      enter-active-class="animated rotateIn"
      leave-active-class="hidden"
    >
      <congrats v-if="congratsShowing"></congrats>
      <tryagain v-else-if="tryAgainShowing"></tryagain>
      <div v-else-if="gridMap" class="grid">
        <div
          class="grid-row animated"
          :class="messageShowing ? 'grid-opacity' : ''"
          v-for="(row, rInd) in gridMap"
          :key="'row:' + rInd"
        >
          <div
            class="grid-space animated"
            v-for="(space, sInd) in row"
            :class="isItForceField(space.name) && space.active ? 'active-force-field' : isItForceField(space.name) && !space.active ? 'inactive-force-field' : ''"
            :level="isItForceField(space.name) ? sInd / 5 : null"
            :key="'space:' + rInd + ':' + sInd"
            :style="space.name === 'empty space floor' ? {'background-image': 'url(' + permanentImages.floor + ')'} : space.name === 'wall' ? {'background-image': 'url(' + permanentImages.wall + ')', 'background-color': 'rgba(0, 0, 0, 0.2)'} : {'background-image': 'url(' + permanentImages.floor + ')'}"
          >
            <img
              v-if="space.name === 'final answer' || space.name === 'hold button' "
              class="drop-point glyphicon"
              :src="permanentImages.blackHole">
            <img
              class="tool animated zoomIn"
              v-for="(tool, tInd) in space.tools"
              :key="'tool:' + tInd + ':' + rInd + ':' + sInd"
              :src="toolImages[tool.image]"
            >
            <img
              class="robot animated"
              v-if="space.robotSpot"
              :key="'ROBOT'"
              :src="robot._robotDirections[robotOrientation]"
            >
          </div>
        </div>
      </div>
      <splash-screen v-else></splash-screen>
    </transition>
  </div>
</template>

<script>
  import {_} from 'underscore';
  import assets from '../assets/assets';
  import Congrats from './Congrats';
  import Tryagain from './Try_again';
  import SplashScreen from './Splash_screen';

  export default {
    computed: {
      currentStepData() {
        return this.$store.getters.getCurrentStepData;
      },
      level() {
        return this.currentStepData.level;
      },
      step() {
        return this.currentStepData.step;
      },
      gridMap() {
        return this.currentStepData.gridMap;
      },
      robotOrientation() {
        return this.robot.robotFacing;
      },
      toolImages() {
        return assets.tools;
      },
      robotDeactivated() {
        return this.$store.getters.getRobotDeactivated;
      },
      robot() {
        return this.$store.getters.getRobot;
      },
      congratsShowing() {
        return this.$store.getters.getCongratsShowing;
      },
      tryAgainShowing() {
        return this.$store.getters.getTryAgainShowing;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      },
      messageShowing() {
        if (this.congratsShowing) {
          return true;
        } else return !!this.tryAgainShowing;
      },
      currentPaused() {
        return this.$store.getters.getPaused;
      },
      mode() {
        return this.$store.getters.getMode;
      }
    },
    data() {
      return {
        runCompiled: {},
        showSpeech: true,
      }
    },
    methods: {
      pause() {
        this.robot.state = 'paused';
      },
      isItForceField(name) {
        return name === 'force field';
      },
      isItExpression(name) {
        return name === 'full expression';
      },
      beforeEnter: function (el) {
        el.style.opacity = 0;
      }
    },
    components: {
      Congrats,
      Tryagain,
      SplashScreen
    },
  };
</script>

<style scoped src="../css/grid.css"></style>
