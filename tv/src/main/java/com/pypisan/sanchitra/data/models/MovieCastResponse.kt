import kotlinx.serialization.Serializable

@Serializable
data class MovieCastResponseItem(
    val id: String,
    val characterName: String,
    val realName: String,
    val avatarUrl: String,
)
