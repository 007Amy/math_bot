<template>
  <div class="edit-main">

    <function-drop
      :list="functions"
      :options="mainDraggableOptions"
      :change="copyCommand"
      :start="moving"
      :end="end"
      :origin="'editMain'"
    ></function-drop>

    <div class="bar noDrag">
      <img class="x button noDrag dialog-button" :src="permanentImages.buttons.trashButton"  @click="wipeFunction" data-toggle="tooltip" title="Clear main" />
      <div class="speed button dialog-button" @click="goFast" data-toggle="tooltip" title="Adjust speed"> {{ speeds[currentSpeed].display }}</div>
      <img v-if="robot.state === 'home' || robot.state === 'paused'" class="play button noDrag dialog-button" :src="permanentImages.buttons.playButton" @click="compileMain" data-toggle="tooltip" title="Run program" />
      <img v-else class="play button noDrag dialog-button" :src="permanentImages.buttons.pauseButton" @click="pause" data-toggle="tooltip" title="Pause program" />
      <img v-if="robot.state === 'running'" class="stop button noDrag dialog-button" :src="permanentImages.buttons.stopButton" @click="stop" data-toggle="tooltip" title="Stop program" />
    </div>
  </div>
</template>

<script>
  import {_} from 'underscore';
  import uid from 'uid';
  import utils from '../services/utils';
  import buildUtils from '../services/build_function_utils';
  import draggable from 'vuedraggable';
  import api from '../services/api';
  import RunCompiled from '../services/RunCompiled';
  import FunctionBox from './Function_box';
  import FunctionDrop from './Function_drop';

  export default {
    computed: {
      showMesh() {
        return this.$store.getters.getShowMesh;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      },
      funcImages() {
        return this.permanentImages.funcImages;
      },
      cmdImages() {
        return this.permanentImages.cmdImages;
      },
      funcNcmdImages() {
        return _.extend(this.funcImages, this.cmdImages);
      },
      editingIndex() {
        return this.$store.getters.getEditingIndex;
      },
      editingFunction() {
        return this.$store.getters.getMainFunction.func[this.editingIndex];
      },
      functionAreaShowing() {
        return this.$store.getters.getFunctionAreaShowing;
      },
      currentFunc() {
        return this.$store.getters.getFunctions[this.$store.getters.getCurrentFunction];
      },
      wipeFunctionShowing() {
        return this.$store.getters.getWipeFunctionShowing;
      },
      robot() {
        if(this.$store.getters.getRobot) {
          this.buttonSize = $(".commands > button").width();
        }
        return this.$store.getters.getRobot;
      },
      stepData() {
        return this.$store.getters.getCurrentStepData;
      },
      currentColor() {
        return this.$store.getters.getColorSelected;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      },
      speed() {
        return this.$store.getters.getRobotSpeed;
      },
      speedImages() {
        return { 300: this.permanentImages.buttons.turtleButton, 100: this.permanentImages.buttons.rabbitButton, 800: this.permanentImages.buttons.snailButton };
      },
      colors() {
        return this.$store.getters.getColors;
      },
    },
    data() {
      return {
        buttonSize: $('.commands > button').width() || 70,
        screenSize: $("#robot").width(),
        mainDraggableOptions: {
          group: {
            name: 'commands-slide',
            pull: true,
            put: true,
          },
          animation: 100,
          ghostClass: 'ghost',
          chosenClass: 'chosen',
          filter: '.noDrag',
          dragClass: 'dragging'
        },
        speeds: {
          800: {
            display: ".5x",
            next: 300
          },
          300: {
            display: "1x",
            next: 100
          },
          100: {
            display: "2x",
            next: 800
          },
        },
        currentSpeed: this.$store.getters.getRobotSpeed,
      }
    },
    methods: {
      stop() {
        this.robot.state = 'stop';
      },
      pause() {
        this.robot.state = 'paused';
      },
      compileMain() {
        const scripts = this.$store.getters.getMainFunction.func;
        const problem = this.stepData.problem;
        if (this.robot.state !== 'paused') {
          if (scripts.length) {
            api.compileWs({context: this, problem: problem}, (compiled) => {
              this.runCompiled = new RunCompiled({context: this, frames: compiled.frames})
            })
          } else {
            this.$store.dispatch('addMessage', 'emptyMain');
          }
        } else {
          this.robot.state = 'running';
          this.runCompiled.processFrames();
        }
      },
      copyCommand(evt) {
        if (evt.hasOwnProperty('added') && this.stepData.mainMax !== -1 && this.functions.length > this.stepData.mainMax) {
          this.$store.dispatch('addMessage', 'mainMax')
        }
        if (!evt.hasOwnProperty('removed')) {
          const command = evt.hasOwnProperty('added') ? evt.added.element : evt.moved.element;
          const ind = evt.hasOwnProperty('added') ? evt.added.newIndex : evt.moved.newIndex;
          buildUtils.updateFunctionsOnChange({context: this, currentFunction: buildUtils.currentFunc(this), addedFunction: command, newIndex: ind, override: evt.hasOwnProperty('moved')});
        }
      },
      toggleFunctionEdit(func, ind) {
        if (func.name) {
          utils.toggleFunctionEdit(this, func, ind, "editMain");
        }
      },
      goFast() {
        this.currentSpeed = this.speeds[this.currentSpeed].next;
        this.$store.dispatch('changeRobotSpeed', this.currentSpeed);
      },
      uID() {
        return uid(7);
      },
      scrollEm(evt, dir) {
        utils.scroll(evt, dir, 'function-drop');
      },
      wipeFunction() {
        this.$store.dispatch('clearCurrentFunction');
        buildUtils.updateFunctionsOnChange({context: this, currentFunction: buildUtils.currentFunc(this), addedFunction: null, newIndex: null, override: true});
      },
      moving() {
        this.$store.dispatch('updateTrashVisible', true);
        this.$store.dispatch('toggleShowMesh', true);
      },
      end() {
        this.$store.dispatch('toggleShowMesh', false);
        this.$store.dispatch('updateTrashVisible', false);
      }
    },
    props: ['functions'],
    components: {
      draggable,
      FunctionBox,
      FunctionDrop
    }
  }
</script>

<style scoped src="../css/editMain.css"></style>
