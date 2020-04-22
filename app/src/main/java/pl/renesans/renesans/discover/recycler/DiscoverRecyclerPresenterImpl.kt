package pl.renesans.renesans.discover.recycler

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.converter.ArticleConverterImpl

class DiscoverRecyclerPresenterImpl(val objectType: Int, val context: Context):
    DiscoverRecyclerPresenter, ImageDaoContract.ImageDaoInterractor {

    private lateinit var articleDao: ArticleDao
    private var articlesList = listOf<ArticleItem>()
    private var imageDao: ImageDaoContract.ImageDao? = null
    private val holders: MutableList<DiscoverRowHolder> = mutableListOf()
    private val converter = ArticleConverterImpl()

    override fun onCreate(articleId: Int) {
        articleDao = ArticleDaoImpl()
        articlesList = converter.convertArticlesToArticleItemsList(articleDao.getArticlesList(articleId))
        imageDao = ImageDaoImpl(context, this)
    }

    override fun itemClicked(pos: Int) {
        val intent = Intent(context, ArticleActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE, articleDao.getArticleFromId(articlesList[pos].objectId!!))
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
       return articlesList.size
    }

    override fun onBindViewHolder(holder: DiscoverRowHolder, position: Int) {
        resetVariables(holder)
        refreshHoldersList(holder, position)
        holder.setArticleTitle(articlesList[position].title!!)
        holder.setArticlePhotoSize(objectType)
        holder.setOnRowClickListener(position)
        imageDao?.loadPhotoInBothQualities(position, "${articlesList[position].objectId!!}_0")
    }

    private fun refreshHoldersList(holder: DiscoverRowHolder, position: Int){
        if(holders.size-1<position || holders.isEmpty()) holders.add(position, holder)
        else holders[position] = holder
    }

    private fun resetVariables(holder: DiscoverRowHolder){
        holder.setArticleTitle(" ")
        holder.setOnRowClickListener(0)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        holders[pos].setArticleUriPhoto(photoUri)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        holders[pos].setArticleBitmapPhoto(photoBitmap)
    }
}