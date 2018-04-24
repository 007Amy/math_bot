object DeployKeysExample {

  val CONFIG_VARS = Map(
    "MONGO_URI" -> "FILL_ME_IN",
    "CRYPTO_SALT" -> "FILL_ME_IN",
    "PLAY_SECRET" -> "FILL_ME_IN"
  )

  val NAME = "FILL_ME_IN"

  val PROCESS_TYPES = Map(
    "web" -> "target/universal/stage/bin/mathacademy -Dhttp.port=$PORT"
  )
}