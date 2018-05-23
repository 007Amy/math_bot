class InitFocus {
  constructor (stepData) {
    this.initFocusList = stepData.initFocus
    this._iterate = this._iterate.bind(this)
    this._removePulse = this._removePulse.bind(this)
  }

  _pulseRate = 1800

  _addPulse ($ele, $parent) {
    return new Promise(resolve => {
      $ele.addClass('pulse')
      setTimeout(() => resolve(next => next()), this._pulseRate)
    })
  }

  _removePulse ($ele, $parent) {
    return new Promise(resolve => {
      $ele.removeClass('pulse')
      $parent.removeClass('functions-show-overflow')
      resolve(next => next())
    })
  }

  async _iterate () {
    if (!this.initFocusList.length) return
    const $ele = $(`#${this.initFocusList.shift()}`)

    const $parent = $ele.parent()

    $parent.addClass('functions-show-overflow')

    setTimeout(async () => {
      const addPulse = await this._addPulse($ele, $parent)

      addPulse(async () => {
        const removePulse = await this._removePulse($ele, $parent)
        removePulse(() => this._iterate())
      })
    }, 800)
  }

  init () {
    setTimeout(this._iterate, 200)
  }
}

export default InitFocus
