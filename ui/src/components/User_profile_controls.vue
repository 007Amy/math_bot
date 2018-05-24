<template>
  <div class="user-profile-controls">
    <div class="user-profile-controls-image-container">
      <img :src="permanentImages.instructionsRobot">
    </div>
    <div class="user-profile-controls-text-container">
      <mathbot-header :font-size="headerFont" :line-height="headerFont + 2"></mathbot-header>
      <div class="user-profile-controls-text-footer">
        <div class="text about" @click="gotoMarketing">About</div>
        <div class="text sign-out" v-if="auth !== null" @click="auth.logout()">Sign Out</div>
      </div>
    </div>
  </div>
</template>

<script>
import MathbotHeader from './Mathbot_header'
export default {
  name: 'user_profile_controls',
  computed: {
    auth () {
      return this.$store.getters.getAuth
    },
    windowWidth () {
      return window.innerWidth
    },
    headerFont () {
      if (this.windowWidth >= 992) {
        return 60
      } else {
        return 30
      }
    }
  },
  methods: {
    gotoMarketing () {
      this.$router.push({path: '/about'})
    }
  },
  components: {
    MathbotHeader
  },
  props: ['permanentImages']
}
</script>

<style scoped src="../css/scoped/userProfileControls.css"></style>
