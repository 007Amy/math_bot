<template>
  <div class="main-placeholder" v-if="stepData.mainMax !== 10000">
    <div class="placeholder-container placeholder-short-background">
      <div
        v-for="(_, ind) in placeholders"
        :key="'placeholder/' + ind"
        class="placeholder"
        :class="placeholders.length === 1 ? 'single-placeholder' : ''"
      ></div>
    </div>
  </div>
</template>

<script>
import FunctionBox from './Function_box'

export default {
  name: 'main_placeholder',
  computed: {
    mainFunctionFunc () {
      const mainToken = this.$store.getters.getMainFunction
      return mainToken === null ? [] : mainToken.func
    },
    mainFull () {
      return this.mainFunctionFunc.length === this.stepData.mainMax
    },
    placeholders () {
      return new Array(this.stepData.mainMax)
    },
    stepData () {
      return this.$store.getters.getCurrentStepData
    }
  },
  watch: {
    mainFull (bool) {
      const $placeHolder = $('.placeholder-container')
      if (bool) this.placeholderFull($placeHolder)
      else this.placeholderShort($placeHolder)
    }
  },
  data () {
    return {
      list: []
    }
  },
  methods: {
    placeholderFull ($placeholder) {
      const messageBuilder = {
        type: 'success',
        msg: 'Press play',
        handlers () {
          const $play = $('.play')

          return {
            runBeforeAppend () {
              $play.addClass('pulse')
            },
            runOnDelete () {
              $play.removeClass('pulse')
            }
          }
        }
      }

      this.$store.dispatch('addMessage', messageBuilder)

      $placeholder
        .addClass('placeholder-full-background')
    },
    placeholderShort ($placeholder) {
      $placeholder
        .removeClass('placeholder-full-background')
    }
  },
  components: {
    FunctionBox
  }
}
</script>

<style scoped src="../css/mainPlaceholder.css"></style>
