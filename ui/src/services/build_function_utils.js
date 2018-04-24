import api from './api'
import { _ } from 'underscore'
import uid from 'uid'
import renderUtils from './render_utils'

export default {
  currentFunc(context) {
    const editingFunctionIndex = context.$store.getters.getEditingIndex
    if (editingFunctionIndex === null) return context.$store.getters.getMainFunction;
    else return context.$store.getters.getActiveFunctions[editingFunctionIndex];
  },
  /*
   * updateFunctionsOnChange preps the current function to be sent to server
   * then sends the currentFunction to the server.
   * @param context {context function is referring too}
   * @param currentFunction {function being edited}
   * @param addedFunction {function being added to currentFunction}
   * @param newIndex {addedFunctoins new position on current function}
   **/
  updateFunctionsOnChange({context, currentFunction, addedFunction, newIndex, newColor, override}) {
    const token = context.$store.getters.getToken;
    if (addedFunction !== null) {
      const tokenizedAdded = {};
      tokenizedAdded.created_id = addedFunction.created_id;
      tokenizedAdded.color = addedFunction.color;
      tokenizedAdded.image = addedFunction.image;
      tokenizedAdded.name = addedFunction.name;
      tokenizedAdded.index = addedFunction.index;

      if (newIndex === 'length') {
        currentFunction.func.push(tokenizedAdded);
      } else {
        currentFunction.func[newIndex] = tokenizedAdded;
      }
    }
    currentFunction.color = newColor ? newColor : currentFunction.color;
    // console.log('sentFunction', JSON.stringify(currentFunction, null, 4));
    /*
    * send function to server to update lambdas
    * */
    api.putFunc({tokenId: token.token_id, funcToken: currentFunction, override}, (lambdas) => {
      context.$store.dispatch('updateLambdas', {lambdas: lambdas});
    })
  },
}
