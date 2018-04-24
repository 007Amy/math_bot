package model.models

import compiler.processor.Frame

/*
 * Only used in compiler.
 * */
case class GridMap(
    gMap: List[List[GridPart]],
    robotOrientation: String,
    success: (Frame, Problem) => Boolean,
    problem: Problem,
    description: String,
    toolList: ToolList = ToolList()
)
