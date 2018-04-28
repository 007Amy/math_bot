<template>
  <transition-group class="message-container" name="messages" tag="div" leave-active-class="animated zoomOut">
    <div v-for="(message, ind) in messageList" class="message" :id="'message-' + ind" :key="'message/' + (ind + 1)">
      <p>{{ message.msg }}</p>
      <div>
        <img @click="removeMessage(ind)" :src="permanentImages.buttons.xButton" alt="x button">
      </div>
    </div>
  </transition-group>
</template>

<script>
  import uid from "uid";
	export default {
    mounted() {
    },
    computed: {
      messageList() {
        return this.$store.getters.getMessageList
      },
      permanentImages () {
        return this.$store.getters.getPermanentImages;
      }
    },
    methods: {
      removeMessage (message) {
        this.$store.dispatch('removeMessage', message);
      }
    }
  }
</script>

<style scoped src="../css/messages.css"></style>
