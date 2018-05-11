<template>
  <div v-if="robotCarrying" class="robot-carrying" :style="robotCarrying.length ? {'background-color': 'rgba(0, 0, 0, 0.5)'} : ''">
    <p v-if="totalValueCarried">sum: {{totalValueCarried}}</p>
    <img
        class="animated zoomIn"
        v-for="(image, ind) in robotCarrying"
        :key="'robot-carrying' + ind"
        :src="toolImages[image]"
      >
  </div>
</template>

<script>
import uid from 'uid'
import assets from '../assets/assets'

export default {
  computed: {
    totalValueCarried () {
      return this.robotCarrying.reduce((acc, tool) => {
        switch (tool) {
          case 'kitty':
            acc += 1
            break
          case 'ten':
            acc += 10
            break
          case 'oneHundred':
            acc += 100
            break
          case 'oneThousand':
            acc += 1000
            break
          case 'tenHundred':
            acc += 10000
            break
          default:
            acc += 0
        }
        return acc
      }, 0)
    },
    toolImages () {
      return assets.tools
    },
    robotCarrying () {
      return this.$store.getters.getRobotCarrying
    },
    game () {
      return this.$store.getters.getGame
    },
    level () {
      return this.$store.getters.getLevel
    },
    equation () {
      return this.$store.getters.getCurrentEquation
    }
  },
  methods: {
    uID () {
      return uid(7)
    }
  }
}

</script>

<style scoped src="../css/scoped/robotCarrying.css"></style>
