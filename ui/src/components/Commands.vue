<template>
  <div class="commands" v-if="activeFunctionGroups !== null">

    <popover-bucket
      :content="functionAreaShowing"
    ></popover-bucket>

    <div class="command-control-button-group">
      <img class="commands-up command-control-button" @click="moveSwiper('up')" :src="permanentImages.buttons.playButton">
      <img class="commands-down command-control-button" @click="moveSwiper('down')" :src="permanentImages.buttons.playButton">
    </div>

    <div class="commands-slide">
      <draggable
        class="methods"
        :list="commands"
        :options="commandOptions"
        @start="start"
        @end="end"
      >
        <function-box
          v-for="(command, ind) in commands"
          :key="command.id"
          :func="command"
          :ind="ind"
          :collection="commands"
          :origin="'functions'"
        ></function-box>
      </draggable>
      <draggable
        class="functions"
        :list="activeFunctions"
        :options="functionOptions"
        @start="start"
        @end="end"
        @add="addToActiveFunc"
      >
        <function-box
          v-for="(func, ind) in activeFunctions"
          :key="func.created_id"
          :func="func"
          :ind="ind"
          :collection="activeFunctionGroups[1]"
          :origin="'functions'"
          :method="toggleFunctionEdit"
        ></function-box>
      </draggable>
    </div>

    <img class="open-staged dialog-button" v-if="this.currentStepData.stagedEnabled" :class="functionAreaShowing === 'addFunction' ? 'rotate-to-x' : 'rotate-to-plus'" @click="[toggleFunctionAdd(), updatePopOverBucketPointerPosition($event)]" :src="permanentImages.buttons.plusButton" />

  </div>
</template>

<script>
  import draggable from 'vuedraggable';
  import api from '../services/api';
  import buildUtils from '../services/build_function_utils';
  import renderUtils from '../services/render_utils';
  import FunctionBox from './Function_box';
  import PopoverBucket from './Popover_bucket';

  export default {
    name: 'FunctionDrop',
    mounted() {
      window.addEventListener('resize', () => {
        this.functionsPosition = 0;
        this.moveSwiper('up');
      });
    },
    computed: {
      currentStepData() {
        return this.$store.getters.getCurrentStepData;
      },
      token() {
        return this.$store.getters.getToken;
      },
      editingIndex() {
        return this.$store.getters.getEditingIndex;
      },
      editingFunction() {
        return this.$store.getters.getActiveFunctions[this.editingIndex];
      },
      functionAreaShowing() {
        return this.$store.getters.getFunctionAreaShowing;
      },
      commands() {
        return this.$store.getters.getCommands;
      },
      activeFunctions() {
        return this.$store.getters.getActiveFunctions;
      },
      colorSelected() {
        return this.$store.getters.getColorSelected;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      },
      cmdImages() {
        return this.permanentImages.cmdImages;
      },
      colorPallet() {
        return this.permanentImages.colorPallet;
      },
      colors() {
        return this.$store.getters.getColors;
      },
      funcImages() {
        return this.permanentImages.funcImages;
      },
      activeFunctionGroups() {
        return this.$store.getters.getActiveFunctionGroups;
      },
      swiperSlide() {
        return this.$store.getters.getSwiperSlide;
      },
      functionAreaShowing() {
        return this.$store.getters.getFunctionAreaShowing;
      }
    },
    data() {
      return {
        functionsPosition: 0,
        commandOptions: {
          group: {
            name: 'commands-slide',
            pull: 'clone',
            put: false
          },
          filter: '.command-name',
          dragClass: 'dragging',
          sort: false,
          ghostClass: 'ghost'
        },
        functionOptions: {
          group: {
            name: 'commands-slide',
            pull: 'clone',
            put: ["commands-staged"]
          },
          filter: '.command-name',
          dragClass: 'dragging',
          sort: false,
          ghostClass: 'ghost'
        },
        currentColor: this.colorSelected,
      };
    },
    methods: {
      findColor() {
        return this.colors[this.colorSelected].next;
      },
      applyColorConditional() {
        const color = this.findColor();
        this.$store.dispatch('colorSelected', color);
      },
      appendBuildFunction(command) {
        buildUtils.updateFunctionsOnChange({context: this, currentFunction: buildUtils.currentFunc(this), addedFunction: command, newIndex: 'length'});
      },
      toggleFunctionEdit(evt, func, ind) {
        // Updates pointer position
        this.updatePopOverBucketPointerPosition(evt);
        renderUtils.toggleFunctionEdit(this, func, ind, 'editMain');
      },
      start() {
        if (this.functionAreaShowing === 'editMain') {
          this.$store.dispatch('toggleShowMesh', true);
        }
      },
      end() {
        this.$store.dispatch('toggleShowMesh', false);
      },
      addToActiveFunc(evt) {
        const index = evt.item.getAttribute('data-function-index');

        // console.log('INDEX IN ~ ', index);

        api.activateFunction({ tokenId: this.token.token_id, stagedIndex: index, activeIndex: evt.newIndex}, lambdas => {
          // console.log('NEW LAMBDAS ~ ', lambdas)
          this.$store.dispatch('updateLambdas', {lambdas: lambdas})
          this.toggleFunctionEdit(evt, lambdas.activeFuncs[evt.newIndex], evt.newIndex)
        })
      },
      updatePopOverBucketPointerPosition(evt) {
        // updates location of popover-bucket pointer
        this.$store.dispatch('updatePointerPosition', {evt: evt});
      },
      toggleFunctionAdd() {
        const showing = this.functionAreaShowing

        if (showing === 'editMain' || showing === 'editFunction') {
          this.$store.dispatch('updateFunctionAreaShowing', 'addFunction');
          this.$store.dispatch('updateEditingIndex', null);
        } else {
          this.$store.dispatch('updateFunctionAreaShowing', 'editMain');
          this.$store.dispatch('updateEditingIndex', null);
        }
      },
      closeEditFunction() {
        this.$store.dispatch('updateFunctionAreaShowing', 'editMain');
        this.$store.dispatch('updateEditingIndex', null);
      },
      moveSwiper(direction) {
        this.closeEditFunction();
        (function (windowWidth, dis) {
          const $functions = $('.functions');
          const functionsWidth = $functions.width();
          const $functionBoxes = $functions.children();
          const $firstFunctionBox = $functionBoxes.first();
          const functionBoxMarginRight = Number($firstFunctionBox.css('margin-right').replace('px', ''));
          const functionBoxMarginBottom = Number($firstFunctionBox.css('margin-bottom').replace('px', ''))
          const functionBoxWidth = $firstFunctionBox.outerWidth() + (functionBoxMarginRight * 2);
          const functionBoxHeight = $firstFunctionBox.outerHeight() + (functionBoxMarginBottom);
          const amtPerRow = Math.floor(functionsWidth / functionBoxWidth);
          const rowCount = Math.ceil($functionBoxes.length / amtPerRow);
          const allRowsHeight = functionBoxHeight * rowCount;
          const ableToScrollDown = (dis.functionsPosition + functionBoxHeight) < allRowsHeight;

          if (direction === 'up' && dis.functionsPosition > 0) {
            dis.functionsPosition -= functionBoxHeight;
          } else if (direction === 'down' && ableToScrollDown) {
            dis.functionsPosition += functionBoxHeight;
          }

          $functions.animate({scrollTop: dis.functionsPosition + 'px'}, 300, 'linear');
        })($(window).width(), this)
      },
    },
    components: {
      draggable,
      FunctionBox,
      PopoverBucket
    },
  };
</script>

<style scoped src="../css/commands.css"></style>
