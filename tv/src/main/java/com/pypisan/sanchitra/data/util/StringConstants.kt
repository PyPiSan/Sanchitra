package com.pypisan.sanchitra.data.util

object StringConstants {

    object API {
        const val TVURL = "https://media.pypisan.com/api/v1/"
        const val UIURL = "https://sanchitra.pypisan.com/"

        const val VIDEOURL = "https://tv.pypisan.com/api/v1/"
    }

    object Profile {
        var accountsEmail: String? = null
        var userName: String? = null
        var userProfileName: String? = null
        var userProfilePicture: String? = null
        var userSelectedLanguage: List<String> = emptyList()
    }

    object Utils {
        var LanguageSectionItems: List<String> = emptyList()
    }

    object Movie {
        const val StatusReleased = "Released"
        const val BudgetDefault = "$100M"
        const val WorldWideGrossDefault = "$720M"

        object Reviewer {
            const val RottenTomatoes = "Rotten Tomatoes"
            const val RottenTomatoesScore = "89%"
            const val RottenTomatoesReviewCount = "125"
            const val RottenTomatoesImageUrl = "https://bm3urmmijtko.objectstorage.ap-mumbai-1.oci.customer-oci.com/n/bm3urmmijtko/b/pypisan/o/movies/rt.png"
            const val IMDBReviewerName = "IMDB"
            const val IMDBImageUrl = "https://bm3urmmijtko.objectstorage.ap-mumbai-1.oci.customer-oci.com/n/bm3urmmijtko/b/pypisan/o/movies/imdb.png"
            const val IMDBDefaultCount = "1.8M"
            const val IMDBDefaultRating = "9.2"
        }
    }

    object Composable {
        object ContentDescription {
            fun moviePoster(movieName: String) = "Movie poster of $movieName"
            fun image(imageName: String) = "image of $imageName"
            const val MoviesCarousel = "Movies Carousel"
            const val UserAvatar = "User Profile Button"
            const val DashboardSearchButton = "Dashboard Search Button"
            const val BrandLogoImage = "Brand Logo Image"
        }
        const val HomeScreenTrendingTitle = "Trending Movies"
        fun reviewCount(count: String) = "$count reviews"

        object Placeholders {
            const val AboutSectionTitle = "About Sanchitra"
            const val AboutSectionDescription = "Welcome to Sanchitra! We are a new and" +
                    " exciting streaming platform that offers a vast selection of movies," +
                    " TV shows, and original content for you to enjoy. Our team is dedicated" +
                    " to providing an intuitive and seamless streaming experience for all" +
                    " users. With a simple and intuitive interface, you can easily find and" +
                    " watch your favourite content in just a few clicks. We are constantly" +
                    " updating and expanding our library, so there is always something new" +
                    " to discover. We also offer personalised recommendations based on your" +
                    " viewing history, so you can easily find new and exciting content to" +
                    " enjoy. Thank you for choosing Sanchitra for all of your entertainment" +
                    " needs. We hope you have a great time streaming!"
            const val AboutSectionAppVersionTitle = "Application Version"
            const val LanguageSectionTitle = "Language"
            const val SearchHistorySectionTitle = "Search history"
            const val SearchHistoryClearAll = "Clear All"
            val SampleSearchHistory = listOf(
                "The Light Knight",
                "Iceberg",
                "Jungle Gump",
                "The Devil father",
                "Space Wars",
                "The Lion Queen"
            )
            const val SubtitlesSectionTitle = "Settings"
            const val SubtitlesSectionSubtitlesItem = "Subtitles"
            const val SubtitlesSectionLanguageItem = "Subtitles Language"
            const val SubtitlesSectionLanguageValue = "English"
            const val AccountsSelectionSwitchAccountsTitle = "Switch profile"
            const val AccountsSelectionLogOut = "Log out"
            const val AccountsSelectionChangePasswordTitle = "Change password"
            const val AccountsSelectionChangePasswordValue = "••••••••••••••"
            const val AccountsSelectionAddNewProfile = "Add new profile"
            const val AccountsSelectionViewSubscriptionsTitle = "View subscriptions"
            const val AccountsSelectionDeleteAccountTitle = "Delete account"
            const val HelpAndSupportSectionTitle = "Help and Support"
            const val HelpAndSupportSectionListItemIconDescription = "select section"
            const val HelpAndSupportSectionFAQItem = "FAQ's"
            const val HelpAndSupportSectionPrivacyItem = "Privacy Policy"
            const val HelpAndSupportSectionContactItem = "Contact us on"
            const val HelpAndSupportSectionContactValue = "support@pypisan.com"
        }
        const val VideoPlayerControlClosedCaptionsButton = "Playlist Button"

        const val VideoPlayerControlInfoButton = "Playlist Button"

        const val VideoPlayerControlAudioSelectionButton = "Playlist Button"
        const val VideoPlayerControlSettingsButton = "Playlist Button"
        const val VideoPlayerControlPlayPauseButton = "Playlist Button"
        const val VideoPlayerControlSkipNextButton = "Skip to the next movie"
        const val VideoPlayerControlSkipPreviousButton = "Skip to the previous movie"
        const val VideoPlayerControlRepeatButton = "Repeat Button"
    }
}
