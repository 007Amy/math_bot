<template>
  <div class="edit-main">

    <function-drop
      :list="mainFunctionFunc"
      :options="mainDraggableOptions"
      :change="copyCommand"
      :start="moving"
      :end="end"
      :origin="'editMain'"
    ></function-drop>

    <div class="bar noDrag" v-if="Object.keys(robot).length">
      <main-placeholder></main-placeholder>
      <img class="trash noDrag dialog-button" :src="permanentImages.buttons.trashButton"  @click="wipeFunction" data-toggle="tooltip" title="Clear main" />
      <div class="speed dialog-button" @click="adjustSpeed" data-toggle="tooltip" title="Adjust speed"> {{ robotSpeedDisplay }}</div>
      <img class="play noDrag dialog-button" v-if="robot.state === 'home' || robot.state === 'paused'" :src="permanentImages.buttons.playButton" @click="compileMain" data-toggle="tooltip" title="Run program" />
      <img v-else class="play noDrag dialog-button" :src="permanentImages.buttons.pauseButton" @click="pause" data-toggle="tooltip" title="Pause program" />
      <img v-if="robot.state === 'running'" class="stop button noDrag dialog-button" :src="permanentImages.buttons.stopButton" @click="stop" data-toggle="tooltip" title="Stop program" />
    </div>
  </div>
</template>

<script>
import {_} from 'underscore'
import uid from 'uid'
import utils from '../services/utils'
import buildUtils from '../services/build_function_utils'
import draggable from 'vuedraggable'
import RunCompiled from '../services/RunCompiled'
import FunctionBox from './Function_box'
import FunctionDrop from './Function_drop'
import MainPlaceholder from './Main_placeholder'

export default {
  computed: {
    mainFunctionFunc () {
      const mainToken = this.$store.getters.getMainFunction
      return mainToken === null ? [] : mainToken.func
    },
    showMesh () {
      return this.$store.getters.getShowMesh
    },
    permanentImages () {
      return this.$store.getters.getPermanentImages
    },
    funcImages () {
      return this.permanentImages.funcImages
    },
    cmdImages () {
      return this.permanentImages.cmdImages
    },
    funcNcmdImages () {
      return _.extend(this.funcImages, this.cmdImages)
    },
    editingIndex () {
      return this.$store.getters.getEditingIndex
    },
    editingFunction () {
      return this.$store.getters.getMainFunction.func[this.editingIndex]
    },
    functionAreaShowing () {
      return this.$store.getters.getFunctionAreaShowing
    },
    currentFunc () {
      return this.$store.getters.getFunctions[this.$store.getters.getCurrentFunction]
    },
    wipeFunctionShowing () {
      return this.$store.getters.getWipeFunctionShowing
    },
    robot () {
      return this.$store.getters.getRobot
    },
    robotSpeedDisplay () {
      return this.robot.getSpeed().display
    },
    stepData () {
      return this.$store.getters.getCurrentStepData
    },
    currentColor () {
      return this.$store.getters.getColorSelected
    },
    colors () {
      return this.$store.getters.getColors
    }
  },
  data () {
    return {
      buttonSize: $('.commands > button').width() || 70,
      screenSize: $('#robot').width(),
      mainDraggableOptions: {
        group: {
          name: 'commands-slide',
          pull: true,
          put: true
        },
        animation: 100,
        ghostClass: 'ghost',
        chosenClass: 'chosen',
        filter: '.noDrag',
        dragClass: 'dragging'
      }
    }
  },
  methods: {
    stop () {
      this.robot.state = 'stop'
    },
    pause () {
      this.robot.state = 'paused'
    },
    togglePut (bool) {
      this.mainDraggableOptions.group.put = bool
    },
    compileMain () {
      const scripts = this.$store.getters.getMainFunction.func

      // Ensure draggable put is true for next level
      this.togglePut(true)

      if (this.robot.state !== 'paused') {
        if (scripts.length) {
          // Delete all existing messages
          this.$store.dispatch('deleteMessages')
          // Compile robot
          this.runCompiled = new RunCompiled({context: this})
        } else {
          const messageBuilder = {
            type: 'warn',
            msg: 'Main is empty',
            handlers () {
              const $bar = $('.bar')

              return {
                runBeforeAppend () {
                  $bar.addClass('red-bar')
                },
                runOnDelete () {
                  $bar.removeClass('red-bar')
                }
              }
            }
          }

          this.$store.dispatch('addMessage', messageBuilder)
        }
      } else {
        this.robot.state = 'running'
        this.runCompiled.processFrames()
      }
    },
    copyCommand (evt) {
      if (!evt.hasOwnProperty('removed')) {
        const command = evt.hasOwnProperty('added') ? evt.added.element : evt.moved.element
        const ind = evt.hasOwnProperty('added') ? evt.added.newIndex : evt.moved.newIndex
        buildUtils.updateFunctionsOnChange({context: this, currentFunction: buildUtils.currentFunc(this), addedFunction: command, newIndex: ind, override: evt.hasOwnProperty('moved')})
      }

      // Main func length is equal to main max, make put false (preventing unneeded api call)
      this.togglePut(this.mainFunctionFunc.length < this.stepData.mainMax)
    },
    toggleFunctionEdit (func, ind) {
      if (func.name) {
        utils.toggleFunctionEdit(this, func, ind, 'editMain')
      }
    },
    uID () {
      return uid(7)
    },
    scrollEm (evt, dir) {
      utils.scroll(evt, dir, 'function-drop')
    },
    wipeFunction () {
      this.$store.dispatch('clearCurrentFunction')
      buildUtils.updateFunctionsOnChange({context: this, currentFunction: buildUtils.currentFunc(this), addedFunction: null, newIndex: null, override: true})
    },
    adjustSpeed () {
      this.$store.dispatch('changeRobotSpeed')
    },
    moving () {
      this.$store.dispatch('updateTrashVisible', true)
      this.$store.dispatch('toggleShowMesh', true)
    },
    end () {
      this.$store.dispatch('toggleShowMesh', false)
      this.$store.dispatch('updateTrashVisible', false)
    }
  },
  components: {
    draggable,
    FunctionBox,
    FunctionDrop,
    MainPlaceholder
  }
}
</script>

<style scoped src="../css/scoped/editMain.css"></style>
