<template>
    <div
      :id="func.created_id"
      class="function-box-button"
      :class="origin + '-box-button'"
      :style="origin !== 'editMain' ? {
              'background-image': 'url(' + funcAndcmdImages[func.image] + ')',
              'border-color': func.color ? colors[func.color].hex : '',
              'outline-color': func.color ? colors[func.color].hex : '',
              'outline-width': '5px'} : {'background-image': 'url(' + puzzlePiece(func, ind, collection) + ')'}"
      @click="method ? method($event, func, ind) : ''"
    >
      <div
        v-if="origin === 'functions'"
        class="command-name"
        :id="'funcTextBox' + func.created_id"
        :style="{ 'background-color': func.color ? colors[func.color].hex : '' }"
      >
        <div class="funcText" :id="'funcText' + func.created_id"> <p v-if="func.name">{{ func.name }}</p> <p v-else style="opacity: 0">"-"</p> </div>
      </div>
      <img v-else-if="origin === 'editMain'" class="function-image" :src="funcAndcmdImages[func.image]">
    </div>
</template>

<script>
import { _ } from 'underscore'

export default {
  name: 'function_box',
  computed: {
    permanentImages () {
      return this.$store.getters.getPermanentImages
    },
    commandImages () {
      return this.permanentImages.cmdImages
    },
    funcImages () {
      return this.permanentImages.funcImages
    },
    funcAndcmdImages () {
      return _.extend(this.funcImages, this.commandImages)
    },
    colors () {
      return this.$store.getters.getColors
    }
  },
  methods: {
    puzzlePiece (func, ind, collection) {
      return ind === 0 ? this.permanentImages.puzzlePieces.start[func.color] : ind === collection.length - 1 ? this.permanentImages.puzzlePieces.end[func.color] : this.permanentImages.puzzlePieces.middle[func.color]
    }
  },
  props: ['func', 'ind', 'origin', 'method', 'collection', 'otherMounted']
}
</script>

<style scoped src="../css/scoped/functionBox.css"></style>
