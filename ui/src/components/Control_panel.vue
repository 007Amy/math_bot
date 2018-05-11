<template>
  <div class="control-panel">

    <img @click="goToProfile()"
         class="return-to-profile"
         :src="permanentImages.returnToProfile"
         data-toggle="tooltip" title="Return to profile"
    />

    <div class="instructions" :style="congratsShowing || tryAgainShowing ? {opacity: 0} : {}">
      <div class="instructions-filler-left"></div>
      <div class="instructions-robot-container">
        <img @click="toggleSpeechBubble(this)" :src="permanentImages.instructionsRobot" class="instructions-robot" data-toggle="tooltip" title="Toggle speech bubble">
      </div>
        <!--<div class="speech-container" :class="description !== '' && speechBubbleShowing ? 'fade-in-speech' : 'fade-out-speech'">-->
        <!--<img class="speech-bubble" :src="permanentImages.speechBubble" :alt="description">-->
        <!--<div class="speech" v-html="description"></div>-->
      <!--</div>-->
      <speech-bubble :html="description" :showing="speechBubbleShowing"></speech-bubble>
      <div class="instructions-filler-right"></div>
    </div>
  </div>
</template>

<script>
import SpeechBubble from './Speech_bubble'

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
  },
  components: {
    SpeechBubble
  }
}
</script>

<style scoped src="../css/scoped/controlPanel.css"></style>
