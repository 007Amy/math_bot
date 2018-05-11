<template>
  <div class="message-container">
    <div v-for="(message, ind) in messageList" class="message" :class="message.type" :id="'message-' + ind" :key="message.id" @click="removeMessage(ind)">
      {{ message.msg }}
    </div>
  </div>
</template>

<script>
export default {
  mounted () {
  },
  computed: {
    messageList () {
      return this.$store.getters.getMessageList
    },
    permanentImages () {
      return this.$store.getters.getPermanentImages
    }
  },
  methods: {
    removeMessage (message) {
      this.$store.dispatch('removeMessage', message)
    }
  }
}
</script>

<style scoped src="../css/scoped/messages.css"></style>
