<template>
  <div class="puzzle-piece">

    <div
      v-if="pieceToShow === 'closed'"
      class="piece closed-piece border-right"
      :style="{'background-image': `url(${backgroundImg})`}">
      <div v-if="showName">
        <div v-if="funcName !== ''" class="command-name">{{funcName}}</div>
        <div v-else class="command-name command-name-empty">Name me!</div>
      </div>
    </div>

    <div
      v-else-if="pieceToShow === 'start'"
      class="piece puzzle-start"
      :style="{'background-image': `url(${backgroundImg})`}">
      <div class="tab-insert">
        <div class="top-insert insert"></div>
        <div class="notch"></div>
        <div class="bottom-insert insert"></div>
      </div>
    </div>

    <div
      v-else-if="pieceToShow === 'middle'"
      class="piece puzzle-middle"
      :style="{'background-image': `url(${backgroundImg})`}">
      <div class="left-tab"></div>
      <div class="tab-insert">
        <div class="top-insert insert"></div>
        <div class="notch"></div>
        <div class="bottom-insert insert"></div>
      </div>
    </div>

    <div
      v-else-if="pieceToShow === 'end'"
      class="piece puzzle-end border-right"
      :style="{'background-image': `url(${backgroundImg})`}">
      <div class="left-tab"></div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'puzzle_pieces',
  mounted () {
    this.changeColor(this.color)
  },
  data () {
    return {
      colors: {
        default: '#FFFFFF',
        green: '#FF98B1',
        blue: '#4A90E2',
        red: '#F25C5C',
        purple: '#CA7AFF'
      }
    }
  },
  methods: {
    changeColor (color) {
      document.documentElement.style.setProperty(`--puzzle-piece-border-color`, this.colors[color])
    }
  },
  props: ['pieceToShow', 'backgroundImg', 'color', 'funcName', 'showName']
}
</script>

<style>
  :root {
    --puzzle-piece-border-color: #FFFFFF;
  }

  .piece {
    position: relative;
    height: 75px;
    width: 75px;
    margin-left: 9px;
    border-bottom-left-radius: 3px;
    border-top-left-radius: 3px;
    color: white;
    background-color: #000000;
    border: 1px solid var(--puzzle-piece-border-color);
    border-right: none;
    background-size: 70%;
    background-position: center;
    background-repeat: no-repeat;
    cursor: grab;
    cursor: -moz-grab;
    cursor: -webkit-grab;
  }

  .piece:active {
    cursor: grabbing;
    cursor: -moz-grabbing;
    cursor: -webkit-grabbing;
  }

  .puzzle-start, .puzzle-middle {
    width: 65px;
  }

  .border-right {
    border-right: 1px solid var(--puzzle-piece-border-color);
    border-top-right-radius: 3px;
    border-bottom-right-radius: 3px;
  }

  .closed-piece {
    width: 70px;
  }

  .left-tab {
    display: flex;
    position: absolute;
    align-items: center;
    left: -10px;
    height: 100%;
    background-color: transparent;
  }

  .left-tab::before {
    content: "";
    width: 10px;
    height: 20px;
    background-color: #000000;
    border-bottom-right-radius: 20px;
    border-top-right-radius: 20px;
    border: 1px solid var(--puzzle-piece-border-color);
    border-left: 0;
    box-sizing: border-box;
    transform: rotate(180deg);
  }

  .tab-insert {
    top: -1px;
    left: 64px;
    position: absolute;
    display: flex;
    flex-direction: column;
    width: 10px;
    height: 75px;
    background: radial-gradient(transparent 10px, rgba(0,0,0,1) 20px);
    border-top: 1px solid var(--puzzle-piece-border-color);
    border-bottom: 1px solid var(--puzzle-piece-border-color);
  }

  .top-insert, .bottom-insert {
    content: "";
    flex-grow: 1;
    width: 100%;
    border-right: 1px solid var(--puzzle-piece-border-color);
    background-color: #000000;
  }

  .insert {
    position: relative;
    overflow: hidden;
  }

  .notch {
    position: relative;
    left: -10px;
    width: 20px;
    height: 20px;
    background: radial-gradient(transparent 10px, rgba(0, 0, 0, 1) 10px) no-repeat 10px;
    pointer-events: none;
  }

  .command-name {
    position: absolute;
    top: 93%;
    width: 90%;
    left: 1px;
    right: 0;
    margin-left: auto;
    margin-right: auto;
    color: #000000;
    font-size: 12px;
    line-height: 14px;
    font-weight: bold;
    text-align: center;
    word-wrap: break-word;
    white-space: normal;
    border-radius: 3px;
    background-color: var(--puzzle-piece-border-color);
  }

  .command-name-empty {
    color: transparent;
  }

  /* 13" screen */
  @media only screen and (max-width : 1280px) {

  }

  /* Medium Devices, Desktops */
  @media only screen and (max-width : 992px) {
    .command-name {
      display: none;
    }
  }

  /* Small Devices */
  @media only screen and (max-width : 667px) {
    .command-name {
      display: none;
    }
  }

  /* Extra Small Devices, Phones */
  @media only screen and (max-width : 480px) {

  }

  /* Custom, iPhone Retina */
  @media only screen and (max-width : 320px) {

  }

  /* iPad */
  @media all and (device-width: 768px) and (device-height: 1024px) and (orientation:portrait) {
    .command-name {
      display: block;
    }
  }

  @media all and (device-width: 768px) and (device-height: 1024px) and (orientation:landscape) {

  }
</style>
