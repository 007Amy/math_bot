<template>
  <div class="profile-container">
    <div class="profile" data-aos="fade-in">
      <div class="secretTools">
        <button @click="unlock()">Unlock</button>
        <button @click="reset()">Reset</button>
      </div>
      <splash-screen v-if="splashScreenShowing"></splash-screen>
      <arithmetic v-else-if="!splashScreenShowing && profileView === 'Arithmetic' && stats !== undefined"></arithmetic>
      <div class="bottomButtons">
        <img :src="permanentImages.instructionsRobot">
        <div class="mathbotText">MathBot</div>
        <div @click="$router.push({path: '/marketing'})" class="text" id="about">About</div>
        <div v-if="auth !== null" @click="auth.logout()" class="text" id="signOut">Sign Out</div>
      </div>
    </div>
  </div>
</template>

<script>
import SplashScreen from './Splash_screen'
import Arithmetic from './Arithmetic'
import AuthService from '../services/AuthService'

export default {
  mounted () {
    this.auth = new AuthService(this)
    this.auth.createLock()
    this.auth.init()
  },
  data () {
    return {
      auth: null
    }
  },
  computed: {
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
    unlock () {
      const tokenId = this.$store.getters.getToken.token_id
      this.$http.get('/api/stats/unlock/' + tokenId)
        .then(res => {
          this.$store.dispatch('updateStats', {stats: res.body})
        })
        .catch(err => console.error(err.message))
    },
    reset () {
      const tokenId = this.$store.getters.getToken.token_id
      this.$http.get('/api/stats/reset/' + tokenId)
        .then(res => {
          this.$store.dispatch('updateStats', {stats: res.body})
        })
        .catch(err => console.error(err.message))
    }
  },
  components: {
    Arithmetic,
    SplashScreen
  }
}
</script>

<style scoped src="../css/scoped/profile.css"></style>
