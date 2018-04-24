<template>
  <div v-if="functionAreaShowing !== 'editMain'">
    <div class="popover-bucket">
      <div class="popover-bucket-content">
        <staged-functions v-if="functionAreaShowing === 'addFunction'"></staged-functions>
        <edit-function v-else></edit-function>
      </div>
      <div class="pointer-slider">
        <div class="pointer-border" :style="{'margin-left': pointerPosition + 'px'}"></div>
        <div class="pointer" :style="{'margin-left': pointerPosition + 'px'}"></div>
      </div>
    </div>
  </div>
</template>

<script>
  import StagedFunctions from './Staged_functions';
  import EditFunction from './Edit_function'

  export default {
    mounted() {
      this.ensurePointerInBounds();
    },
    updated() {
      this.$nextTick(this.ensurePointerInBounds);
    },
    computed: {
      pointerPosition() {
        return this.$store.getters.getPointerPosition;
      },
      functionAreaShowing() {
        return this.$store.getters.getFunctionAreaShowing;
      },
    },
    methods: {
      ensurePointerInBounds() {
        const $pointerSlider = $('.pointer-slider');
        const pointSliderWidth = Math.floor($pointerSlider.width());

        if (pointSliderWidth <= this.pointerPosition + 38) {
          this.$store.dispatch('updatePointerPosition', {setSpot: pointSliderWidth - 16});
        }
      }
    },
    components: {
      StagedFunctions,
      EditFunction
    }
  }
</script>

<style scoped src="../css/popoverBucket.css"></style>
