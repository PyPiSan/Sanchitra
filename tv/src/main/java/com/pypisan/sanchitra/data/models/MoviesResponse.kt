import kotlinx.serialization.Serializable

@Serializable
data class MoviesResponseItem(
    val id: String,
    val videoUri: String,
    val subtitleUri: String?,
    val rank: Int,
    val rankUpDown: String,
    val title: String,
    val fullTitle: String,
    val year: Int,
    val releaseDate: String,
    val image_16_9: String,
    val image_2_3: String,
    val runtimeMins: Int,
    val runtimeStr: String,
    val plot: String,
    val contentRating: String,
    val rating: Float,
    val ratingCount: Int,
    val metaCriticRating: Int,
    val genres: String,
    val directors: String,
    val stars: String,
)
