object DeployKeysExample {

  val CONFIG_VARS = Map(
    "AUTH0_DOMAIN" -> "FILL_ME_IN (or not)", // AUTH0 callback
    "AUTH0_ID" -> "FILL_ME_IN (or not)", // AUTH0 id
    "MONGODB_URI" -> "FILL_ME_IN (or not)", // MONGODB creds
    "CRYPTO_SALT" -> "FILL_ME_IN (or not)", // Salt for problem encryption
    "PLAY_SECRET" -> "FILL_ME_IN (or not)", // Play secret
    "DEPLOY_HOST" -> "FILL_ME_IN (or not)" // Same origin check for websockets
  )

  val NAME = "FILL_ME_IN (or not)"

  val PROCESS_TYPES = Map(
    "web" -> "target/universal/stage/bin/math_bot -Dhttp.port=$PORT"
  )
}
