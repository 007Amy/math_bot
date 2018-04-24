<template>
  <div id="app" class="container-fluid" :style="$route.name === 'Marketing' ? {'background': '#321528'} :$route.name === 'Profile' ? {'background-image': 'url(' + permanentImages.profileSpace} : {'background-image': 'url(' + permanentImages.gridSpace}">
    <router-view></router-view>
  </div>
</template>

<script>
  export default {
    name: 'app',
    computed: {
      auth() {
        return this.$store.getters.getAuth;
      },
      permanentImages() {
        return this.$store.getters.getPermanentImages;
      }
    }
  };
</script>

<style src="./css/styles.css"></style>
