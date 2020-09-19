package pl.renesans.renesans.search.recycler

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl

class SearchPresenterImpl(private val realmDao: RealmContract.RealmDao,
                          private val imageDao: ImageDaoContract.ImageDao,
                          private val view: SearchContract.SearchView):
    SearchContract.SearchPresenter, ImageDaoContract.ImageDaoInterractor {

    private var articlesList = getSearchedArticles()
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
        view.startArticleActivity(articlesList[pos].objectId!!)
    }

    override fun deleteItemClicked(pos: Int) {
        realmDao.deleteItemFromLocalArticlesList(RealmDaoImpl.SEARCH_HISTORY, articlesList[pos].objectId!!)
        articlesList = getSearchedArticles()
        view.viewDeletedAtPos(pos)
    }

    override fun getItemCount() = articlesList.size

    override fun onBindViewHolder(holder: SearchRowHolder, position: Int) {
        resetHolderVariables(holder)
        setupSearchRowHolder(holder, position)
        loadPhoto(position)
    }

    private fun loadPhoto(position: Int) =
        imageDao.loadPhoto(position, articlesList[position].objectId + "_0", false)

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

    private fun resetHolderVariables(holder: SearchRowHolder){
        with(holder) {
            setSearchTitle(" ")
            setOnClickListener(0)
            setOnDeleteViewClickListener(0)
            setVisibilityOfDeleteBtn(View.GONE)
            setSearchDrawablePhoto(view.getSearchDefaultDrawable())
        }
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) =
        holders[pos]!!.setSearchUriPhoto(photoUri)

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) =
        holders[pos]!!.setSearchBitmapPhoto(photoBitmap)
}