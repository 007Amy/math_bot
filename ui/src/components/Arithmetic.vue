<template>
  <div class="arithmetic">
    <!--<button @click="addMutatedData">Add Mutated bs</button>-->
    <space :levels="levels" :is-level-active="isLevelActive" :permanent-images="permanentImages" :select-level="selectLevel" :selected-level="selectedLevel" :first-step="firstStep"></space>
    <steps :level="level" :steps="steps" :permanent-images="permanentImages" :go-to-robot="goToRobot"></steps>
  </div>
</template>

<script>
import api from '../services/api'
import utils from '../services/utils'
import Steps from './Steps'
import Space from './Space'

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
    addMutatedData () {
      api.insertTokenForTesting()
    },
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
  },
  components: {
    Steps,
    Space
  }
}
</script>

<style scoped src="../css/scoped/arithmetic.css"></style>
