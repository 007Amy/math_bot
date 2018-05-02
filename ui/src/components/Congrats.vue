<template>
  <div
    class="message-template congrats"
  >
    <!-- Need to come back to this when we create an actual starring system -->
    <div><img class="star" id="oneStar" :src="permanentImages.stars[5]"></div>
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
