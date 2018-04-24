package compiler.processor

import compiler.Grid
import compiler.operations.Operation

case class Frame(operation: Operation, register: Register, board: Grid, robotLocation: Option[RobotLocation] = None)
