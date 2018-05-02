import Auth0Lock from 'auth0-lock';
import api from '../services/api';
import images from '../assets/assets';

import { AUTH0_DOMAIN, AUTH0_ID } from '../keys';

class AuthService {
  constructor (context) {
    this.context = context

    this.lock = null
  }

  createLock() {
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
    );
  }

  handleAuth(authResult) {
    const dis = this
    // Use the token in authResult to getUserInfo() and save it to localStorage
    this.lock.getUserInfo(authResult.accessToken, function (error, profile) {
      if (error) {
        // Handle error
        console.log("Error Retrieving Profile");
        return;
      }
      localStorage.setItem('accessToken', authResult.accessToken);
      localStorage.setItem('profile', JSON.stringify(profile));
      dis.login();
      dis.context.$router.push({path: "/profile"});
    });
  }

  login() {
    this.context.$store.dispatch("login");
    this.context.$store.dispatch("setCurrentUser");

    const tokenId = this.context.$store.getters.getCurrentUser.user_id || this.context.$store.getters.getCurrentUser.sub;

    api.getUserToken({tokenId: tokenId}, token => {
      this.context.$store.dispatch('updateToken', [token]);

      // Checks if level and step state is set in localstorge
      const levelAndStep = localStorage.getItem('LEVEL_STEP');

      if (levelAndStep !== null) {
        this.context.$store.dispatch('initNewGame', this.context);
      } else { // else set path to profile
        this.context.$router.push({path: 'profile'});
      }

      this.stopSplashScreen();
    })
  }

  logout() {
    this.context.$store.dispatch('removeNonces');
    this.context.$router.push({path: '/'});
    setTimeout(_ => location.reload());
  }

  startSplashScreen() {
    this.context.$store.dispatch('updateSplashScreenShowing', true);
  }

  stopSplashScreen() {
    this.context.$store.dispatch('updateSplashScreenShowing', false);
  }

  init() {
    // Listening for the authenticated event
    this.lock.on("authenticated", (authResult) => {
      this.handleAuth(authResult)
      this.stopSplashScreen();
    });

    if (this.context.$store.getters.getLoggedIn) {
      let token = this.context.$store.getters.getToken;
      if (token === null || token.token_id === '') {
        this.login();
      }
    }

    if (!this.context.$store.getters.getLoggedIn) {
      this.lock.show();
    }
  }
}

export default AuthService
