<template>
  <div>
    <draggable
      v-if="trashVisible"
      class="fire"
      :list="trash"
      :options="trashDraggableData"
      @change="dumpTrash($event)"
      @onDragOver="test($event)"
    >
    </draggable>
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import buildUtils from '../services/build_function_utils'

export default {
  data () {
    return {
      trash: [],
      trashDraggableData: {
        group: {
          name: 'commands-slide',
          put: true,
          pull: false
        },
        scroll: false
      }
    }
  },
  computed: {
    permanentImages () {
      return this.$store.getters.getPermanentImages
    },
    trashVisible () {
      return this.$store.getters.getTrashVisible
    }
  },
  methods: {
    dumpTrash (evt) {
      this.$store.dispatch('updateTrashVisible', false)
      buildUtils.updateFunctionsOnChange({context: this, currentFunction: buildUtils.currentFunc(this), addedFunction: null, newIndex: null, override: true})
    },
    test (evt) {
      console.log(evt)
    }
  },
  components: {
    draggable
  }
}
</script>

<style scoped src="../css/scoped/trash.css">

</style>
