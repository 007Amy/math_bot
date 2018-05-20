<template>
 <div id="Marketing-top-section" data-aos="zoom-in">
   <div class="header-wrapper">
    <mathbot-header></mathbot-header>
   </div>
   <div class="header-content">
     <div class="slogan-box">
       <div class="slogan">
         Learn math by programming a robot
       </div>
       <div class="minor-slogan">
         From counting to calculus.
       </div>
       <div class="play-now" @click="gotosite()">PLAY NOW</div>
     </div>
   </div>
   <img class="logo" :src="imgResources['mathBotLogo']" alt="Image of a robot.">
   <img class="planet planet-one" :src="imgResources['planetOne']">
   <img class="planet planet-two" :src="imgResources['planetTwo']">
   <img class="planet planet-three" :src="imgResources['planetThree']">
   <img class="planet planet-four" :src="imgResources['planetFour']">
   <img class="planet planet-five" :src="imgResources['planetFive']">
   <img class="space-ship" :src="imgResources['spaceShip']">
   <img class="arched-line" :src="imgResources['archedLine']">
 </div>
</template>

<script>
import MathbotHeader from '../Mathbot_header'
export default {
  name: 'TopSection',
  data () {
    return {
      imgResources: {
        'mathBotLogo': 'http://res.cloudinary.com/doohickey/image/upload/v1521989155/Logo_yplcxn.svg',
        'planetOne': 'http://res.cloudinary.com/doohickey/image/upload/v1521989169/noun_727334_cc_l5lza9.svg',
        'planetTwo': 'http://res.cloudinary.com/doohickey/image/upload/v1521989163/noun_727330_cc_eerpfh.svg',
        'planetThree': 'http://res.cloudinary.com/doohickey/image/upload/v1521989166/noun_727331_cc_hpgvzl.svg',
        'planetFour': 'http://res.cloudinary.com/doohickey/image/upload/v1521989160/noun_727327_cc_fhbmzh.svg',
        'planetFive': 'http://res.cloudinary.com/doohickey/image/upload/c_scale,w_100/v1522344876/planet_p_ddnulv.png',
        'spaceShip': 'http://res.cloudinary.com/doohickey/image/upload/v1521989176/noun_1018446_cc_zvq5px.svg',
        'archedLine': 'http://res.cloudinary.com/doohickey/image/upload/v1521989179/Path_2_ggddw3.svg'
      }
    }
  },
  components: {
    MathbotHeader
  },
  props: ['gotosite']
}
</script>

<style scoped>
  #Marketing-top-section {
    grid-area: topsection;
    /* Create the parallax scrolling effect */
    background: url("http://res.cloudinary.com/doohickey/image/upload/v1521748894/Header_Background_xlmkkt.png") no-repeat fixed center;
    background-size: cover;

    padding: 100px 0 100px 0;

    display: grid;
    grid-template-columns: [c-one]12.5% [c-two]12.5% [c-three]12.5% [c-four]12.5% [c-five]12.5% [c-six]12.5% [c-seven]12.5% [c-eight]12.5%;
    grid-template-rows: [r-header]50px [r-one]auto [r-two]auto [r-three]auto [r-four]auto [r-five]auto [r-six]auto [r-seven]auto [r-eight]auto [r-nine]auto;
  }

  .header-wrapper {
    grid-column: c-two / span 2;
    grid-row: r-header;
    text-align: left;
  }

  .header-content {
    text-align: left;
    width: auto;
    grid-column: c-two / span 4;
    grid-row: r-three / span 3;
    z-index: 4;
    display: flex;
    align-content: flex-end;
  }

  .slogan-box {
    display: flex;
    flex-direction: column;
    align-content: flex-end;
  }

  .slogan {
    color: #FFFFFF;
    font-size: 64px;
    line-height: 72px;
    margin-bottom: 15px;
  }

  .minor-slogan {
    opacity: 0.8;
    color: #FFFFFF;
    font-size: 32px;
    line-height: 38px;
    margin-bottom: 59px;
  }

  .logo {
    display: inline-block;
    grid-column: c-six / span 2;
    grid-row: r-three / span 2;
    z-index: 4;
  }

  .planet {
    z-index: 3;
  }

  .planet-one {
    grid-column: c-five / span 2;
    grid-row: r-one / span 3;
    align-self: end;
    justify-self: end;
  }

  .planet-two {
    grid-column: c-seven / span 2;
    grid-row: r-four / span 2;
    justify-self: center;
  }

  .planet-three {
    grid-column: c-six / span 2;
    grid-row: r-six / span 2;
    justify-self: center;
    align-self: start;
  }

  .planet-four {
    grid-column: c-three / span 2;
    grid-row: r-five / span 2;
    justify-self: center;
    align-self: start;
    margin-top: 30px;
  }

  .planet-five {
    grid-column: c-seven / span 2;
    grid-row: r-header / span 2;
    justify-self: center;
  }

  .space-ship {
    grid-column: c-five;
    grid-row: r-six;
    justify-self: end;
  }

  .arched-line {
    grid-column: c-one / span 8;
    grid-row: r-four / span 3;
    z-index: 2;
    align-self: end;
    max-width: 100vw;
  }

  @media (max-width: 813px) {
    #Marketing-top-section {
      padding: 100px 0 100px 0;
    }

    .header-wrapper {
      grid-column: c-one / span 2;
      margin-left: 30px;
    }

    .header-content {
      grid-column: c-one / span 5;
      margin-left: 30px;
    }

    .slogan {
      font-size: 45px;
      line-height: 72px;
      margin-bottom: 15px;
    }

    .minor-slogan {
      font-size: 30px;
      line-height: 38px;
      margin-bottom: 59px;
    }

    .planet-one {
      grid-column: c-four / span 2;
    }

    .planet-two {
      grid-column: c-six / span 2;
    }

    .planet-three {
      grid-column: c-five / span 2;
    }

    .planet-four {
      grid-column: c-two / span 2;
    }

    .planet-five {
      grid-column: c-six / span 2;
    }

    .space-ship {
      grid-column: c-four;
    }
  }

  @media (max-width: 640px) {
    #Marketing-top-section {
      padding: 20px 0 20px 0;
    }

    .header-wrapper {
      grid-column: c-two / span 2;
      margin-left: 30px;
    }

    .header-content {
      grid-column: c-two / span 3;
      grid-row: c-five / span 4;
      margin: 0;
    }

    .slogan {
      font-size: 30px;
      line-height: 30px;
      margin-bottom: 15px;
    }

    .minor-slogan {
      font-size: 25px;
      line-height: 38px;
      margin-bottom: 59px;
    }

    .planet-one {
      display: none;
    }

    .planet-two {
      display: none;
    }

    .planet-three {
      display: none;
    }

    .planet-four {
      display: none;
    }

    .planet-five {
      display: none;
    }

    .space-ship {
      grid-column: c-four / span 1;
      grid-row: r-two / span 1;
      width: 70%;
      margin-top: 10px;
    }

    .logo {
      grid-column: c-four / span 1;
      grid-row: r-header / span 1;
      width: 35px;
    }

    .arched-line {
      grid-column: c-one / span 8;
      grid-row: r-two / span 1;
    }
  }

</style>
