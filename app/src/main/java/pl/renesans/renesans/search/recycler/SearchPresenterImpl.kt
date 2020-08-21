package pl.renesans.renesans.search.recycler

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import pl.renesans.renesans.R
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl

class SearchPresenterImpl(private val context: Context,
                          private val searchView: SearchContract.SearchView):
    SearchContract.SearchPresenter, ImageDaoContract.ImageDaoInterractor {

    private lateinit var articlesList: List<ArticleItem>
    private lateinit var imageDao: ImageDaoContract.ImageDao
    private lateinit var realmDao: RealmContract.RealmDao
    private val holders: MutableList<SearchRowHolder> = mutableListOf()
    private val converter = ArticleConverterImpl()

    override fun onCreate() {
        realmDao = RealmDaoImpl(context)
        articlesList = getSearchedArticles()
        imageDao = ImageDaoImpl(context, this)
    }

    override fun getSearchedArticles() = realmDao.getArticlesItemsFromSearchHistory()

    override fun getAllArticles() =
        converter.convertArticlesToArticleItemsList(realmDao.getAllArticles())

    override fun getCurrentArticlesList() = articlesList

    override fun setCurrentArticlesList(articlesList: List<ArticleItem>) {
        this.articlesList = articlesList
    }

    override fun itemClicked(pos: Int) {
        realmDao.addItemToSearchHistoryRealm(articlesList[pos].objectId!!)
        searchView.startArticleActivity(articlesList[pos].objectId!!)
    }

    override fun deleteItemClicked(pos: Int) {
        realmDao.deleteItemFromSearchHistoryRealm(articlesList[pos].objectId!!)
        articlesList = getSearchedArticles()
        searchView.viewDeletedAtPos(pos)
    }

    override fun getItemCount() = articlesList.size

    override fun onBindViewHolder(holder: SearchRowHolder, position: Int) {
        resetVariables(holder)
        refreshHoldersList(holder, position)
        holder.setSearchTitle(articlesList[position].title!!)
        holder.setOnClickListener(position)
        holder.setOnDeleteViewClickListener(position)
        if(articlesList[position].searchHistoryItem) holder.setVisibilityOfDeleteBtn(View.VISIBLE)
        imageDao.loadPhoto(position, articlesList[position].objectId + "_0", false)
    }

    private fun refreshHoldersList(holder: SearchRowHolder, position: Int){
        if(holders.size-1<position || holders.isEmpty()) holders.add(position, holder)
        else holders[position] = holder
    }

    private fun resetVariables(holder: SearchRowHolder){
        holder.setSearchTitle(" ")
        holder.setOnClickListener(0)
        holder.setOnDeleteViewClickListener(0)
        holder.setVisibilityOfDeleteBtn(View.GONE)
        holder.setSearchDrawablePhoto(context.getDrawable(R.drawable.sh_search_recycler_row)!!)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) =
        holders[pos].setSearchUriPhoto(photoUri)

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) =
        holders[pos].setSearchBitmapPhoto(photoBitmap)
}