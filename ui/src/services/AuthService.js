import Auth0Lock from 'auth0-lock'
import api from '../services/api'
import images from '../assets/assets'

import { AUTH0_DOMAIN, AUTH0_ID } from '../keys'

class AuthService {
  constructor () {
    this.lock = null
    this.authenticated = false
    this.userToken = null
    this._init()
  }

  login (updateStepData) {
    if (this._isAuthenticated()) {
      this._getUserToken(updateStepData)
    } else {
      this.lock.show()
    }
  }

  logout () {
    localStorage.clear()
    this.userToken = null
    this.authenticated = false
    window.location = '/#/about'
  }
  _isAuthenticated () {
    return !!localStorage.getItem('accessToken')
  }

  _handleAuth (authResult) {
    // Use the token in authResult to getUserInfo() and save it to localStorage
    this.lock.getUserInfo(authResult.accessToken, (error, profile) => {
      if (error) {
        // Handle error
        console.log('Error Retrieving Profile')
        return
      }
      localStorage.setItem('accessToken', authResult.accessToken)
      localStorage.setItem('profile', JSON.stringify(profile))
      this._getUserToken()
    })
  }

  _getCurrentUser () {
    return JSON.parse(localStorage.getItem('profile'))
  }

  _getUserToken (updateStepData) {
    const currentUser = this._getCurrentUser()
    const tokenId = currentUser.user_id || currentUser.sub

    api.getUserToken({tokenId: tokenId}, token => {
      this.userToken = token
      if (updateStepData) updateStepData()
      setTimeout(() => {
        this.authenticated = true
      }, 300)
    })
  }

  _createLock () {
    this.lock = new Auth0Lock(
      AUTH0_ID,
      AUTH0_DOMAIN,
      {
        autofocus: false,
        auth: {
          redirect: false
        },
        theme: {
          primaryColor: '#00AAE4',
          logo: images.instructionsRobot
        },
        languageDictionary: {
          title: 'MATH_BOT'
        },
        autoclose: true
      }
    )
  }

  _init () {
    this._createLock()

    // Listening for the authenticated event
    this.lock.on('authenticated', (authResult) => {
      this._handleAuth(authResult)
    })
  }
}

export default AuthService
