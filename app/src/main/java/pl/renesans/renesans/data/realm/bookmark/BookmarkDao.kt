package pl.renesans.renesans.data.realm.bookmark

import pl.renesans.renesans.data.Bookmark

interface BookmarkDao {

    fun bookmarksAreAvailable(): Boolean

    fun mapBookmarksAreAvailable(): Boolean

    fun getListsOfBookmarks(): List<Bookmark>?

    fun getAllBookmarks(): List<Bookmark>?

    fun getMapBookmarks(): List<Bookmark>?
}