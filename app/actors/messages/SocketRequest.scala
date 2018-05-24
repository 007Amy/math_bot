package actors.messages

case class SocketRequest(
                          steps : Option[Int], // Maximum number of client frames for the compiler to respond with
                          problem : Option[String],  // The encrypted problem the user is solving
                          halt : Option[Boolean],  // Stops or destroys the compiler
                          create: Option[Boolean],  // When true, creates a new compiler to run the program
                          exitOnSuccess: Option[Boolean] // When true, checks the problem on every frame to support problems that teach recursive non-terminating loops.
                        )
