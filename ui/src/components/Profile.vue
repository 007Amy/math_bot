<template>
  <div class="profile-container">
    <splash-screen v-if="!auth.authenticated"></splash-screen>
    <div v-else class="profile" data-aos="fade-in">
      <div class="secretTools">
        <!--<button @click="mutateMe">MUTATE ME!</button>-->
        <button @click="unlock()">Unlock</button>
        <button @click="reset()">Reset</button>
      </div>
      <arithmetic></arithmetic>
      <user-profile-controls :permanent-images="permanentImages"></user-profile-controls>
    </div>
  </div>
</template>

<script>
import SplashScreen from './Splash_screen'
import Arithmetic from './Arithmetic'
import UserProfileControls from './User_profile_controls'

export default {
  mounted () {
    this.$store.dispatch('updateStepData', {})
    this.$store.dispatch('updateRobot', {})
  },
  computed: {
    auth () {
      return this.$store.getters.getAuth
    },
    splashScreenShowing () {
      return this.$store.getters.getSplashScreenShowing
    },
    profileView () {
      return this.$store.getters.getProfileView
    },
    permanentImages () {
      return this.$store.getters.getPermanentImages
    },
    stats () {
      return this.$store.getters.getStats
    }
  },
  methods: {
    mutateMe () {
      const mutatedJson = require('../services/mutated_token.json')
      this.$http.post('/api/token/test', mutatedJson)
        .then(res => res.body)
        .then(console.log)
        .catch(console.error)
    },
    unlock () {
      const tokenId = this.$store.getters.getToken.token_id
      this.$http.get('/api/stats/unlock/' + tokenId)
        .then(res => {
          this.$store.dispatch('updateStats', res.body)
        })
        .catch(err => console.error(err.message))
    },
    reset () {
      const tokenId = this.$store.getters.getToken.token_id
      this.$http.get('/api/stats/reset/' + tokenId)
        .then(res => {
          this.$store.dispatch('updateStats', res.body)
        })
        .catch(err => console.error(err.message))
    }
  },
  components: {
    Arithmetic,
    SplashScreen,
    UserProfileControls
  }
}
</script>

<style scoped src="../css/scoped/profile.scss" lang="scss"></style>
