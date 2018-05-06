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
  constructor ({type, msg, ind, state, runOnDelete}) {
    this.type = type;
    this.msg = msg ? this._messages(msg) : null;
    this.state = state;
    this.index = ind || null;
    this.id = !ind ? uid(7) : null;
    this.runOnDelete = runOnDelete || function () {}
    this.timeOutCounter = 5000
    this._removeSelf = this._removeSelf.bind(this)
    this._timedDelete = this._timedDelete.bind(this)
  }

  _messages (msg) {
    return msg
  }

  _removeSelf () {
    this.state.messageList = this.state.messageList.filter(m => m.id !== this.id)
    this.runOnDelete();
  }

  _timedDelete () {
    if (!this.timeOutCounter) return this._removeSelf();
    this.timeOutCounter -= 10
    setTimeout(this._timedDelete, 10);
  }

  add () {
    if (!this.state.messageList.filter(m => m.msg === this.msg).length) {
      this.state.messageList.unshift(this);
      this.state.messageList = this.state.messageList.slice(0, 5);
      this._timedDelete()
    }
  }

  delete () {
    this.timeOutCounter = 0
  }
}

export default Message;
