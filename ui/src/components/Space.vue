<template>
  <div class="space">
    <img
      v-for="(level, index) in levels"
      class="planet"
      :key="level.name + '-planet'"
      :id="level.name + '-planet'"
      :class="'planet' + (index + 1)"
      :src="isLevelActive(level) ? selectedLevel === level.name ? permanentImages.planets['selected' + (index + 1)] : permanentImages.planets['active' + (index + 1)] : permanentImages.planets['inactive' + (index + 1)]"
      @click="selectLevel(level.name, firstStep)"
    >
  </div>
</template>

<script>
export default {
  name: 'space',
  props: ['levels', 'isLevelActive', 'permanentImages', 'selectLevel', 'selectedLevel', 'firstStep']
}
</script>

<style scoped src="../css/scoped/space.css"></style>
