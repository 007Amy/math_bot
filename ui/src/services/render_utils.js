export default {
  toggleFunctionEdit(context, func, ind, calledFrom) {
    if (context.editingIndex !== null && func.created_id === context.editingFunction.created_id && context.functionAreaShowing === 'editFunction') {
      context.$store.dispatch('updateFunctionAreaShowing', 'editMain');
      context.$store.dispatch('updateEditingIndex', null);
    } else {
      context.$store.dispatch('updateEditingIndex', ind);
      context.$store.dispatch('updateFunctionAreaShowing', 'editFunction');
    }
  },
  cancelMessageFades() {
    let messages = $(".messageContainer").children();
    for (var i = 0; i < messages.length; i++) {
      $(messages[i]).stop();
      if (i === 0) {
        $(messages[i]).fadeTo(0, 1);
      } else if (i === 1) {
        $(messages[i]).fadeTo(0, .85);
      } else if (i === 2) {
        $(messages[i]).fadeTo(0, .7);
      } else if (i === 3) {
        $(messages[i]).fadeTo(0, .6);
      } else if (i === 4) {
        $(messages[i]).fadeTo(0, .5);
      } else if (i === 5) {
        $(messages[i]).fadeTo(0, .3);
      }
    }
  },
  fadeOutMessages: (messageList, cb) => {
    let fadeInterval = setInterval(() => {
      if (messageList.length > 0) {
        let messageId = $(".messageContainer").children().eq(messageList.length-1)[0].id;
        let el = $("#" + messageId);
        el.fadeTo(1000, 0, () => {
          if (messageId === $(".messageContainer").children().eq(messageList.length-1)[0].id) {
            cb()
          }
        });
      } else {
        clearInterval(fadeInterval);
      }
    }, 2500);
    return fadeInterval;
  }
}
