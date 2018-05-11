<template>
  <div class="commands" v-if="commands !== null && activeFunctions !== null">

    <popover-bucket
      v-if="commandEvt !== null"
      :evt="commandEvt"
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
          v-on:click.native="notEditableMessage"
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
          :collection="activeFunctions"
          :origin="'functions'"
          :method="toggleFunctionEdit"
          v-on:click.native="editingFunctionMessage(func)"
        ></function-box>
      </draggable>
    </div>

    <img
      id="open-staged"
      class="dialog-button"
      v-if="this.currentStepData.stagedEnabled"
      :class="functionAreaShowing === 'addFunction' ? 'rotate-to-x' : 'rotate-to-plus'"
      @click="toggleFunctionAdd"
      :src="permanentImages.buttons.plusButton"
      data-toggle="tooltip" :title="functionAreaShowing === 'addFunction' ? 'Close' : 'Open'" />

  </div>
</template>

<script>
import draggable from 'vuedraggable'
import api from '../services/api'
import FunctionBox from './Function_box'
import PopoverBucket from './Popover_bucket'

export default {
  name: 'FunctionDrop',
  mounted () {
    window.addEventListener('resize', () => {
      if (window.location.hash === '#/robot') {
        this.functionsPosition = 0
        this.moveSwiper('up')
      }
    })
  },
  computed: {
    currentStepData () {
      return this.$store.getters.getCurrentStepData
    },
    token () {
      return this.$store.getters.getToken
    },
    editingIndex () {
      return this.$store.getters.getEditingIndex
    },
    editingFunction () {
      return this.$store.getters.getActiveFunctions[this.editingIndex]
    },
    functionAreaShowing () {
      return this.$store.getters.getFunctionAreaShowing
    },
    commands () {
      return this.$store.getters.getCommands
    },
    activeFunctions () {
      return this.$store.getters.getActiveFunctions
    },
    colorSelected () {
      return this.$store.getters.getColorSelected
    },
    permanentImages () {
      return this.$store.getters.getPermanentImages
    },
    cmdImages () {
      return this.permanentImages.cmdImages
    },
    colorPallet () {
      return this.permanentImages.colorPallet
    },
    colors () {
      return this.$store.getters.getColors
    },
    funcImages () {
      return this.permanentImages.funcImages
    },
    swiperSlide () {
      return this.$store.getters.getSwiperSlide
    }
  },
  data () {
    return {
      commandEvt: null,
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
          put: ['commands-staged']
        },
        filter: '.command-name',
        dragClass: 'dragging',
        sort: false,
        ghostClass: 'ghost'
      },
      currentColor: this.colorSelected
    }
  },
  methods: {
    notEditableMessage (evt) {
      const messageBuilder = {
        type: 'warn',
        msg: 'Can\'t edit'
      }

      this.$store.dispatch('addMessage', messageBuilder)
    },
    editingFunctionMessage (func) {
      if (this.editingFunction) {
        const messageBuilder = {
          type: 'success',
          msg: `${func.name}`
        }
        this.$store.dispatch('addMessage', messageBuilder)
      }
    },
    findColor () {
      return this.colors[this.colorSelected].next
    },
    applyColorConditional () {
      const color = this.findColor()
      this.$store.dispatch('colorSelected', color)
    },
    togglePopoverBucket ({ind, show}) {
      this.$store.dispatch('updateEditingIndex', ind)
      this.$store.dispatch('updateFunctionAreaShowing', show)
    },
    toggleFunctionEdit (evt, _2, ind) {
      this.commandEvt = evt
      const i = ind === this.editingIndex ? null : ind
      const show = i === null ? 'editMain' : 'editFunction'
      this.togglePopoverBucket({ind: i, show: show})
    },
    toggleFunctionAdd (evt) {
      this.commandEvt = evt
      this.togglePopoverBucket({show: this.functionAreaShowing === 'addFunction' ? 'editMain' : 'addFunction'})
    },
    closeFunctionBox () {
      this.commandEvt = null
      this.togglePopoverBucket({ind: null, show: 'editMain'})
    },
    start () {
      if (this.functionAreaShowing === 'editMain') {
        this.$store.dispatch('toggleShowMesh', true)
      }
    },
    end () {
      this.$store.dispatch('toggleShowMesh', false)
    },
    addToActiveFunc (evt) {
      const index = evt.item.getAttribute('data-function-index')

      // console.log('INDEX IN ~ ', index);

      api.activateFunction({tokenId: this.token.token_id, stagedIndex: index, activeIndex: evt.newIndex}, lambdas => {
        // console.log('NEW LAMBDAS ~ ', lambdas)
        this.$store.dispatch('updateLambdas', {lambdas: lambdas})
      })
    },
    moveSwiper (direction) {
      this.closeFunctionBox();
      (function (windowWidth, dis) {
        const $functions = $('.functions')
        const functionsWidth = $functions.width()
        const $functionBoxes = $functions.children()
        const $firstFunctionBox = $functionBoxes.first()
        const functionBoxMarginRight = Number($firstFunctionBox.css('margin-right').replace('px', ''))
        const functionBoxMarginBottom = Number($firstFunctionBox.css('margin-bottom').replace('px', ''))
        const functionBoxWidth = $firstFunctionBox.outerWidth() + (functionBoxMarginRight * 2)
        const functionBoxHeight = $firstFunctionBox.outerHeight() + (functionBoxMarginBottom)
        const amtPerRow = Math.floor(functionsWidth / functionBoxWidth)
        const rowCount = Math.ceil($functionBoxes.length / amtPerRow)
        const allRowsHeight = functionBoxHeight * rowCount
        const ableToScrollDown = (dis.functionsPosition + functionBoxHeight) < allRowsHeight

        if (direction === 'up' && dis.functionsPosition > 0) {
          dis.functionsPosition -= functionBoxHeight
        } else if (direction === 'down' && ableToScrollDown) {
          dis.functionsPosition += functionBoxHeight
        }

        $functions.animate({scrollTop: dis.functionsPosition + 'px'}, 300, 'linear')
      })($(window).width(), this)
    }
  },
  components: {
    draggable,
    FunctionBox,
    PopoverBucket
  }
}
</script>

<style scoped src="../css/scoped/commands.css"></style>
