package pl.renesans.renesans.article.recycler

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.FragmentManager
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment
import pl.renesans.renesans.map.MapBottomSheetDialog
import pl.renesans.renesans.sources.SourcesBottomSheetDialog
import pl.renesans.renesans.tour.TourActivity

class RelatedPresenterImpl(private val context: Context,
                           private val fragmentManager: FragmentManager,
                           private val article: Article):
    RelatedContract.RelatedPresenter, ImageDaoContract.ImageDaoInterractor {

    private val relatedArticlesList: List<Article>
    private val imageDao: ImageDaoContract.ImageDao
    private val holders = hashMapOf<Int, RelatedRowHolder>()

    init {
        val articleDao = ArticleDaoImpl(context)
        relatedArticlesList = articleDao.getRelatedArticlesList(article)
        imageDao = ImageDaoImpl(context, this)
    }

    override fun itemClicked(pos: Int) {
        when (relatedArticlesList[pos].objectType) {
            DiscoverRecyclerFragment.SOURCES ->
                SourcesBottomSheetDialog().newInstance(article).show(fragmentManager, "Sources")
            DiscoverRecyclerFragment.MAP ->
                MapBottomSheetDialog().newInstance(article).show(fragmentManager, "Map")
            DiscoverRecyclerFragment.TOUR ->
                startTourActivity()
            else -> startArticleActivity(pos)
        }
    }

    private fun startTourActivity(){
        val intent = Intent(context, TourActivity::class.java)
        intent.putExtra(TourActivity.TOUR, article.tour)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun startArticleActivity(pos: Int){
        val intent = Intent(context, ArticleActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE, relatedArticlesList[pos])
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun getItemCount() = relatedArticlesList.size

    override fun onBindViewHolder(holder: RelatedRowHolder, position: Int) {
        resetVariables(holder)
        setupRelatedRowHolder(holder, position)
        imageDao.loadPhoto(position, "${relatedArticlesList[position].objectId}_0")
    }

    private fun setupRelatedRowHolder(holder: RelatedRowHolder, position: Int) {
        with(holder) {
            holders[position] = this
            setArticleTitle(relatedArticlesList[position].title!!)
            setOnRowClickListener(position)
        }
    }

    private fun resetVariables(holder: RelatedRowHolder) {
        with(holder) {
            setArticleTitle(" ")
            setOnRowClickListener(0)
        }
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        holders[pos]?.setArticleUriPhoto(photoUri)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        holders[pos]?.setArticleBitmapPhoto(photoBitmap)
    }
}