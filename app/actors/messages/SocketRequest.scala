package actors.messages

case class SocketRequest(steps : Option[Int], problem : Option[String], halt : Option[Boolean], create: Option[Boolean])
