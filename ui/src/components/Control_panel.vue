<template>
  <div class="control-panel">

    <img @click="goToProfile()"
         class="return-to-profile"
         :src="permanentImages.returnToProfile"
    />

    <div class="instructions">
      <div class="instructions-filler-left"></div>
      <img @click="toggleSpeechBubble(this)" :src="permanentImages.instructionsRobot" class="instructions-robot">
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
      currentStepData() {
        return this.$store.getters.getCurrentStepData;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      },
      description() {
        return this.parseDescription(this.currentStepData.description);
      },
    },
    data() {
      return {
        speechBubbleShowing: true
      }
    },
    methods: {
      parseDescription(str) {
        if (str) {
          // Convert \n to <br />
          const makeBr = str.replace(/(?:\r\n|\r|\n)/g, '<br />');
          // Convert !!![img]src=whatever!!! to <img class="description-image" src='whatever' />
          const makeImg = makeBr.split('!!!').map(s => {
            if (s.indexOf('[img]') !== -1) {
              return `<img class="description-image" ${s.split('[img]').pop()} />`;
            } else {
              return s;
            }
          }).join(' ');

          return '<p>' + makeImg + '</p>';
        }
      },
      toggleSpeechBubble() {
        this.speechBubbleShowing = !this.speechBubbleShowing;
      },
      goToProfile() {
        this.$router.push({path: 'profile'});
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
