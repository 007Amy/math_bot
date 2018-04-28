class PopoverBucket {
  constructor (context) {
    this.context = context
    this.pointerPosition = 0
    this.style = null
    this._makeStyle()
  }

  updatePointerPosition () {
    const editingIndex = this.context.$store.getters.getEditingIndex
    if (typeof editingIndex === 'number') {
      const $functions = $('.functions')
      const $clicked = $($functions.children()[editingIndex])
      this.pointerPosition = $clicked.offset().left - 25
    } else if (editingIndex !== null) {
      this.pointerPosition = $('.open-staged').offset().left - 25
    }
    this._makeStyle()
  }

  _makeStyle () {
    this.style = {'margin-left': this.pointerPosition + 'px'}
  }
}

export default PopoverBucket
