package pl.renesans.renesans.utility

interface BookmarkUtilityInterractor {

    interface BookmarkUtility {

        fun handleBookmarkOnClick()
    }

    interface BookmarkView {

        fun changeColorOfBookmark(active: Boolean)
    }
}