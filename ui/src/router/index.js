import Vue from 'vue';
import Router from 'vue-router';
import VueResource from 'vue-resource';
import Sortable from 'vue-sortable';
import {$} from 'jquery';
import Counter from 'components/Counter';
import Robot from 'components/Robot';
import Profile from 'components/Profile';
import Marketing from 'components/marketing/Marketing'

Vue.use(Router);
Vue.use(VueResource);
Vue.use(Sortable);

export default new Router({
  routes: [
    {
      path: '/count',
      name: 'Kitty Counter',
      component: Counter
    },
    {
      path: '/robot',
      name: 'Robot Counter',
      component: Robot
    },
    {
      path: '/signup',
      name: 'Signup',
      component: Robot,

    },
    {
      path: '/profile',
      name: 'Profile',
      component: Profile,
    },
    {
      path: '/about',
      name: 'Marketing',
      component: Marketing
    },
    {
      path: '*',
      redirect: localStorage.getItem('LAST_PATH') || '/about'
    }
  ]
});

