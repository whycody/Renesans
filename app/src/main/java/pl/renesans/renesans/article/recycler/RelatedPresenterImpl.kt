package pl.renesans.renesans.article.recycler

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.*
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment

class RelatedPresenterImpl(val context: Context, val article: Article):
    RelatedContract.RelatedPresenter, ImageDaoContract.ImageDaoInterractor {

    private lateinit var relatedArticlesList: List<Article>
    private var imageDao: ImageDaoContract.ImageDao? = null
    private val holders: MutableList<RelatedRowHolder> = mutableListOf()

    override fun onCreate() {
        val articleDao = ArticleDaoImpl()
        relatedArticlesList = articleDao.getRelatedArticlesList(article)
        imageDao = ImageDaoImpl(context, this)
    }

    override fun itemClicked(pos: Int) {
        if(relatedArticlesList[pos].objectType!=DiscoverRecyclerFragment.SOURCES){
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ArticleActivity.ARTICLE, relatedArticlesList[pos])
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return relatedArticlesList.size
    }

    override fun onBindViewHolder(holder: RelatedRowHolder, position: Int) {
        resetVariables(holder)
        refreshHoldersList(holder, position)
        holder.setArticleTitle(relatedArticlesList[position].title!!)
        holder.setOnRowClickListener(position)
        imageDao?.loadPhotoInBothQualities(position, "${relatedArticlesList[position].objectId}_0")
    }

    private fun refreshHoldersList(holder: RelatedRowHolder, position: Int){
        if(holders.size-1<position || holders.isEmpty()) holders.add(position, holder)
        else holders[position] = holder
    }

    private fun resetVariables(holder: RelatedRowHolder){
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