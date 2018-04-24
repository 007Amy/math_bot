package model

import model.models.FuncToken

object CommandIds extends Enumeration {
  val changeRobotDirection = "changeRobotDirection"
  val moveRobotForwardOneSpot = "moveRobotForwardOneSpot"
  val setItemDown = "setItemDown"
  val pickUpItem = "pickUpItem"
}

object DefaultCommands {

  def main: FuncToken = FuncToken(
    created_id = "100001",
    name = None,
    func = Some(List.empty[FuncToken]),
    image = Some("main"),
    index = Some(0),
    `type` = Some("main-function"),
    commandId = Some("main-function")
  )

  def cmds: List[FuncToken] = List(
    FuncToken(
      created_id = "1001",
      name = Some("Turn Right"),
      func = Some(List.empty[Nothing]),
      image = Some("turn_right"),
      index = Some(0),
      `type` = Some("command"),
      commandId = Some(CommandIds.changeRobotDirection)
    ),
    FuncToken(
      created_id = "1002",
      name = Some("Walk"),
      func = Some(List.empty[Nothing]),
      image = Some("move_forward"),
      index = Some(1),
      `type` = Some("command"),
      commandId = Some(CommandIds.moveRobotForwardOneSpot)
    ),
    FuncToken(
      created_id = "1003",
      name = Some("Put Down"),
      func = Some(List.empty[Nothing]),
      image = Some("drop_item"),
      index = Some(2),
      `type` = Some("command"),
      commandId = Some(CommandIds.setItemDown)
    ),
    FuncToken(
      created_id = "1004",
      name = Some("Pick Up"),
      func = Some(List.empty[Nothing]),
      image = Some("pick_up"),
      index = Some(3),
      `type` = Some("command"),
      commandId = Some(CommandIds.pickUpItem)
    )
  )

  def funcs: List[FuncToken] = List(
    FuncToken(
      created_id = "2000000",
      name = Some(""),
      func = Some(List()),
      image = Some("1"),
      index = Some(0),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000001",
      name = Some(""),
      func = Some(List()),
      image = Some("2"),
      index = Some(1),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000002",
      name = Some(""),
      func = Some(List()),
      image = Some("3"),
      index = Some(2),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000003",
      name = Some(""),
      func = Some(List()),
      image = Some("4"),
      index = Some(3),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000004",
      name = Some(""),
      func = Some(List()),
      image = Some("5"),
      index = Some(4),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000005",
      name = Some(""),
      func = Some(List()),
      image = Some("6"),
      index = Some(5),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000006",
      name = Some(""),
      func = Some(List()),
      image = Some("7"),
      index = Some(6),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000007",
      name = Some(""),
      func = Some(List()),
      image = Some("8"),
      index = Some(7),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000008",
      name = Some(""),
      func = Some(List()),
      image = Some("9"),
      index = Some(8),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000009",
      name = Some(""),
      func = Some(List()),
      image = Some("10"),
      index = Some(9),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000010",
      name = Some(""),
      func = Some(List()),
      image = Some("11"),
      index = Some(10),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000011",
      name = Some(""),
      func = Some(List()),
      image = Some("12"),
      index = Some(11),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000012",
      name = Some(""),
      func = Some(List()),
      image = Some("13"),
      index = Some(12),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000013",
      name = Some(""),
      func = Some(List()),
      image = Some("14"),
      index = Some(13),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000014",
      name = Some(""),
      func = Some(List()),
      image = Some("15"),
      index = Some(14),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000015",
      name = Some(""),
      func = Some(List()),
      image = Some("16"),
      index = Some(15),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000016",
      name = Some(""),
      func = Some(List()),
      image = Some("17"),
      index = Some(16),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000017",
      name = Some(""),
      func = Some(List()),
      image = Some("18"),
      index = Some(17),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000018",
      name = Some(""),
      func = Some(List()),
      image = Some("19"),
      index = Some(18),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000019",
      name = Some(""),
      func = Some(List()),
      image = Some("20"),
      index = Some(19),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000020",
      name = Some(""),
      func = Some(List()),
      image = Some("21"),
      index = Some(20),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000021",
      name = Some(""),
      func = Some(List()),
      image = Some("22"),
      index = Some(21),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000022",
      name = Some(""),
      func = Some(List()),
      image = Some("23"),
      index = Some(22),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000023",
      name = Some(""),
      func = Some(List()),
      image = Some("24"),
      index = Some(23),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000024",
      name = Some(""),
      func = Some(List()),
      image = Some("25"),
      index = Some(24),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000025",
      name = Some(""),
      func = Some(List()),
      image = Some("26"),
      index = Some(25),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000026",
      name = Some(""),
      func = Some(List()),
      image = Some("27"),
      index = Some(26),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000027",
      name = Some(""),
      func = Some(List()),
      image = Some("28"),
      index = Some(27),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000028",
      name = Some(""),
      func = Some(List()),
      image = Some("29"),
      index = Some(28),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000029",
      name = Some(""),
      func = Some(List()),
      image = Some("30"),
      index = Some(29),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000030",
      name = Some(""),
      func = Some(List()),
      image = Some("31"),
      index = Some(30),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000031",
      name = Some(""),
      func = Some(List()),
      image = Some("32"),
      index = Some(31),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000032",
      name = Some(""),
      func = Some(List()),
      image = Some("33"),
      index = Some(32),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000033",
      name = Some(""),
      func = Some(List()),
      image = Some("34"),
      index = Some(33),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000034",
      name = Some(""),
      func = Some(List()),
      image = Some("35"),
      index = Some(34),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000035",
      name = Some(""),
      func = Some(List()),
      image = Some("36"),
      index = Some(35),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000036",
      name = Some(""),
      func = Some(List()),
      image = Some("37"),
      index = Some(36),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000037",
      name = Some(""),
      func = Some(List()),
      image = Some("38"),
      index = Some(37),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000038",
      name = Some(""),
      func = Some(List()),
      image = Some("39"),
      index = Some(38),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000039",
      name = Some(""),
      func = Some(List()),
      image = Some("40"),
      index = Some(39),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000040",
      name = Some(""),
      func = Some(List()),
      image = Some("41"),
      index = Some(40),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000041",
      name = Some(""),
      func = Some(List()),
      image = Some("42"),
      index = Some(41),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000042",
      name = Some(""),
      func = Some(List()),
      image = Some("43"),
      index = Some(42),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000043",
      name = Some(""),
      func = Some(List()),
      image = Some("44"),
      index = Some(43),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000044",
      name = Some(""),
      func = Some(List()),
      image = Some("45"),
      index = Some(44),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000045",
      name = Some(""),
      func = Some(List()),
      image = Some("46"),
      index = Some(45),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000046",
      name = Some(""),
      func = Some(List()),
      image = Some("47"),
      index = Some(46),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000047",
      name = Some(""),
      func = Some(List()),
      image = Some("48"),
      index = Some(47),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000048",
      name = Some(""),
      func = Some(List()),
      image = Some("49"),
      index = Some(48),
      `type` = Some("function"),
      commandId = Some("function")
    ),
    FuncToken(
      created_id = "2000049",
      name = Some(""),
      func = Some(List()),
      image = Some("50"),
      index = Some(49),
      `type` = Some("function"),
      commandId = Some("function")
    )
  )
}
