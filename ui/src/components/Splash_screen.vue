<template>
  <div id="splash-screen">
    <img class="black-hole" :src="permanentImages.blackHole" alt="">
  </div>
</template>

<script>
export default {
  name: 'splash_screen',
  computed: {
    permanentImages () {
      return this.$store.getters.getPermanentImages
    }
  }
}
</script>

<style scoped src="../css/splashScreen.css"></style>
