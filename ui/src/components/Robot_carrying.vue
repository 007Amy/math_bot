<template>
  <div v-if="robotCarrying" class="robot-carrying" :style="robotCarrying.length ? {'background-color': 'rgba(0, 0, 0, 0.5)'} : ''">
    <img
      class="animated zoomIn"
      v-for="(image, ind) in robotCarrying"
      :key="'robot-carrying' + ind"
      :src="toolImages[image]"
    >
  </div>
</template>

<script>
  import uid from 'uid';
  import assets from '../assets/assets';

  export default {
    mounted () {
      const $grid = $('.grid');
      const gridWidth = $grid.width()
      $('.robot-carrying').css({width: gridWidth + 'px'})
    },
    computed: {
      toolImages() {
        return assets.tools;
      },
      robotCarrying() {
        return this.$store.getters.getRobotCarrying;
      },
      game() {
        return this.$store.getters.getGame;
      },
      level() {
        return this.$store.getters.getLevel;
      },
      equation() {
        return this.$store.getters.getCurrentEquation;
      },
    },
    methods: {
      uID() {
        return uid(7);
      },
    },
  };

</script>

<style scoped src="../css/robotCarrying.css">

/*  @media only screen and (max-width: 1366px) and (max-height: 1024px) {
    .expression {
      font-size: 2.3em;
    }
  }

  @media only screen and (max-width: 1280px), screen and (max-height: 800px) {
    .expression {
      font-size: 1.85em;
    }
  }

  @media only screen and (max-width: 1024px), screen and (max-height: 768px) {
    .expression {
      font-size: 1.7em;
    }
  }

  @media only screen and (max-width: 960px), screen and (max-height: 550px) {
    .expression {
      font-size: 1.3em;
    }
  }

  @media only screen and (max-width: 736px), screen and (max-height: 412px) {
    .expression {
      font-size: 0.9em;
    }
  }

  @media only screen and (max-width: 732px), screen and (max-height: 412px) {
    .expression {
      font-size: 0.85em;
    }
  }

  @media only screen and (max-width: 667px), screen and (max-height: 375px) {
    .expression {
      font-size: 0.82em;
    }
  }

  @media only screen and (max-width: 640px), screen and (max-height: 360px) {
    .expression {
      font-size: 0.8em;
    }
  }

  @media only screen and (max-width: 568px), screen and (max-height: 320px) {
    .expression {
      font-size: 0.7em;
    }
  }*/
</style>

