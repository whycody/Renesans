package pl.renesans.renesans.search.recycler

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import pl.renesans.renesans.R
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.data.realm.RealmDaoImpl

class SearchPresenterImpl(context: Context, private val searchView: SearchContract.SearchView):
    SearchContract.SearchPresenter, ImageDaoContract.ImageDaoInterractor {

    private val realmDao = RealmDaoImpl(context)
    private var articlesList = getSearchedArticles()
    private val imageDao = ImageDaoImpl(context, this)
    private val holders = hashMapOf<Int, SearchRowHolder>()
    private val converter = ArticleConverterImpl()

    override fun getSearchedArticles() =
        realmDao.getArticlesItemsFromLocalList(RealmDaoImpl.SEARCH_HISTORY)

    override fun getAllArticles() =
        converter.convertArticlesToArticleItemsList(realmDao.getAllArticles())

    override fun getCurrentArticlesList() = articlesList

    override fun setCurrentArticlesList(articlesList: List<ArticleItem>) {
        this.articlesList = articlesList
    }

    override fun itemClicked(pos: Int) {
        realmDao.addItemToLocalArticlesList(RealmDaoImpl.SEARCH_HISTORY, articlesList[pos].objectId!!)
        searchView.startArticleActivity(articlesList[pos].objectId!!)
    }

    override fun deleteItemClicked(pos: Int) {
        realmDao.deleteItemFromLocalArticlesList(RealmDaoImpl.SEARCH_HISTORY, articlesList[pos].objectId!!)
        articlesList = getSearchedArticles()
        searchView.viewDeletedAtPos(pos)
    }

    override fun getItemCount() = articlesList.size

    override fun onBindViewHolder(holder: SearchRowHolder, position: Int) {
        resetVariables(holder)
        setupSearchRowHolder(holder, position)
        imageDao.loadPhoto(position, articlesList[position].objectId + "_0", false)
    }

    private fun setupSearchRowHolder(holder: SearchRowHolder, position: Int) {
        with(holder) {
            holders[position] = this
            setSearchTitle(articlesList[position].title!!)
            setOnClickListener(position)
            setOnDeleteViewClickListener(position)
            if(articlesList[position].searchHistoryItem)
                setVisibilityOfDeleteBtn(View.VISIBLE)
        }
    }

    private fun resetVariables(holder: SearchRowHolder){
        with(holder) {
            setSearchTitle(" ")
            setOnClickListener(0)
            setOnDeleteViewClickListener(0)
            setVisibilityOfDeleteBtn(View.GONE)
            setSearchDrawablePhoto(ContextCompat
                .getDrawable(context, R.drawable.sh_search_recycler_row)!!)
        }
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) =
        holders[pos]!!.setSearchUriPhoto(photoUri)

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) =
        holders[pos]!!.setSearchBitmapPhoto(photoBitmap)
}