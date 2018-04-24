<template>
  <div class="messageContainer">
  	<div v-for="(message, ind) in messageList" class="message" :id="'message' + ind">
      {{ messages[message] }}
    </div>
  </div>
</template>

<script>
  import uid from "uid";
	export default {
    mounted() {
    },
    computed: {
      messageList() {
        return this.$store.getters.getMessageList;
      }
    },
    data() {
      return {
        messages: {
          mainMax: "The main function is full!",
          emptyMain: "The main function is empty!"
        }
      }
    },
    methods: {
      uID() {
        return uid(7);
      },
    }
  }
</script>

<style scoped src="../css/messages.css">

   /*Media queries*/
 /* @media (max-width: 1366px), (max-height: 1024px) {
    .messageContainer {
      width: 158px;
    }
    .message {
      font-size: 12.2px;
      line-height: 13px;
    }
  }
  @media (max-width: 1200px), (max-height: 900px) {
    .messageContainer {
      width: 145px;
    }
    .message {
      font-size: 11.1px;
      line-height: 12px;
      padding-top: 7px;
      padding-bottom: 7px;
    }
  }
  @media (max-width: 1100px), (max-height: 824px) {
    .messageContainer {
      width: 131px;
    }
    .message {
      font-size: 12px;
      line-height: 11px;
      padding-top: 5px;
      padding-bottom: 5px;
    }
  }
  @media (max-width: 1000px), (max-height: 750px) {
    .messageContainer {
      width: 117px;
    }
    .message {
      margin-top: 5px;
      font-size: 12px;
    }
  }
  @media (max-width: 900px), (max-height: 675px) {
    .messageContainer {
      width: 103px;
    }
    .message {
      margin-top: 5px;
      font-size: 11.5px;
      padding-top: 4px;
      padding-bottom: 4px;
    }
  }
  @media (max-width: 800px), (max-height: 600px) {
    .messageContainer {
      width: 91px;
    }
    .message {
      margin-top: 5px;
      font-size: 10.5px;
      padding-top: 3px;
      padding-bottom: 3px;
    }
  }*/
</style>
