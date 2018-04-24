<template>
  <div
    class="message-template congrats"
  >
    <div><img class="star" id="oneStar" :src="permanentImages.stars[stats.levels[stats.level][stats.step].stars]"></div>
    <p>NICE!!!</p>
  </div>
</template>

<script>
  export default {
    computed: {
      stats() {
        return this.$store.getters.getStats;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      }
    }
  };
</script>

<style scoped src="../css/congrats.css">
</style>
