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
  import FunctionBox from './Function_box';
  import { _ } from 'underscore';

  export default {
    name: 'main_placeholder',
    mounted () {
      this.placeholderContainer$ = $('.placeholder-container')
    },
    computed: {
      mainFunctionFunc() {
        const mainToken = this.$store.getters.getMainFunction;
        return mainToken === null ? [] : mainToken.func;
      },
      mainFull () {
        return this.mainFunctionFunc.length === this.stepData.mainMax;
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
        if (bool) this.placeholderFull();
        else this.placeholderShort();
      }
    },
    data () {
      return {
        list: [],
        placeholderContainer$: null
      }
    },
    methods: {
      message (type, msg, runOnDelete) {
        this.$store.dispatch('addMessage', {type: type, msg: msg, runOnDelete: runOnDelete})
      },
      placeholderFull () {
        const $play = $('.play')

        $play.addClass('play-border')

        this.message('success', 'Main is full, check your program then press play', () => {
          $play.removeClass('play-border')
        })

        this.placeholderContainer$
          .addClass('placeholder-full-background')
      },
      placeholderShort () {
        this.placeholderContainer$
          .removeClass('placeholder-full-background')
      }
    },
    components: {
      FunctionBox
    }
  }
</script>

<style scoped src="../css/mainPlaceholder.css"></style>
