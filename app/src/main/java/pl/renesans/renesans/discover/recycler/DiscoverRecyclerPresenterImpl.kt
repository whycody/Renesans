package pl.renesans.renesans.discover.recycler

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl

class DiscoverRecyclerPresenterImpl(private val articleId: String,
                                    private val objectType: Int,
                                    private val context: Context):
    DiscoverContract.DiscoverRecyclerPresenter, ImageDaoContract.ImageDaoInterractor {

    private val realmDao: RealmContract.RealmDao
    private val imageDao: ImageDaoContract.ImageDao
    private val articlesList: List<ArticleItem>
    private val holders = hashMapOf<Int, DiscoverRowHolder>()

    init {
        realmDao = RealmDaoImpl(context)
        imageDao = ImageDaoImpl(context, this)
        articlesList = realmDao.getArticlesItemsFromListWithId(articleId)
    }

    override fun itemClicked(pos: Int) =
        startArticleActivity(realmDao.getArticleWithId(articlesList[pos].objectId!!))

    private fun startArticleActivity(article: Article) {
        val intent = Intent(context, ArticleActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE, article)
        context.startActivity(intent)
    }

    override fun getArticlesListTitle() = realmDao.getArticlesListWithId(articleId).name!!

    override fun getItemCount() = articlesList.size

    override fun onBindViewHolder(holder: DiscoverRowHolder, position: Int) {
        resetVariables(holder)
        setupDiscoverRowHolder(holder, position)
        imageDao.loadPhoto(position, "${articlesList[position].objectId!!}_0")
    }

    private fun setupDiscoverRowHolder(holder: DiscoverRowHolder, position: Int) {
        with(holder) {
            holders[position] = this
            setArticleTitle(articlesList[position].title!!)
            setArticlePhotoSize(objectType)
            setOnRowClickListener(position)
        }
    }

    private fun resetVariables(holder: DiscoverRowHolder) {
        with(holder) {
            setArticleTitle(" ")
            setOnRowClickListener(0)
            setArticleDrawablePhoto()
        }
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        holders[pos]?.setArticleUriPhoto(photoUri)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        holders[pos]?.setArticleBitmapPhoto(photoBitmap)
    }
}