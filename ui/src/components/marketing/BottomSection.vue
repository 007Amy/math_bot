<template>
  <div id="Marketing-bottom-section">
    <div class="media bottom-info" v-if="showBottomInfo">
      <p>Right now Mathbot is still in development, but you can sign up and play with the robot for free. Once the site is open, prices will be determined for each class.</p>
      <a href="#">About</a><a href="#">Pricing</a>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BottomSection',
  data () {
    return {
      showBottomInfo: false
    }
  }
}
</script>

<style scoped>
  #Marketing-bottom-section {
    grid-area: bottomsection;
    /* Create the parallax scrolling effect */
    background: url("http://res.cloudinary.com/doohickey/image/upload/v1521748894/Header_Background_xlmkkt.png") no-repeat fixed 0 0;
    background-size: cover;

    padding: 100px 0 100px 0;

    display: grid;
    grid-template-columns: [c-one]auto [c-two]553px [c-three]auto;
    grid-template-rows: auto;
  }

  .bottom-info {
    grid-column: c-two;
    color: #FFFFFF;
    font-size: 18px;
    line-height: 23px;
    text-align: center;
  }

  .bottom-info > a {
    color: #FFFFFF;
    text-decoration: none;
    font-size: 26px;
    line-height: 32px;
    margin: 39px;
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

  }

</style>
