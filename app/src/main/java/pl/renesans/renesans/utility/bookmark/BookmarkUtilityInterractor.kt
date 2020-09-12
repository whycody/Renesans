package pl.renesans.renesans.utility.bookmark

interface BookmarkUtilityInterractor {

    interface BookmarkUtility {

        fun handleBookmarkOnClick()
    }

    interface BookmarkView {

        fun changeColorOfBookmark(active: Boolean)
    }
}