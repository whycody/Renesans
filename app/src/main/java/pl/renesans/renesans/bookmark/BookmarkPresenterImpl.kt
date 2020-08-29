package pl.renesans.renesans.bookmark

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.data.Bookmark
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.data.realm.bookmark.BookmarkDao
import pl.renesans.renesans.data.realm.bookmark.BookmarkDaoImpl
import pl.renesans.renesans.sources.SourcesContract
import pl.renesans.renesans.sources.SourcesRowHolder

class BookmarkPresenterImpl(context: Context,
                            private val bookmarkView: BookmarkContract.BookmarkView):
    SourcesContract.SourcesPresenter, BookmarkContract.BookmarkPresenter,
    ImageDaoContract.ImageDaoInterractor {

    private val imageDao: ImageDaoContract.ImageDao
    private val bookmarkDao: BookmarkDao
    private var bookmarksList = listOf<Bookmark>()
    private val holders = hashMapOf<Int, SourcesRowHolder>()
    private var mode: String

    init {
        imageDao = ImageDaoImpl(context, this)
        bookmarkDao = BookmarkDaoImpl(context)
        mode = getMode()
        bookmarksList = getBookmarksList()
        if(bookmarksList.isEmpty()) bookmarkView.showNoBookmarksView()
    }

    private fun getMode(): String {
        return if(!bookmarkDao.bookmarksAreAvailable()) BookmarkDaoImpl.NO_BOOKMARKS
        else if(bookmarkDao.mapBookmarksAreAvailable()) BookmarkDaoImpl.LISTS_MODE
        else BookmarkDaoImpl.ALL_ARTICLES_MODE
    }

    private fun getBookmarksList(): List<Bookmark> {
        return when(mode){
            BookmarkDaoImpl.LISTS_MODE -> bookmarkDao.getListsOfBookmarks()!!
            BookmarkDaoImpl.ALL_ARTICLES_MODE -> bookmarkDao.getAllBookmarks()!!
            BookmarkDaoImpl.PLACES_MODE -> bookmarkDao.getMapBookmarks()!!
            else -> listOf()
        }
    }

    override fun itemClicked(pos: Int) {
        if(mode == BookmarkDaoImpl.LISTS_MODE) {
            mode = bookmarksList[pos].mode!!
            bookmarksList = getBookmarksList()
            bookmarkView.notifyBookmarksDataSetChanged(mode)
        }else if(mode == BookmarkDaoImpl.ALL_ARTICLES_MODE) {
            bookmarkView.startArticleActivity(bookmarksList[pos].articleId!!)
        }else if(mode == BookmarkDaoImpl.PLACES_MODE) {
            bookmarkView.openPhotoBottomSheet(bookmarksList[pos].articleId!!)
        }
    }

    override fun getItemCount() = bookmarksList.size

    override fun onBindViewHolder(holder: SourcesRowHolder, position: Int) {
        resetVariables(holder)
        holders[position] = holder
        holder.setTitle(bookmarksList[position].bookmarkTitle!!)
        if(bookmarksList[position].bookmarkDescription != null)
            holder.setDescription(bookmarksList[position].bookmarkDescription!!)
        holder.setOnClickListener(position)
        imageDao.loadPhoto(position, bookmarksList[position].photoId!!)
    }

    private fun resetVariables(holder: SourcesRowHolder){
        holder.setTitle(" ")
        holder.setDescription(" ")
        holder.setOnClickListener(0)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        holders[pos]?.setSourceUriPhoto(photoUri)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        holders[pos]?.setSourceBitmapPhoto(photoBitmap)
    }

    override fun onBackPressed(): Boolean {
        return if(bookmarkDao.mapBookmarksAreAvailable() && mode != BookmarkDaoImpl.LISTS_MODE) {
            mode = BookmarkDaoImpl.LISTS_MODE
            bookmarksList = getBookmarksList()
            bookmarkView.notifyBookmarksDataSetChanged(mode)
            true
        }else false
    }

    override fun setMode(mode: String) {
        this.mode = mode
        bookmarksList = getBookmarksList()
    }

    override fun getCurrentMode() = mode

    override fun onResume() {
        if(mode == BookmarkDaoImpl.ALL_ARTICLES_MODE && !bookmarkDao.bookmarksAreAvailable())
            mode = BookmarkDaoImpl.NO_BOOKMARKS
        bookmarksList = getBookmarksList()
        bookmarkView.notifyBookmarksDataSetChanged(mode)
        if(bookmarksList.isEmpty()) bookmarkView.showNoBookmarksView()
    }
}