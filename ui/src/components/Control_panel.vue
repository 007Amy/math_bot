<template>
  <div class="control-panel">

    <img @click="goToProfile()"
         class="return-to-profile"
         :src="permanentImages.returnToProfile"
         data-toggle="tooltip" title="Return to profile"
    />

    <div class="instructions" :style="congratsShowing || tryAgainShowing ? {opacity: 0} : {}">
      <div class="instructions-filler-left"></div>
      <img @click="toggleSpeechBubble(this)" :src="permanentImages.instructionsRobot" class="instructions-robot" data-toggle="tooltip" title="Toggle speech bubble">
      <div class="speech-container" :class="description !== '' && speechBubbleShowing ? 'fade-in-speech' : 'fade-out-speech'">
        <img class="speech-bubble" :src="permanentImages.speechBubble" :alt="description">
        <div class="speech" v-html="description"></div>
      </div>
      <div class="instructions-filler-right"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'control-panel',
  computed: {
    tryAgainShowing () {
      return this.$store.getters.getTryAgainShowing
    },
    congratsShowing () {
      return this.$store.getters.getCongratsShowing
    },
    currentStepData () {
      return this.$store.getters.getCurrentStepData
    },
    permanentImages () {
      return this.$store.getters.getPermanentImages
    },
    description () {
      return this.currentStepData.description
    }
  },
  data () {
    return {
      speechBubbleShowing: true
    }
  },
  methods: {
    toggleSpeechBubble () {
      this.speechBubbleShowing = !this.speechBubbleShowing
    },
    goToProfile () {
      this.$store.dispatch('deleteMessages')
      this.$router.push({path: 'profile'})
    }
  }
}
</script>

<style scoped src="../css/controlPanel.css"></style>
<style>
  /* None scoped styles for speech images */
  .speech img {
    height: 22px;
    background-color: black;
    border-radius: 2px;
  }
  /* Medium Devices, Desktops */
  @media only screen and (max-width : 992px) {
    .speech img {
      height: 10px;
    }
  }

  /* Small Devices */
  @media only screen and (max-width : 667px) {

  }

  /* Extra Small Devices, Phones */
  @media only screen and (max-width : 480px) {

  }

  /* Custom, iPhone 5 Retina */
  @media only screen and (max-width : 320px) {

  }

</style>
