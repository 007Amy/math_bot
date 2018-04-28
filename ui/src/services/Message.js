import uid from 'uid'

class Message {
  constructor ({msg, state}) {
    this.msg = this._messages[msg];
    this.state = state;
    this.id = uid(7);
    this._delete()
  }

  _messages = {
    mainMax: "The main function is full!",
    emptyMain: "The main function is empty!"
  }

  _delete () {
    setTimeout(() => {
      this.state.messageList = this.state.messageList.filter(m => {
        if (m.id !== this.id) return m
      })
    }, 3000)
  }
}

export default Message;
