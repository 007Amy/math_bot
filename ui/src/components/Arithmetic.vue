<template>
  <div class="arithmetic">
    <div class="space">
      <img
        v-for="(level, index) in levels"
        class="planet"
        :key="level.name + '-planet'"
        :id="level.name + '-planet'"
        :class="'planet' + (index + 1)"
        :src="isLevelActive(level) ? selectedLevel === level.name ? permanentImages.planets['selected' + (index + 1)] : permanentImages.planets['active' + (index + 1)] : permanentImages.planets['inactive' + (index + 1)]"
        @click="selectLevel(level.name, firstStep)"
      >
    </div>

    <div class="blue">
      <div class="level"> {{ parseCamelCase(level) }}</div>
      <div class="info">
        <div
          v-for="(step, value) in steps"
          class="step-info"
          :class="step.active ? '' : 'step-disabled'"
          @click="step.active ? goToRobot(level, step.name) : ''"
          :key="step + ':' + value"
        >
          <div class="step-info-container">
            <div class="step-text">{{ parseCamelCase(step.name) }}</div>
            <img class="step-image" :src="step.active ? permanentImages.stars[step.stars] : permanentImages.lock">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'
import utils from '../services/utils'

export default {
  mounted () {
    this.selectedLevel = this.stats.level
    // Anytime this component is mounted remove level and step state from local storage.
    localStorage.removeItem('LEVEL_STEP')
  },
  data () {
    return {
      selectedLevel: ''
    }
  },
  computed: {
    currentUserName () {
      let currentUser = this.$store.getters.getCurrentUser
      if (currentUser === null) {
        return 'Profile'
      } else {
        return currentUser.given_name || currentUser.nickname
      }
    },
    level () {
      return this.$store.getters.getLevel
    },
    permanentImages () {
      return this.$store.getters.getPermanentImages
    },
    levels () {
      return this.$store.getters.getLevels
    },
    stats () {
      return this.$store.getters.getStats
    },
    step () {
      return this.$store.getters.getStep
    },
    steps () {
      return this.$store.getters.getSteps
    },
    tokenId () {
      return this.$store.getters.getToken.token_id
    },
    firstStep () {
      return this.steps[0].name
    }
  },
  methods: {
    parseCamelCase: utils.parseCamelCase,
    isLevelActive (l) {
      const level = l.level
      return Object.keys(level).reduce((bool, step) => {
        if (level[step].active === true) {
          bool = true
        }
        return bool
      }, false)
    },
    logout () {
      this.$store.dispatch('removeNonces')
    },
    selectLevel (level, step) {
      api.switchLevel({tokenId: this.tokenId, level: level, step: step}, (res) => {
        this.$store.dispatch('updateStats', {stats: res.body})
        this.selectedLevel = level
      })
    },
    selectProfileView (loc) {
      this.$store.dispatch('updateProfileView', loc)
    },
    goToRobot (level, step) {
      api.switchLevel({tokenId: this.tokenId, level: level, step: step}, (res) => {
        this.$store.dispatch('updateStats', {stats: res.body,
          cb: () => {
            this.$router.push({path: '/robot'})
          }})
      })
    }
  }
}
</script>

<style scoped src="../css/scoped/arithmetic.css"></style>
