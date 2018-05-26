<template>
  <div class="main-placeholder-container" v-if="stepData.mainMax !== 10000">
    <div id="main-placeholder">
      <div
        v-for="(_, ind) in placeholders"
        :key="'placeholder/' + ind"
        class="placeholder placeholder-short-background"
        :class="placeholders.length === 1 ? 'single-placeholder' : ''"
      ></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'main_placeholder',
  mounted () {
    this.placeHolderWidth()
  },
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
      return this.$store.getters.getStepData
    }
  },
  watch: {
    mainFull (bool) {
      const $placeHolder = $('.placeholder')
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
    placeHolderWidth () {
      const $functionDrop = $('.function-drop-drop-zone')
      const dropWidth = $functionDrop.width()
      $('.main-placeholder-container').css({width: `${dropWidth}px`})
    },
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
  }
}
</script>

<style scoped src="../css/scoped/mainPlaceholder.scss" lang="scss"></style>
