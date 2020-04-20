package pl.renesans.renesans.search.recycler

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.data.*

class SearchPresenterImpl(private val context: Context,
                          private val searchView: SearchContract.SearchView):
    SearchContract.SearchPresenter, ImageDaoContract.ImageDaoInterractor {

    private lateinit var articlesList: List<Article>
    private lateinit var imageDao: ImageDaoContract.ImageDao
    private lateinit var articleDao: ArticleDao
    private val holders: MutableList<SearchRowHolder> = mutableListOf()

    override fun onCreate() {
        articleDao = ArticleDaoImpl()
        articlesList = articleDao.getAllArticles()
        imageDao = ImageDaoImpl(context, this)
    }

    override fun getAllArticles(): List<Article> {
        return articleDao.getAllArticles()
    }

    override fun getCurrentArticlesList(): List<Article> {
        return articlesList
    }

    override fun setCurentArticlesList(articlesList: List<Article>){
        this.articlesList = articlesList
    }

    override fun itemClicked(pos: Int) {
        searchView.startArticleActivity(articlesList[pos])
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }

    override fun onBindViewHolder(holder: SearchRowHolder, position: Int) {
        resetVariables(holder)
        refreshHoldersList(holder, position)
        holder.setSearchTitle(articlesList[position].title!!)
        holder.setOnClickListener(position)
        imageDao.loadPhotoInBothQualities(position, articlesList[position].objectId + "_0")
    }

    private fun refreshHoldersList(holder: SearchRowHolder, position: Int){
        if(holders.size-1<position || holders.isEmpty()) holders.add(position, holder)
        else holders[position] = holder
    }

    private fun resetVariables(holder: SearchRowHolder){
        holder.setSearchTitle(" ")
        holder.setOnClickListener(0)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        holders[pos].setSearchUriPhoto(photoUri)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        holders[pos].setSearchBitmapPhoto(photoBitmap)
    }
}