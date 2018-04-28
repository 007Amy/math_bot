export default {
  toggleFunctionEdit(context, func, ind, calledFrom) {
    if (context.editingIndex !== null && func.created_id === context.editingFunction.created_id && context.functionAreaShowing === 'editFunction') {
      context.$store.dispatch('updateFunctionAreaShowing', 'editMain');
      context.$store.dispatch('updateEditingIndex', null);
    } else {
      context.$store.dispatch('updateEditingIndex', ind);
      context.$store.dispatch('updateFunctionAreaShowing', 'editFunction');
    }
  }
}
