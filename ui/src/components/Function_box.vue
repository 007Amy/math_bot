<template>
  <div class="function-box" @click="method ? method($event, func, ind) : ''">
    <puzzle-pieces
      :piece-to-show="pieceToShow"
      :background-img="funcAndcmdImages[func.image]"
      :color="func.color"
      :func-name="func.name"
      :show-name="origin !== 'stagedFunctions' || origin !== 'editMain'"></puzzle-pieces>
  </div>
</template>

<script>
import { _ } from 'underscore'

import PuzzlePieces from './Puzzle_pieces'

export default {
  name: 'function_box',
  computed: {
    pieceToShow () {
      if (this.collection.length === 1 || this.origin === 'functions' || this.origin === 'stagedFunctions' || this.origin === 'editFunction') {
        return 'closed'
      } else if (this.ind === 0) {
        return 'start'
      } else if (this.collection.length === this.ind + 1) {
        return 'end'
      } else {
        return 'middle'
      }
    },
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
  components: {
    PuzzlePieces
  },
  props: ['func', 'ind', 'origin', 'method', 'collection', 'otherMounted']
}
</script>

<style scoped src="../css/scoped/functionBox.css"></style>
