<template>
  <div class="messageContainer">
    <transition-group
      name="message-leave"
      leave-active-class="animated fadeOut"
    >
      <div v-for="(message, ind) in messageList" class="message" :id="'message' + ind" :key="'message/' + (ind + 1)">
        {{ message }}
      </div>
    </transition-group>
  </div>
</template>

<script>
  import uid from "uid";
	export default {
    mounted() {
    },
    computed: {
      messageList() {
        const m = this.$store.getters.getMessageList.map(m => m.msg);
        return m;
      }
    },
    methods: {
      uID() {
        return uid(7);
      },
    }
  }
</script>

<style scoped src="../css/messages.css"></style>
