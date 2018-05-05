<template>
  <div class="main-placeholder" v-if="stepData.mainMax !== 10000">
    <div class="placeholder-container placeholder-short-background">
      <div
        v-for="(_, ind) in placeholders"
        :key="'placeholder/' + ind"
        class="placeholder"
        :style="placeholders.length === 1 ? {width: '78px'} : {}"
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

        this.message('success', 'Main is full, press play!', () => {
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

<style scoped>
  .main-placeholder {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .placeholder-container {
    display: flex;
    position: relative;
    left: 0.35em;
    border-radius: 2px;
  }

  .placeholder-short-background {
    background-color: rgba(184, 233, 134, 0.2);
  }

  .placeholder-info-border {
    border: 1px solid rgb(135, 206, 250);
  }

  .placeholder-info-background {
    background-color: rgba(135, 206, 250, 0.2);
  }

  .placeholder-full-background {
    background-color: rgba(184, 233, 134, 0.8);
  }

  .placeholder {
    height: 78px;
    width: 72px;
  }
</style>
