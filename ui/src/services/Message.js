import uid from 'uid'
/*
* Used to add and remove messages
* @param{state}[object] = Vuex state property
*
* @param{messageBuilder}[object] =
*   {
*     type: [string], required
*     msg: [string], required
*     handlers: [function] (using closure for selectors), not required, see below
*   }
*
*   handlers: () => {
*       // Place selectors here
*
*       return {
*         runBeforeAppend () {}, not required
*         runOnDelete () {} not required
*       }
*     }
*
* @var{msg}[string] = message to display
* @var{state}[object] = state (store)
* @var{runBeforeAppend}[function] = function to run before appending message
* @var{runOnDelete}[function] = function to run after message is deleted
* @var{id} = unique id for this message
* @method{_messages} = if not included in messages will render given message (allows for custom messages)
* @method{delete} = deletes message
* @method{add} = adds message
* @method{_timedDelete} = private method to delete method in 3 seconds
* */
class Message {
  constructor (state, messageBuilder) {
    this.state = state

    this.id = uid(7)
    this.timeOutCounter = 3000

    this.type = messageBuilder.type
    this.msg = `${messageBuilder.msg}`
    this.handlers = messageBuilder.handlers ? messageBuilder.handlers() : {}

    this.runBeforeAppend = this.handlers.runBeforeAppend || function () {}
    this.runOnDelete = this.handlers.runOnDelete || function () {}

    this._removeSelf = this._removeSelf.bind(this)
    this._timedDelete = this._timedDelete.bind(this)
  }

  _removeSelf () {
    this.state.messageList = this.state.messageList.filter(m => m.id !== this.id)
    this.runOnDelete()
  }

  _timedDelete () {
    if (!this.timeOutCounter) return this._removeSelf()
    this.timeOutCounter -= 10
    setTimeout(this._timedDelete, 10)
  }

  add () {
    this.runBeforeAppend()
    this.state.messageList.unshift(this)
    this.state.messageList = this.state.messageList.slice(0, 2)
    this._timedDelete()
  }

  delete () {
    this.timeOutCounter = 0
  }
}

export default Message
