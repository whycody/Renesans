package pl.renesans.renesans.search.recycler

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.R
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.article.ArticleDao
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl

class SearchPresenterImpl(private val context: Context,
                          private val searchView: SearchContract.SearchView):
    SearchContract.SearchPresenter, ImageDaoContract.ImageDaoInterractor {

    private lateinit var articlesList: List<ArticleItem>
    private lateinit var imageDao: ImageDaoContract.ImageDao
    private lateinit var articleDao: ArticleDao
    private val holders: MutableList<SearchRowHolder> = mutableListOf()
    private val converter = ArticleConverterImpl()

    override fun onCreate() {
        articleDao = ArticleDaoImpl(context)
        articleDao.onCreate()
        articlesList = converter.convertArticlesToArticleItemsList(articleDao.getAllArticles())
        imageDao = ImageDaoImpl(context, this)
    }

    override fun getAllArticles(): List<ArticleItem> {
        return converter.convertArticlesToArticleItemsList(articleDao.getAllArticles())
    }

    override fun getCurrentArticlesList(): List<ArticleItem> {
        return articlesList
    }

    override fun setCurrentArticlesList(articlesList: List<ArticleItem>){
        this.articlesList = articlesList
    }

    override fun itemClicked(pos: Int) {
        searchView.startArticleActivity(articleDao.getArticleFromId(articlesList[pos].objectId!!))
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }

    override fun onBindViewHolder(holder: SearchRowHolder, position: Int) {
        resetVariables(holder)
        refreshHoldersList(holder, position)
        holder.setSearchTitle(articlesList[position].title!!)
        holder.setOnClickListener(position)
        val bitmap = imageDao.getBitmap(articlesList[position].objectId + "_0")
        if(bitmap!=null) holder.setSearchBitmapPhoto(bitmap)
        else imageDao.loadPhoto(position, articlesList[position].objectId + "_0",
            highQuality = false, bothQualities = false)
    }

    private fun refreshHoldersList(holder: SearchRowHolder, position: Int){
        if(holders.size-1<position || holders.isEmpty()) holders.add(position, holder)
        else holders[position] = holder
    }

    private fun resetVariables(holder: SearchRowHolder){
        holder.setSearchTitle(" ")
        holder.setOnClickListener(0)
        holder.setSearchDrawablePhoto(context.getDrawable(R.drawable.sh_search_recycler_row)!!)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        if(searchView.holderIsVisible(pos)) holders[pos].setSearchUriPhoto(photoUri)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(searchView.holderIsVisible(pos)) holders[pos].setSearchBitmapPhoto(photoBitmap)
    }
}