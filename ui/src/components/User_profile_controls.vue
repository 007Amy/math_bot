<template>
  <div class="user-profile-controls">
    <div class="user-profile-controls-image-container">
      <img :src="permanentImages.instructionsRobot">
    </div>
    <div class="user-profile-controls-text-container">
      <div class="mathbot-header">MATH<span class="line">_</span>BOT</div>
      <div class="user-profile-controls-text-footer">
        <div class="text about" @click="gotoMarketing">About</div>
        <div class="text sign-out" v-if="auth !== null" @click="auth.logout()">Sign Out</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'user_profile_controls',
  methods: {
    gotoMarketing () {
      this.$router.push({path: '/about'})
    }
  },
  props: ['auth', 'permanentImages']
}
</script>

<style scoped src="../css/scoped/userProfileControls.css"></style>
