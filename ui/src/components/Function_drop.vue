<template>
  <div class="function-drop" :class="showMesh ? 'mesh-background' : ''">
    <draggable
      class="function-drop-drop-zone"
      :list="list"
      :options="options"
      @change="change"
      @start="start"
      @add="functionAreaShowing === 'editMain' ? adjustJustify() : ''"
      @remove="functionAreaShowing === 'editMain' ? adjustJustify() : ''"
      @end="end"
    >
        <function-box
          v-for="(func, ind) in list"
          :key="'function-drop/' + ind"
          :func="func"
          :ind="ind"
          :collection="list"
          :origin="origin"
        ></function-box>

    </draggable>
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import FunctionBox from './Function_box'

export default {
  name: 'function_drop',
  mounted () {
    this.adjustJustify()
    window.addEventListener('resize', () => {
      if (window.location.hash === '#/robot') this.adjustJustify()
    })
  },
  updated () {
    this.$nextTick(this.adjustJustify)
  },
  computed: {
    showMesh () {
      return this.$store.getters.getShowMesh
    },
    permanentImages () {
      return this.$store.getters.getPermanentImages
    },
    functionAreaShowing () {
      return this.$store.getters.getFunctionAreaShowing
    }
  },
  methods: {
    adjustJustify () {
      const $dropZone = document.querySelector('.function-drop-drop-zone')
      if (this.list.length && this.functionAreaShowing === 'editMain' && $dropZone !== null) {
        const dropZoneWidth = $dropZone.offsetWidth
        const $lastButton = $dropZone.lastChild
        const lastButtonWidth = $lastButton.offsetWidth
        const lastButtonLeft = $lastButton.offsetLeft

        if ((dropZoneWidth + lastButtonWidth) < lastButtonLeft) {
          $dropZone.style['justify-content'] = 'flex-start'
        } else {
          $dropZone.style['justify-content'] = 'center'
        }
      } else {
        $dropZone.style['justify-content'] = 'center'
      }
    }
  },
  components: {
    draggable,
    FunctionBox
  },
  props: ['origin', 'list', 'options', 'change', 'start', 'end']
}
</script>

<style scoped src="../css/scoped/functionDrop.css"></style>
