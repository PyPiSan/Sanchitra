package com.pypisan.sanchitra.data.util

object StringConstants {

    object API {
        const val TVURL = "https://media.pypisan.com/api/v1/"
        const val VIDEOURL = "https://tv.pypisan.com/api/v1/"
        const val UIURL = "https://sanchitra.pypisan.com/"
    }

    object Profile {
        var accountsEmail: String? = null
        var userName: String? = null
        var userProfileName: String? = null
        var userProfilePicture: String? = null
        var userSelectedLanguage: List<String> = emptyList()
        var isSubtitleOn: Boolean = true
    }

    object ProfileExtended {
        var isPremium: Boolean = false
        var extToken: String? = null
        var extJToken: String? = null
        var extSSOToken: String? = null
        var subscriberId: String? = null
        var uniqueId: String? = null
        var drmLink: String? = null
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
            const val RottenTomatoesImageUrl =
                "https://bm3urmmijtko.objectstorage.ap-mumbai-1.oci.customer-oci.com/n/bm3urmmijtko/b/pypisan/o/movies/rt.png"
            const val IMDBReviewerName = "IMDB"
            const val IMDBImageUrl =
                "https://bm3urmmijtko.objectstorage.ap-mumbai-1.oci.customer-oci.com/n/bm3urmmijtko/b/pypisan/o/movies/imdb.png"
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

            const val Affirmative = "Yes"

            const val Negative = "No"
            const val ErrorMessage = "Wops, something went wrong…"

            const val LoginError = "Timed out, please try again"
            const val AboutSectionTitle = "About Sanchitra"
            const val AboutSectionDescription =
                "Welcome to Sanchitra! We are a new and" + " exciting streaming platform that offers a vast selection of movies," + " TV shows, and original content for you to enjoy. Our team is dedicated" + " to providing an intuitive and seamless streaming experience for all" + " users. With a simple and intuitive interface, you can easily find and" + " watch your favourite content in just a few clicks. We are constantly" + " updating and expanding our library, so there is always something new" + " to discover. We also offer personalised recommendations based on your" + " viewing history, so you can easily find new and exciting content to" + " enjoy. Thank you for choosing Sanchitra for all of your entertainment" + " needs. We hope you have a great time streaming!"
            const val AboutSectionAppVersionTitle = "Application Version"
            const val LanguageSectionTitle = "Languages"
            const val LanguageDialogSuccessTitle = "Language Updated"
            const val LanguageDialogSuccessMessage =
                "Your changes have been saved. They will only reflect when you logout and log in."
            const val LanguageDialogFailedTitle = "Language Update Failed"
            const val LanguageDialogFailedMessage =
                "Unable to update language preferences. Please try again."
            const val SearchHistorySectionTitle = "Search history"
            const val SearchHistoryClearAll = "Clear All"
            const val NoSearchHistory = "No Search History"
            const val SubtitlesSectionTitle = "Settings"
            const val SubtitlesSectionSubtitlesItem = "Subtitles"
            const val SubtitlesSectionLanguageItem = "Subtitles Language"
            const val SubtitlesSectionLanguageValue = "English"
            const val AccountsSelectionSwitchAccountsTitle = "Switch profile"

            const val AccountsSelectionSwitchAccountsMessage = "Are you sure you want to switch to another profile?"
            const val AccountsSelectionLogOut = "Log out"
            const val AccountsSelectionLogOutCancel = "Cancel"
            const val AccountsSelectionLogOutDescription = "Are you sure you want to logout from your account?"

            const val AccountsSelectionLogOutFailed = "Failed to logout. Please try again."
            const val AccountsSelectionChangePasswordTitle = "Change password"
            const val AccountsSelectionChangePasswordValue = "••••••••••••••"

            const val AccountsSelectionChangePasswordSuccess = "Password Updated Successfully. To login next time, use your new password"
            const val AccountsSelectionChangePasswordFailed = "Failed to update password. Please check your current password and try again."

            const val AccountsSelectionAddNewProfile = "Add new profile"

            const val AccountsSelectionAddNewProfileSuccess = "New profile Added Successfully, change profile to use new profile."

            const val AccountsSelectionAddNewProfileFailed = "Failed to add new profile. Please try again."
            const val AccountsSelectionViewSubscriptionsTitle = "View subscriptions"
            const val AccountsSelectionDeleteAccountTitle = "Delete account"

            const val AccountsSelectionDeleteAccountDescription =
                "By deactivating your account, you will no longer be able to sign in, your activity will be removed from Sanchitra and your profile preferences will go away. " + "\n\nDo you wish to continue?"
            const val AccountsSelectionDeleteAccountConfirm = "Yes, delete my account"
            const val AccountsSelectionDeleteAccountCancel = "No, keep it"

            const val AccountsSelectionDeleteAccountFailed = "Failed to delete account. Please try again."
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
