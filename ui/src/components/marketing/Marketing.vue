<template>
  <div id="Marketing" data-aos="zoom-in">
    <top-section :gotosite="goToSite"></top-section>
    <middle-section :gotosite="goToSite"></middle-section>
    <bottom-section></bottom-section>
  </div>
</template>

<script>
import TopSection from './TopSection'
import MiddleSection from './MiddleSection'
import BottomSection from './BottomSection'

export default {
  name: 'Marketing',
  components: {
    TopSection,
    MiddleSection,
    BottomSection
  },
  methods: {
    goToSite () {
      this.$router.push('/profile')
    }
  }
}
</script>

<style>
  @import url('https://fonts.googleapis.com/css?family=Roboto');
  @import url('https://fonts.googleapis.com/css?family=Material+Icons');

  body {
    margin: 0 auto;
  }

  * {
    font-family: 'probapro-regular', 'Roboto', Helvetica, Arial, sans-serif;
  }

  #app {
    overflow-y: auto;
  }

  #Marketing {
    /*height: 2000px;*/
    width: 100vw;
    max-width: 1366px;
    margin: 0 auto;
    font-family: "Proba Pro Regular", serif;
    display: grid;
    grid-template-rows: auto;
    grid-template-areas:
      "topsection topsection topsection topsection"
      "middlesection middlesection middlesection middlesection"
      "bottomsection bottomsection bottomsection bottomsection";
  }

  .play-now {
    height: 65px;
    width: 327px;
    color: #000000;
    font-size: 22px;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    border-radius: 1px;
    background-color: #B8E986;
    box-shadow: 0 2px 10px 0 rgba(0,0,0,0.5);
    cursor: pointer;
  }

  @media (max-width: 1366px), (max-height: 1024px) {
  }
  @media (max-width: 1200px), (max-height: 900px) {
  }
  @media (max-width: 1100px), (max-height: 824px) {
  }
  @media (max-width: 1000px), (max-height: 750px) {
  }
  @media (max-width: 900px), (max-height: 675px) {
  }
  @media (max-width: 800px), (max-height: 600px) {
    .play-now {
      width: 250px;
      height: 60px;
      font-size: 117%;
    }
  }
  @media only screen and (min-device-width: 320px) and (max-device-height: 568px) and (-webkit-device-pixel-ratio: 2) {
    .play-now {
      width: 150px;
      height: 40px;
    }
  }
</style>
