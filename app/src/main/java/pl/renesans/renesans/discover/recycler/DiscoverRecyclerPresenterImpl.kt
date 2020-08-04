package pl.renesans.renesans.discover.recycler

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.article.ArticleDao
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl

class DiscoverRecyclerPresenterImpl(val objectType: Int, val context: Context):
    DiscoverRecyclerPresenter, ImageDaoContract.ImageDaoInterractor {

    private lateinit var articleDao: ArticleDao
    private var articlesList = listOf<ArticleItem>()
    private var imageDao: ImageDaoContract.ImageDao? = null
    private val holders = hashMapOf<Int, DiscoverRowHolder>()
    private lateinit var articleId: String

    override fun onCreate(articleId: String) {
        articleDao = ArticleDaoImpl(context)
        articleDao.onCreate()
        this.articleId = articleId
        articlesList = articleDao.getArticlesItemsList(articleId)
        imageDao = ImageDaoImpl(context, this)
    }

    override fun getArticlesListTitle() = articleDao.getArticlesListTitle(articleId)

    override fun itemClicked(pos: Int) {
        val intent = Intent(context, ArticleActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE,
            articleDao.getArticleFromId(articlesList[pos].objectId!!))
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
       return articlesList.size
    }

    override fun onBindViewHolder(holder: DiscoverRowHolder, position: Int) {
        resetVariables(holder)
        holders[position] = holder
        holder.setArticleTitle(articlesList[position].title!!)
        holder.setArticlePhotoSize(objectType)
        holder.setOnRowClickListener(position)
        imageDao?.loadPhoto(position, "${articlesList[position].objectId!!}_0")
    }

    private fun resetVariables(holder: DiscoverRowHolder){
        holder.setArticleTitle(" ")
        holder.setOnRowClickListener(0)
        holder.setArticleDrawablePhoto()
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        holders[pos]?.setArticleUriPhoto(photoUri)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        holders[pos]?.setArticleBitmapPhoto(photoBitmap)
    }
}