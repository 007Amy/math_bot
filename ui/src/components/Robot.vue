<template>
  <div class="background-container" data-aos="fade-in">
    <div id="robot" class="row animated">

      <div id="control-panel-box">
        <control-panel></control-panel>
      </div>

      <div id="grid-box">
        <robotcarrying></robotcarrying>
        <grid></grid>
        <trash></trash>
      </div>

      <messages></messages>

      <div
        id="edit-main-box">
        <trash></trash>
        <editmain
          :functions="mainFunctionFunc"
        >
        </editmain>

      </div>

      <div id="commands-box">
        <commands></commands>
        <trash></trash>
      </div>

      <div class="filler-box"></div>
    </div>
  </div>
</template>

<script>
  import {_} from 'underscore';
  import Identicon from 'identicon.js';
  import md5 from 'md5';
  import Counter from './Counter';
  import Grid from './Grid';
  import Commands from './Commands';
  import Editmain from './Edit_main';
  import Robotcarrying from './Robot_carrying';
  import Editfunction from './Edit_function';
  import Trash from './Trash';
  import Messages from './Messages';
  import AuthService from '../services/AuthService';
  import ControlPanel from './Control_panel';

  export default {
    mounted() {
      const auth = new AuthService(this)
      auth.createLock()
      auth.init()
    },
    computed: {
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      },
      Functions() {
        return this.$store.getters.getFunctions;
      },
      currentFunction() {
        return this.$store.getters.getCurrentFunction;
      },
      programPanel() {
        return this.$store.getters.getProgramPanelShowing;
      },
      commands() {
        return this.$store.getters.getCommands;
      },
      authShowing() {
        return this.$store.getters.getAuthShowing;
      },
      currentFunction() {
        return this.$store.getters.getCurrentFunction;
      },
      congratsShowing() {
        return this.$store.getters.getCongratsShowing;
      },
      tryAgainShowing() {
        return this.$store.getters.getTryAgainShowing;
      },
      game() {
        return this.$store.getters.getGame;
      },
      userName() {
        let currentUser = this.$store.getters.getCurrentUser;
        const loggedIn = this.$store.getters.getLoggedIn;
        if (loggedIn) {
          if (currentUser.nickname) {
            return new Identicon(md5(this.$store.getters.getCurrentUser.nickname), 30).toString();
          }
        }
      },
      mainFunctionFunc() {
        const mainToken = this.$store.getters.getMainFunction;
        return mainToken === null ? [] : mainToken.func;
      },
      loggedInShowing() {
        return this.$store.getters.getLoggedInShowing;
      },
      loggedIn() {
        return this.$store.getters.getLoggedIn;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      },
      editFunctionShowing() {
        return this.$store.getters.getEditFunctionShowing;
      },
      functionAreaShowing() {
        return this.$store.getters.getFunctionAreaShowing;
      },
      swiperSlide() {
        return this.$store.getters.getSwiperSlide;
      },
      activeFunctionGroups() {
        return this.$store.getters.getActiveFunctionGroups;
      },
      currentStepData() {
        return this.$store.getters.getCurrentStepData;
      }
    },
    methods: {
      showProgramPanel() {
        this.$store.dispatch('controlProgramPanelShowing');
      },
      adjustSpeed() {
        this.speed = this.speed === 500 ? 200 : 500;
      },
    },
    components: {
      Grid,
      Commands,
      Robotcarrying,
      Editfunction,
      Trash,
      Editmain,
      Messages,
      ControlPanel
    },
  };
</script>

<style scoped src="../css/robot.css"></style>
