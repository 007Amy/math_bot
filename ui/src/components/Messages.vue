<template>
  <transition-group class="message-container" name="messages" tag="div" leave-active-class="animated zoomOut">
    <div v-for="(message, ind) in messageList" class="message" :class="message.type" :id="'message-' + ind" :key="'message/' + (ind + 1)" @click="removeMessage(ind)">
      {{ message.msg }}
    </div>
  </transition-group>
</template>

<script>
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
