<template>
  <div class="steps">
    <div class="steps-header-container">
      <div class="steps-header planet-header">{{`${planetName}:`}}</div>
      <div class="steps-header level-header"> {{ parseCamelCase(level) }}</div>
    </div>
    <div class="steps-navigator-container">
      <div
        v-for="(step, value) in steps"
        class="steps-navigator-item"
        :class="step.active ? 'step-active' : 'step-disabled'"
        @click="step.active ? goToRobot(level, step.name) : ''"
        :key="step + ':' + value"
      >
        <div class="step-info-text-container">
          <div class="step-info-text">{{ parseCamelCase(step.name) }}</div>
        </div>
        <div class="step-info-image-container">
          <img
            class="step-image"
            :class="step.active ? 'step-image-stars' : 'step-image-lock'"
            :src="step.active ? permanentImages.stars[step.stars] : permanentImages.lock">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import utils from '../services/utils'

export default {
  name: 'steps',
  computed: {
    planetName () {
      const planets = {
        BasicProgramming: 'Planet 1'
      }
      return planets[this.level]
    }
  },
  methods: {
    parseCamelCase: utils.parseCamelCase
  },
  props: ['level', 'steps', 'permanentImages', 'goToRobot']
}
</script>

<style scoped src="../css/scoped/steps.css"></style>
