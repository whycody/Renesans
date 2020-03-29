package pl.renesans.renesans.discover.recycler

import android.content.Context
import pl.renesans.renesans.data.*

class DiscoverRecyclerPresenterImpl(val objectType: Int, val context: Context): DiscoverRecyclerPresenter {

    private var articlesList = listOf<Article>()
    private var imageDao: ImageDao? = null

    override fun onCreate(articleId: Int) {
        val articleDao = ArticleDaoImpl()
        articlesList = articleDao.getArticlesList(articleId)
        imageDao = ImageDaoImpl(context)
    }

    override fun itemClicked(pos: Int) {

    }

    override fun getItemCount(): Int {
       return articlesList.size
    }

    override fun onBindViewHolder(holder: DiscoverRowHolder, position: Int) {
        resetVariables(holder)
        holder.setArticleTitle(articlesList[position].title!!)
        holder.setArticlePhotoSize(objectType)
        imageDao?.loadPhotoToHolder(holder, "${articlesList[position].objectId!!}_0", false)
        imageDao?.loadPhotoToHolder(holder, "${articlesList[position].objectId!!}_0", true)
    }

    private fun resetVariables(holder: DiscoverRowHolder){
        holder.setArticleTitle(" ")
        holder.setOnRowClickListener(0)
    }
}