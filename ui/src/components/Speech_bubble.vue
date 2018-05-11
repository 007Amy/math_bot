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

<style scoped src="../css/scoped/speechBubble.css"></style>
<style src="../css/global/speechBubble.css"></style>
