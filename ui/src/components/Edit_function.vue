<template>
  <div class="edit-function">
    <div class="edit-function-data">
      <div class="func-param-form">
        <div
          class='displayed-func'
          :style="{
            'background-image': 'url(' + funcImages[editingFunction.image] + ')',
            'border-color': colorSelected.hex}"
        >
        </div>
        <div
          class='color-selector'
          :style="{'background-color': colorSelected.hex}"
        >
        </div>

        <input v-default-value="editingFunction.name" class="func-name" type="text" maxlength="52" placeholder="Name your function here" v-model="editingFunction.name" @change="updateName()" />
      </div>

      <img class="close-edit-function dialog-button" @click="closeEditFunction" :src="permanentImages.buttons.xButton" data-toggle="tooltip" title="Close">
    </div>

    <div class="edit-function-content">

      <function-drop
        :list="functions"
        :options="mainDraggableOptions"
        :change="copyCommand"
        :start="moving"
        :end="end"
        :origin="'editFunction'"
      ></function-drop>

    </div>

  </div>
</template>

<script>
  import draggable from 'vuedraggable';
  import {_} from 'underscore';
  import buildUtils from '../services/build_function_utils';
  import uid from 'uid';
  import FunctionBox from './Function_box';
  import FunctionDrop from './Function_drop';

  export default {
    computed: {
      editingIndex() {
        return this.$store.getters.getEditingIndex;
      },
      editingFunction() {
        return this.$store.getters.getActiveFunctions[this.editingIndex];
      },
      funcImages() {
        return this.$store.getters.getPermanentImages.funcImages;
      },
      colorSelected() {
        return this.colors[this.currentColor];
      },
      currentColor() {
        return this.editingFunction.color;
      },
      functions() {
        return this.editingFunction.func;
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
    },
    data() {
      return {
        func: {
          color: 'default',
          commandId: null,
          created_id: '12345',
          name: ''
        },
        color: this.currentColor,
        colors: {
          default: {
            hex: '#FFFFFF',
            next: 'grey'
          },
          grey: {
            hex: '#CCCCCC',
            next: 'blue'
          },
          blue: {
            hex: '#4A90E2',
            next: 'purple'
          },
          purple: {
            hex: '#CA7AFF',
            next: 'green'
          },
          green: {
            hex: '#50E3C2',
            next: 'pink'
          },
          pink: {
            hex: '#FF98B1',
            next: 'red'
          },
          red: {
            hex: '#F25C5C',
            next: 'default'
          }
        },
        mainDraggableOptions: {
          group: {
            name: 'commands-slide',
            pull: true,
            put: true,
          },
          animation: 100,
          ghostClass: 'ghost',
          chosenClass: 'chosen',
          filter: '.noDrag'
        },
      }
    },
    methods: {
      findColor() {
        return this.colors[this.currentColor].next;
      },
      updateName() {
        buildUtils.updateFunctionsOnChange({ context: this, currentFunction: this.editingFunction, addedFunction: null, newIndex: this.editingFunction.index, newColor: null})
      },
      applyColorConditional() {
        const newColor = this.findColor();
        this.color = newColor;
        buildUtils.updateFunctionsOnChange({ context: this, currentFunction: this.editingFunction, addedFunction: null, newIndex: this.editingFunction.index, newColor: newColor })
        this.color = 'default';
      },
      deleteFuncContents() { // in case we want to implement later
        let deleteFuncContents = buildUtils.currentFunc(this);
        deleteFuncContents.func = [];
        buildUtils.updateFunctionsOnChange(({context: this, currentFunction: deleteFuncContents, addedFunction: null, newIndex: null}));
      },
      copyCommand(evt) {
        if (!evt.hasOwnProperty('removed')) {
          const command = evt.hasOwnProperty('added') ? evt.added.element : evt.moved.element;
          const ind = evt.hasOwnProperty('added') ? evt.added.newIndex : evt.moved.newIndex;
          buildUtils.updateFunctionsOnChange({context: this, currentFunction: buildUtils.currentFunc(this), addedFunction: command, newIndex: ind});
        }
      },
      closeEditFunction() {
        this.$store.dispatch('updateFunctionAreaShowing', 'editMain');
        this.$store.dispatch('updateEditingIndex', null);
      },
      uID() {
        return uid(7);
      },
      moving(evt) {
        this.$store.dispatch('updateTrashVisible', true);
      },
      end() {
        this.$store.dispatch('updateTrashVisible', false);
      }
    },
    components: {
      draggable,
      FunctionBox,
      FunctionDrop
    },
  }
</script>

<style scoped src="../css/editFunction.css"></style>
