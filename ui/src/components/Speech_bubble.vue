<template>
  <div class="speech-bubble" :class="!showing ? 'hide-speech-bubble' : ''">
    <div class="bubble" v-html="html"></div>
  </div>
</template>

<script>
export default {
  name: 'speech_bubble',
  props: ['html', 'showing']
}
</script>

<style scoped>
  .speech-bubble {
    position: relative;
    height: 116px;
    width: 594px;
    opacity: 0.62;
    border: 1px solid #FFFF;
    background-color: #E8E8E8;
    box-shadow: 0 0 10px 0 rgba(0,0,0,0.5);
    border-radius: 5px;
    z-index: 100;
    visibility: visible;
    transition: opacity 1s linear;
  }

  .hide-speech-bubble {
    visibility: hidden;
    opacity: 0;
    transition: visibility 0s 1s, opacity 1s linear;
  }

  @keyframes fade {
    0%,100% { opacity: 0 }
    50% { opacity: 1 }
  }

  .speech-bubble:before, .speech-bubble:after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 0;
    height: 0;
  }

  .speech-bubble:before {
    border-top: 40px solid #FFFF;

    transform: rotate(50deg);

    border-left: 10px solid transparent;
    border-right: 10px solid transparent;

    margin-left: -20px;
    margin-bottom: -27px;
  }

  .speech-bubble:after {
    border-top: 40px solid #E8E8E8;

    border-left: 9px solid transparent;
    border-right: 9px solid transparent;

    transform: rotate(50deg);

    margin-left: -18px;
    margin-bottom: -26px;
  }
</style>
<style>
  .speech-bubble {
    color: #000000;
    font-size: 24px;
    line-height: 29px;
    text-align: left;
    padding: 13px 13px 13px 20px;
  }
  /* None scoped styles for speech images */
  .speech-bubble img {
    height: 22px;
    background-color: black;
    border-radius: 2px;
  }
  /* Medium Devices, Desktops */
  @media only screen and (max-width : 992px) {
    .speech-bubble img {
      height: 10px;
    }
  }
</style>
