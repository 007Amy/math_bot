<template>
 <div id="Marketing-middle-section">
   <div class="message top-message">
     <div class="header">For kids and adults.</div>
     <p>To get started you don't need to know how to read or add, but before you're finished you'll know how to calculate derivatives.</p>
   </div>
   <div class="message middle-message">
     <div class="header">Super easy peasey.</div>
     <p>Programming is simple. In fact, mathbot only has four commands (step forward, turn right, pickup, and put down) so you'll be crushing problems in under 3 minutes.</p>
   </div>
   <div class="message bottom-message">
     <div class="header">Math is Fun</div>
     <p>By using a robot to do the work you get all the joy of problem solving without any annoying or repetitive tasks. And since you are able to focus on the big picture you'll learn faster too.</p>
   </div>
   <img class="left-arch-line" :src="imgResources['leftArchLine']">
   <img class="ship-and-planet" :src="imgResources['shipAndPlanet']">
   <img class="middle-arch-line" :src="imgResources['middleArchLine']">
   <img class="planet-cluster" :src="imgResources['planetCluster']">
   <div class="play-now" @click="gotosite()">SIGN UP</div>
 </div>
</template>

<script>
export default {
  name: 'MiddleSection',
  data () {
    return {
      imgResources: {
        'leftArchLine': 'http://res.cloudinary.com/doohickey/image/upload/v1522023333/Path_3_i2cwxd.svg',
        'shipAndPlanet': 'http://res.cloudinary.com/doohickey/image/upload/v1522023583/Group_3_xhgk5t.png',
        'middleArchLine': 'http://res.cloudinary.com/doohickey/image/upload/v1522023645/Path_4_vwxssf.svg',
        'planetCluster': 'http://res.cloudinary.com/doohickey/image/upload/v1522023702/noun_922273_cc_atgmbu.svg'
      }
    }
  },
  props: ['gotosite']
}
</script>

<style scoped>
  #Marketing-middle-section {
    background-color: #FFFFFF;
    grid-area: middlesection;

    padding: 100px 0 100px 0;

    color: #000000;

    display: grid;
    grid-template-columns: [c-one]12.5% [c-two]12.5% [c-three]12.5% [c-four]12.5% [c-five]12.5% [c-six]12.5% [c-seven]12.5% [c-eight]12.5%;
    grid-template-rows: [r-one]auto[r-two]auto [r-three]auto [r-four]auto [r-five]auto [r-six]auto [r-seven]auto [r-eight]auto [r-nine]auto [r-ten]auto [r-eleven]auto [r-twelve]auto;
  }

  .message > p {
    font-size: 18px;
    line-height: 23px;
    text-align: left;
  }

  .message .header {
    font-size: 48px;
    line-height: 58px;
    text-align: left;
  }

  .top-message {
    grid-column: c-two / span 3;
    grid-row: r-two / span 2;
  }

  .middle-message {
    grid-column: c-six / span 3;
    grid-row: r-seven / span 2;
  }

  .bottom-message {
    grid-column: c-two / span 3;
    grid-row: r-nine / span 2;
  }

  .left-arch-line {
    grid-column: c-one / span 6;
    grid-row: r-three / span 2;
  }

  .ship-and-planet {
    grid-column: c-six / span 3;
    grid-row: r-one / span 4;
    align-self: end;
    margin-bottom: 80px;
  }

  .middle-arch-line {
    grid-column: c-four / span 3;
    grid-row: r-seven / span 2;
    align-self: end;
  }

  .planet-cluster {
    grid-column: c-two / span 3;
    grid-row: r-five / span 3;
    margin-left: 60px;
    align-self: end;
  }

  .play-now {
    grid-column: c-six / span 2;
    grid-row: r-nine / span 1;
  }

  @media (max-width: 813px) {
    #Marketing-middle-section {
      padding: 100px 0 100px 0;
    }

    .message .header {
      font-size: 40px;
    }

    .message > p {
      font-size: 20px;
    }

    .top-message {
      margin-left: 30px;
      grid-column: c-one / span 3;
      grid-row: r-one / span 2;
    }

    .middle-message {
      grid-column: c-five / span 3;
      grid-row: r-seven / span 2;
    }

    .bottom-message {
      margin-left: 30px;
      grid-column: c-one / span 3;
      grid-row: r-eleven / span 2;
    }

    .left-arch-line {
      grid-row: r-three / span 3;
      width: 75%;
    }

    .ship-and-planet {
      grid-column: c-six / span 3;
      grid-row: r-two / span 4;
      width: 75%;
    }

    .middle-arch-line {
      grid-column: c-four / span 3;
      grid-row: r-nine / span 3;
      width: 75%;
    }

    .planet-cluster {
      grid-column: c-one / span 3;
      grid-row: r-six / span 3;
      width: 50%;
      margin-bottom: 40px;
    }

    .play-now {
      grid-column: c-six / span 2;
      grid-row: r-twelve / span 2;
      margin-left: 20px;
    }
  }

  @media (max-width: 640px) {
    #Marketing-middle-section {
      padding: 20px 0 20px 0;
    }

    .message .header {
      font-size: 20px;
      line-height: 28px;
      text-decoration: underline;
    }

    .message > p {
      font-size: 18px;
    }

    .message {
      margin-bottom: 20px;
    }

    .top-message {
      grid-column: c-two/ span 3;
      grid-row: r-one / span 3;
      margin: 0;
      text-align: left;
    }

    .middle-message {
      grid-column: c-two / span 3;
      grid-row: r-seven / span 3;
      margin: 0;
    }

    .bottom-message {
      grid-column: c-two / span 3;
      grid-row: r-eleven / span 3;
      margin: 0;
    }

    .left-arch-line {
      grid-column: c-one / span 12;
      grid-row: r-thirteen / span 2;
    }

    .ship-and-planet {
      display: none;
    }

    .middle-arch-line {
      display: none;
    }

    .planet-cluster {
      display: none;
    }

    .play-now {
      grid-column: c-two / span 2;
      grid-row: r-thirteen / span 2;
      margin: 20px auto;
    }
  }
</style>
