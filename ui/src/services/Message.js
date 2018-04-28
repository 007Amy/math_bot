import uid from 'uid'
/*
* Used to add and remove messages
* @param{msg} = message to display
* @param{state} = state (store)
* @{var}id = unique id for this message
* @{method}_messages = if not included in messages will render given message (allows for custom messages)
* @{method}delete = deletes message
* @{method}add = adds message
* @{method}_timedDelete = private method to delete method in 3 seconds
* */
class Message {
  constructor ({msg, ind, state}) {
    this.msg = msg ? this._messages(msg) : null;
    this.state = state;
    this.index = ind || null;
    this.id = !ind ? uid(7) : null;
    this.delete = this.delete.bind(this)
  }

  _messages (msg) {
    const messages = {
      mainMax: "The main function is full!",
      emptyMain: "The main function is empty!"
    }

    if (Object.keys(messages).includes(msg)) return messages[msg]
    else return msg
  }

  _timedDelete () {
    setTimeout(this.delete, 5000);
  }

  add () {
    this.state.messageList.unshift(this);
    this.state.messageList = this.state.messageList.slice(0, 5);
    this._timedDelete()
  }

  delete () {
    this.state.messageList.splice(this.index, 1);
  }
}

export default Message;
