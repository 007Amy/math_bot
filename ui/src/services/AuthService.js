import Auth0Lock from 'auth0-lock'
import api from '../services/api'
import images from '../assets/assets'

import { AUTH0_DOMAIN, AUTH0_ID } from '../keys'

class AuthService {
  constructor (context) {
    this.context = context
    this.lock = null
  }

  _handleAuth (authResult) {
    const dis = this
    // Use the token in authResult to getUserInfo() and save it to localStorage
    this.lock.getUserInfo(authResult.accessToken, function (error, profile) {
      if (error) {
        // Handle error
        console.log('Error Retrieving Profile')
        return
      }
      localStorage.setItem('accessToken', authResult.accessToken)
      localStorage.setItem('profile', JSON.stringify(profile))
      dis.login()
      dis.context.$router.push({path: '/profile'})
    })
  }

  _isLoggedIn () {
    return !!localStorage.getItem('profile')
  }

  _getCurrentUser () {
    return JSON.parse(localStorage.getItem('profile'))
  }

  _startSplashScreen () {
    this.context.$store.dispatch('updateSplashScreenShowing', true)
  }

  _stopSplashScreen () {
    this.context.$store.dispatch('updateSplashScreenShowing', false)
  }

  login () {
    const currentUser = this._getCurrentUser()
    const tokenId = currentUser.user_id || currentUser.sub

    api.getUserToken({tokenId: tokenId}, token => {
      token.userInfo = this._getCurrentUser()
      this.context.$store.dispatch('updateToken', [token, () => {
        const path = this.context.$route.path
        if (path === '/robot') { // If path is robot, initialize a new game
          this.context.$store.dispatch('initNewGame', this.context)
        }
        localStorage.setItem('LAST_PATH', this.context.$route.path)
        this._stopSplashScreen()
      }])
    })
  }

  createLock () {
    this.lock = new Auth0Lock(
      AUTH0_ID || process.env('AUTH0_ID'),
      AUTH0_DOMAIN || process.env('AUTH0_DOMAIN'),
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

  logout () {
    localStorage.clear()

    this.context.$router.push({path: '/about'})

    setTimeout(_ => location.reload())

    setTimeout(() => {
      location.reload()
    }, 1500)
  }

  init () {
    this._startSplashScreen()

    // Listening for the authenticated event
    this.lock.on('authenticated', (authResult) => {
      this._handleAuth(authResult)
      this._stopSplashScreen()
    })

    if (this._isLoggedIn()) {
      this.login()
    } else {
      this.lock.show()
    }
  }
}

export default AuthService
