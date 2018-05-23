<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'app',
  mounted () {
    window.scrollTo(0, 1)
    this.auth.isAuthenticated()
  },
  computed: {
    auth () {
      return this.$store.getters.getAuth
    }
  }
}
</script>

<style src="./css/global/styles.css"></style>
