<template>
  <div class="function-drop"
       :style="showMesh ? {'background-image': 'url(' + permanentImages.gridMesh + ')'} : {}"
  >
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
  import draggable from 'vuedraggable';
  import FunctionBox from './Function_box';
  import utils from '../services/utils';

  export default {
    name: 'function_drop',
    mounted() {
      this.adjustJustify();
      window.addEventListener('resize', () => {
        this.adjustJustify();
      });
    },
    updated() {
      this.$nextTick(this.adjustJustify);
    },
    computed: {
      showMesh() {
        return this.$store.getters.getShowMesh;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      },
      functionAreaShowing() {
        return this.$store.getters.getFunctionAreaShowing;
      }
    },
    methods: {
      adjustJustify() {
        const $dropZone = document.querySelector('.function-drop-drop-zone');
        if (this.list.length && this.functionAreaShowing === 'editMain' && $dropZone !== null) {
          const dropZoneWidth = $dropZone.offsetWidth;
          const $lastButton = $dropZone.lastChild;
          const lastButtonWidth = $lastButton.offsetWidth;
          const lastButtonLeft = $lastButton.offsetLeft;

          if ((dropZoneWidth + lastButtonWidth) < lastButtonLeft) {
            $dropZone.style['justify-content'] = 'flex-start';
          } else {
            $dropZone.style['justify-content'] = 'center';
          }
        } else {
          $dropZone.style['justify-content'] = 'center';
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

<style scoped src="../css/functionDrop.css"></style>
