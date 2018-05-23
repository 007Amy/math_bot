import Vue from 'vue'
import Vuex from 'vuex'
import _ from 'underscore'
import VueDefaultValue from 'vue-default-value/dist/vue-default-value'
import permanentImages from '../assets/assets'
import Message from '../services/Message'
import AuthService from '../services/AuthService'
import StepData from '../services/StepData'
import utils from '../services/utils'

Vue.use(Vuex)
Vue.use(VueDefaultValue)

function addMessage (state, messageBuilder) {
  const message = new Message(state, messageBuilder)
  message.add()
}

function orderEm (steps) {
  const stepsInOrder = Object.keys(steps).filter(key => steps[key].prevStep === 'None').map(s => steps[s]);

  (function ordEm () {
    if (stepsInOrder[stepsInOrder.length - 1].nextStep === 'None') return

    stepsInOrder.push(steps[stepsInOrder[stepsInOrder.length - 1].nextStep])

    ordEm()
  })()

  return stepsInOrder
}

const colors = {
  default: {
    hex: '#FFFFFF',
    next: 'grey'
  },
  grey: {
    hex: '#CCCCCC',
    next: 'blue'
  },
  blue: {
    hex: '#4A90E2',
    next: 'purple'
  },
  purple: {
    hex: '#CA7AFF',
    next: 'green'
  },
  green: {
    hex: '#50E3C2',
    next: 'pink'
  },
  pink: {
    hex: '#FF98B1',
    next: 'red'
  },
  red: {
    hex: '#F25C5C',
    next: 'default'
  }
}

export default new Vuex.Store({
  state: {
    permanentImages: permanentImages,
    currentFunction: 'main',
    programPanelShowing: false,
    editFunctionShowing: false,
    currentUser: JSON.parse(localStorage.getItem('profile')),
    fullscreen: false,
    robotDeactivated: false,
    lock: {},
    showCongrats: false,
    tryAgainShowing: false,
    compiledDone: false,
    stepData: {params: null},
    robot: {},
    profileView: 'Arithmetic',
    functionGroupsCalc: null,
    functionGroups: [],
    functionAreaShowing: 'editMain',
    editingIndex: null,
    trashVisible: false,
    colors: colors,
    activeFunctionGroups: null,
    swiperSlide: 0,
    messageList: [],
    auth: new AuthService(),
    showMesh: false,
    splashScreenShowing: false
  },
  mutations: {
    UPDATE_STEP_DATA (state) {
      utils.watcher(() => !state.auth.authenticated, () => {
        state.stepData = new StepData(state.auth.userToken.token_id, state.auth.userToken.stats)
        state.stepData.getStep(function (lambdas) { state.auth.userToken.lambdas = lambdas })
      })
    },
    UPDATE_SPLASH_SCREEN_SHOWING (state, bool) {
      state.splashScreenShowing = bool
    },
    TOGGLE_SHOW_MESH (state, bool) {
      state.showMesh = bool
    },
    SET_AUTH (state, auth) {
      state.auth = auth
    },
    UPDATE_TRASH_VISIBLE (state, bool) {
      state.trashVisible = bool
    },
    CLEAR_CURRENT_FUNCTION (state) {
      const currentFunction = state.currentFunction
      if (currentFunction === 'main') {
        state.auth.userToken.lambdas.main.func = []
      } else {
        state.auth.userToken.lambdas.activeFuncs[currentFunction].func = []
      }
    },
    DEACTIVATE_ROBOT (state) {
      state.robotDeactivated = !state.robotDeactivated
    },
    TOGGLE_CONGRATS (state, bool) {
      state.showCongrats = bool
    },
    TOGGLE_TRY_AGAIN (state, bool) {
      state.tryAgainShowing = bool
    },
    UPDATE_ROBOT (state, robot) {
      state.robot = robot
    },
    UPDATE_LAMBDAS (state, {lambdas}) {
      state.auth.userToken.lambdas = lambdas
    },
    UPDATE_STATS (state, {stats, cb}) {
      state.auth.userToken.stats = stats
      if (cb) cb()
    },
    CREATE_LOCK (state) {

    },
    PUSH_RANDOM_IMAGE (state, image) {
      state.auth.userToken.randomImages.push(image)
    },
    CHANGE_CURRENT_FUNCTION (state, args) {
      const type = args[0]
      const ind = args[1]
      if (type === 'main') {
        state.currentFunction = 'main'
      } else {
        state.currentFunction = ind
      }
    },
    ADD_NEW_FUNCTION (state, func) {
      state.auth.userToken.oldVersion.funcs.push(func)
    },
    DELETE_FUNCTION (state, ind, context) {
      const currentFunction = state.currentFunction
      if (currentFunction === 'main') {
        state.auth.userToken.lambdas.main.func.splice(ind, 1)
      } else {
        state.auth.userToken.lambdas.activeFuncs[currentFunction].func.splice(ind, 1)
      }
    },
    PUT_IMAGE_BACK (state, img, context) {
      state.randomImages.unshift(img)
    },
    CONTROL_PROGRAM_PANEL_SHOWING (state) {
      state.programPanelShowing = !state.programPanelShowing
    },
    CONTROL_EDIT_FUNCTION_SHOWING (state, bool) {
      state.editFunctionShowing = bool
    },
    ADD_CURRENT_USER (state, userData) {
      state.currentUser = userData
      state.auth.userToken.token_id = userData.token_id
      state.auth.userToken.u_id = userData._id
    },
    CHANGE_ROBOT_SPEED (state) {
      state.robot.adjustSpeed()
    },
    CHANGE_FULLSCREEN (state) {
      state.fullscreen = !state.fullscreen
    },
    SPLICE_CURRENT (state, args) {
      const ind = args[0]
      const copy = args[1]
      const currentFunc = state.currentFunction
      const funcToUpdate = currentFunc === 'main' ? state.auth.userToken.lambdas.main.func : state.auth.userToken.lambdas.activeFuncs[currentFunc].func
      funcToUpdate.splice(ind, 1)
      funcToUpdate.splice(ind, 0, copy)
    },
    PUSH_CURRENT (state, c) {
      const copy = c
      const currentFunc = state.currentFunction
      const funcToUpdate = currentFunc === 'main' ? state.auth.userToken.lambdas.main.func : state.auth.userToken.lambdas.activeFuncs[currentFunc].func

      funcToUpdate.push(copy)
    },
    INSERT_CURRENT (state, args) {
      const ind = args[0]
      const copy = args[1]
      const currentFunc = state.currentFunction
      const funcToUpdate = currentFunc === 'main' ? state.auth.userToken.lambdas.main.func : state.auth.userToken.lambdas.activeFuncs[currentFunc].func

      funcToUpdate[state.currentFunction].splice(ind, 0, copy)
    },
    UPDATE_PROFILE_VIEW (state, loc) {
      state.profileView = loc
    },
    UPDATE_COURSE (state, course) {
      state.course = course
    },
    UPDATE_COMPILED_DONE (state, bool) {
      state.compiledDone = bool
    },
    UPDATE_PAUSED (state, bool) {
      state.robot.paused = bool
    },
    UPDATE_FUNCTION_AREA_SHOWING (state, show) {
      state.functionAreaShowing = show
    },
    UPDATE_EDITING_INDEX (state, index) {
      state.editingIndex = index
    },
    UPDATE_SWIPER_SLIDE (state, slide) {
      state.swiperSlide = slide
    },
    ADD_MESSAGE: addMessage,
    REMOVE_MESSAGE (state, ind) {
      state.messageList[ind].delete()
    },
    DELETE_MESSAGES (state) {
      state.messageList.map(m => m.delete())
    }
  },
  actions: {
    updateStepData ({commit}) {
      commit('UPDATE_STEP_DATA')
    },
    updateRobot ({commit}, robot) {
      commit('UPDATE_ROBOT', robot)
    },
    updateSplashScreenShowing ({commit}, bool) {
      commit('UPDATE_SPLASH_SCREEN_SHOWING', bool)
    },
    toggleShowMesh ({commit}, bool) {
      commit('TOGGLE_SHOW_MESH', bool)
    },
    setAuth ({commit}, auth) {
      commit('SET_AUTH', auth)
    },
    updateTrashVisible ({commit}, bool) {
      commit('UPDATE_TRASH_VISIBLE', bool)
    },
    updateFunctionGroups ({commit}) {
      commit('UPDATE_FUNCTION_GROUPS')
    },
    updateStats ({commit}, {stats, cb}) {
      commit('UPDATE_STATS', {stats, cb})
    },
    clearCurrentFunction ({commit}) {
      commit('CLEAR_CURRENT_FUNCTION')
    },
    deactivateRobot ({commit}) {
      commit('DEACTIVATE_ROBOT')
    },
    toggleCongrats ({commit}, bool) {
      commit('TOGGLE_CONGRATS', bool)
    },
    toggleTryAgain ({commit}, bool) {
      commit('TOGGLE_TRY_AGAIN', bool)
    },
    pushRandomImage ({commit}, image) {
      commit('PUSH_RANDOM_IMAGE', image)
    },
    addRandomImages ({commit}, images) {
      commit('ADD_RANDOM_IMAGES', images)
    },
    addFunctions ({commit}) {
      commit('ADD_FUNCTIONS')
    },
    changeCurrentFunction ({commit}, args) {
      commit('CHANGE_CURRENT_FUNCTION', args)
    },
    addNewFunction ({commit}, func) {
      commit('ADD_NEW_FUNCTION', func)
    },
    deleteFunction ({commit}, ind) {
      commit('DELETE_FUNCTION', ind)
    },
    putImageBack ({commit}, img) {
      commit('PUT_IMAGE_BACK', img)
    },
    controlProgramPanelShowing ({commit}) {
      commit('CONTROL_PROGRAM_PANEL_SHOWING')
    },
    controlEditFunctionShowing ({commit}, bool) {
      commit('CONTROL_EDIT_FUNCTION_SHOWING', bool)
    },
    addCurrentUser ({commit}, userData) {
      commit('ADD_CURRENT_USER', userData)
    },
    changeRobotSpeed ({commit}) {
      commit('CHANGE_ROBOT_SPEED')
    },
    changeFullscreen ({commit}) {
      commit('CHANGE_FULLSCREEN')
    },
    updateLambdas ({commit}, {lambdas}) {
      commit('UPDATE_LAMBDAS', {lambdas})
    },
    createLock ({commit}) {
      commit('CREATE_LOCK')
    },
    spliceCurrent ({commit}, args) {
      commit('SPLICE_CURRENT', args)
    },
    pushCurrent ({commit}, copy) {
      commit('PUSH_CURRENT', copy)
    },
    insertCurrent ({commit}, args) {
      commit('INSERT_CURRENT', args)
    },
    updateProfileView ({commit}, loc) {
      commit('UPDATE_PROFILE_VIEW', loc)
    },
    updateCourse ({commit}, course) {
      commit('UPDATE_COURSE', course)
    },
    updateCompiledDone ({commit}, bool) {
      commit('UPDATE_COMPILED_DONE', bool)
    },
    updatePaused ({commit}, bool) {
      commit('UPDATE_PAUSED', bool)
    },
    updateFunctionAreaShowing ({commit}, show) {
      commit('UPDATE_FUNCTION_AREA_SHOWING', show)
    },
    updateEditingIndex ({commit}, index) {
      commit('UPDATE_EDITING_INDEX', index)
    },
    updateSwiperSlide ({commit}, slide) {
      commit('UPDATE_SWIPER_SLIDE', slide)
    },
    addMessage ({commit}, messageBuilder) {
      commit('ADD_MESSAGE', messageBuilder)
    },
    removeMessage ({commit}, ind) {
      commit('REMOVE_MESSAGE', ind)
    },
    deleteMessages ({commit}) {
      commit('DELETE_MESSAGES')
    }
  },
  getters: {
    getStepData: state => state.stepData.params,
    getSplashScreenShowing: state => state.splashScreenShowing,
    getPointerPosition: state => state.pointerPosition,
    getShowMesh: state => state.showMesh,
    getAuth: state => state.auth,
    getTokenId: state => state.auth.userToken.token_id,
    getTrashVisible: state => state.trashVisible,
    // getFunctionGroups: state => state.functionGroups,
    getGrid: state => state.stepData.params.grid,
    getRobotDeactivated: state => state.stepData.robot.robotDeactivated,
    getCurrentEquation: state => state.auth.userToken.stats.currentEquation,
    getCongratsShowing: state => state.showCongrats,
    getTryAgainShowing: state => state.tryAgainShowing,
    getGame: state => state.game,
    getRobot: state => state.stepData.robot,
    getLevel: state => state.auth.userToken.stats.level,
    getRobotCarrying: state => state.stepData.robot.robotCarrying,
    getPermanentImages: state => state.permanentImages,
    getRandomImages: state => state.auth.userToken.randomImages,
    getCommands: state => state.auth.userToken.lambdas.cmds, // <-- Array
    getStagedFunctions: state => state.auth.userToken.lambdas.stagedFuncs, // <-- Array
    getActiveFunctions: state => state.auth.userToken.lambdas.activeFuncs, // <-- Array
    getMainFunction: state => state.auth.userToken.lambdas.main, // <-- Object
    getLambdas: state => state.auth.userToken.lambdas,
    getCurrentFunction: state => state.currentFunction,
    getProgramPanelShowing: state => state.programPanelShowing,
    getEditFunctionShowing: state => state.editFunctionShowing,
    getRememberUser: state => state.rememberUser,
    getToken: state => state.auth.userToken,
    getShowDupAlert: state => state.showDupAlert,
    getFullscreen: state => state.fullscreen,
    getSignupLoading: state => state.signupLoading,
    getLock: state => state.lock,
    getSocket: state => state.socket,
    getLevels: state => {
      const levels = state.auth.userToken.stats.levels
      const assembleLevels = _.chain(levels)
        .map((l, k) => {
          return [k, {
            level: l,
            name: k,
            prevStep: _.find(l, v => v.prevStep === 'None').prevLevel,
            nextStep: _.find(l, v => v.nextStep === 'None').nextLevel
          }]
        })
        .object()
        .value()

      return orderEm(assembleLevels)
    },
    getStats: state => state.auth.userToken.stats,
    getStep: state => state.auth.userToken.stats.step,
    getSteps: state => orderEm(state.auth.userToken.stats.levels[state.auth.userToken.stats.level]),
    getStars: state => state.stars,
    getProfileView: state => state.profileView,
    getCourses: state => state.courses,
    getCourse: state => state.course,
    getArithmeticLevels: state => state.arithmeticLevels,
    getCompiledDone: state => state.compiledDone,
    getPaused: state => state.robot.paused,
    getFunctionAreaShowing: state => state.functionAreaShowing,
    getEditingIndex: state => state.editingIndex,
    getColors: state => state.colors,
    getActiveFunctionGroups: state => state.activeFunctionGroups,
    getSwiperSlide: state => state.swiperSlide,
    getMessageList: state => state.messageList
  }
})
