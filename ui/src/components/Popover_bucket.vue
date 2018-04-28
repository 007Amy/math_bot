<template>
  <div v-if="functionAreaShowing !== 'editMain'">
    <div class="popover-bucket">
      <div class="popover-bucket-content">
        <staged-functions v-if="functionAreaShowing === 'addFunction'"></staged-functions>
        <edit-function v-else></edit-function>
      </div>
      <div class="pointer-slider">
        <div class="pointer-border" :style="popoverBucket.style"></div>
        <div class="pointer" :style="popoverBucket.style"></div>
      </div>
    </div>
  </div>
</template>

<script>
  import StagedFunctions from './Staged_functions';
  import EditFunction from './Edit_function'
  import PopoverBucket from '../services/PopoverBucket';

  export default {
    data () {
      return {
        popoverBucket: new PopoverBucket(this)
      }
    },
    computed: {
      editingIndex() {
        return this.$store.getters.getEditingIndex;
      },
      functionAreaShowing() {
        const f = this.$store.getters.getFunctionAreaShowing;
        this.popoverBucket.updatePointerPosition()
        return f
      },
    },
    components: {
      StagedFunctions,
      EditFunction
    }
  }
</script>

<style scoped src="../css/popoverBucket.css"></style>
