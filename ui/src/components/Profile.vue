<template>
  <div class="profile-container">
    <div class="profile" data-aos="fade-in">
      <div class="secretTools">
        <button @click="unlock()">Unlock</button>
        <button @click="reset()">Reset</button>
      </div>
      <splash-screen v-if="stats === undefined"></splash-screen>
      <arithmetic v-else-if="!splashScreenShowing && profileView === 'Arithmetic' && stats !== undefined"></arithmetic>
      <div class="bottomButtons">
        <img :src="permanentImages.instructionsRobot">
        <div class="mathbotText">MathBot</div>
        <div @click="$router.push({path: '/marketing'})" class="text" id="about">About</div>
        <div v-if="auth !== null" @click="auth.logout()" class="text" id="signOut">Sign Out</div>
      </div>
    </div>
  </div>
</template>

<script>
  import SplashScreen from './Splash_screen'
  import Arithmetic from "./Arithmetic";
  import AuthService from '../services/AuthService';

  export default {
    mounted() {
      this.auth = new AuthService(this)
      this.auth.createLock()
      this.auth.init();
    },
    data () {
      return {
        auth: null
      }
    },
    computed: {
      splashScreenShowing() {
        return this.$store.getters.getSplashScreenShowing;
      },
      profileView() {
        return this.$store.getters.getProfileView;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      },
      stats() {
        return this.$store.getters.getStats
      }
    },
    methods: {
      unlock() {
        const tokenId = this.$store.getters.getToken.token_id;
        this.$http.get('/api/stats/unlock/' + tokenId)
          .then(res => {
            this.$store.dispatch('updateStats', {stats: res.body});
          })
          .catch(err => console.error(err.message));
      },
      reset() {
        const tokenId = this.$store.getters.getToken.token_id;
        this.$http.get('/api/stats/reset/' + tokenId)
          .then(res => {
            this.$store.dispatch('updateStats', {stats: res.body});
          })
          .catch(err => console.error(err.message));
      },
    },
    components: {
      Arithmetic,
      SplashScreen
    }
  };
</script>

<style scoped src="../css/profile.css">

  @media (max-width: 1366px), (max-height: 1024px) {
    .profile {
      left: 300px;
      right: 200px;
    }
    img {
      height: 210px;
      width: 186px;
      bottom: -16px;
    }
    #about {
      height: 26px;
      width: 61px;
      margin-left: 210px;
    }
    #signOut {
      height: 26px;
      width: 105px;
      margin-left: 313px;
    }
    .text {
      font-size: 22px;
      line-height: 26px;
    }
    .mathbotText {
      height: 63px;
      width: 183px;
      font-size: 52px;
      line-height: 63px;
      margin-left: 210px;
    }
  }
  @media (max-width: 1200px), (max-height: 900px) {
    .profile {
      left: 200px;
      right: 100px;
    }
    img {
      height: 193px;
      width: 171px;
      bottom: -15px;
    }
    #about {
      height: 24px;
      width: 56px;
      margin-left: 200px;
    }
    #signOut {
      height: 24px;
      width: 97px;
      margin-left: 295px;
    }
    .text {
      font-size: 21px;
      line-height: 24px
    }
    .mathbotText {
      height: 58px;
      width: 168px;
      font-size: 48px;
      line-height: 58px;
      margin-left: 200px;
      bottom: 52px;
    }
  }
  @media (max-width: 1100px), (max-height: 824px) {
    .profile {
      left: 0;
      right: 0;
    }
    img {
      width: 155px;
      height: 176px;
      bottom: -14px;
    }
    #about {
      height: 22px;
      width: 51px;
      margin-left: 180px;
    }
    #signOut {
      height: 22px;
      width: 88px;
      margin-left: 265px;
    }
    .text {
      font-size: 20px;
      line-height: 22px;
    }
    .mathbotText {
      height: 53px;
      width: 153px;
      font-size: 44px;
      line-height: 53px;
      margin-left: 180px;
      bottom: 47px;
    }
  }
  @media (max-width: 1000px), (max-height: 750px) {
    /*.profile {*/
      /*width: 900px;*/
    /*}*/
    img {
      height: 158px;
      width: 140px;
      bottom: -12px;
    }
    #about {
      height: 20px;
      width: 46px;
      margin-left: 160px;
    }
    #signOut {
      height: 20px;
      width: 79px;
      margin-left: 240px;
    }
    .text {
      font-size: 17px;
      line-height: 20px;
    }
    .mathbotText {
      height: 47px;
      width: 138px;
      line-height: 47px;
      font-size: 40px;
      margin-left: 160px;
      bottom: 42px;
    }
  }
  @media (max-width: 900px), (max-height: 675px) {
    /*.profile {*/
      /*width: 800px;*/
    /*}*/
    img {
      height: 141px;
      width: 124px;
    }
    #about {
      height: 18px;
      width: 41px;
      margin-left: 148px;
    }
    #signOut {
      height: 18px;
      width: 70px;
      margin-left: 214px;
    }
    .text {
      font-size: 15px;
      line-height: 18px;
    }
    .mathbotText {
      height: 42px;
      width: 122px;
      font-size: 35px;
      line-height: 42px;
      bottom: 35px;
      margin-left: 145px;
    }
  }
</style>
