<template>
  <div v-if="functionAreaShowing !== 'editMain'">
    <div class="popover-bucket">
      <div class="popover-bucket-content">
        <staged-functions v-if="functionAreaShowing === 'addFunction'"></staged-functions>
        <edit-function v-else></edit-function>
      </div>
      <div class="pointer-slider">
        <div class="pointer" :style="{'margin-left': this.pointerCoord + 'px'}">
          <div class="pointer-size pointer-border"></div>
          <div class="pointer-size pointer-body"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import StagedFunctions from './Staged_functions';
  import EditFunction from './Edit_function'

  export default {
    mounted() {
      this.updatePointerPosition();
    },
    computed: {
      editingIndex() {
        return this.$store.getters.getEditingIndex;
      },
      functionAreaShowing() {
        return this.$store.getters.getFunctionAreaShowing;
      }
    },
    updated() {
      this.updatePointerPosition();
    },
    watch: {
      editingIndex () {
        this.updatePointerPosition();
      }
    },
    data() {
      return {
        pointerCoord: 0
      }
    },
    methods: {
      updatePointerPosition () {
        const $ele = $(this.evt.target);
        const pos = $ele.position();
        let left = pos.left;
        const pointerWidth = $('.pointer').width()

        left += (($ele.width() / 2) - (pointerWidth / 2))

        this.pointerCoord = left;
      }
    },
    components: {
      StagedFunctions,
      EditFunction
    },
    props: ['evt']
  }
</script>

<style scoped src="../css/popoverBucket.css"></style>
