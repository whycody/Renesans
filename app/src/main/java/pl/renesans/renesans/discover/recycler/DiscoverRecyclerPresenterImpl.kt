package pl.renesans.renesans.discover.recycler

import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleDaoImpl
import pl.renesans.renesans.data.ImageDaoImpl

class DiscoverRecyclerPresenterImpl(val objectType: Int): DiscoverRecyclerPresenter {

    private var articlesList = listOf<Article>()
    private val imageDao = ImageDaoImpl()

    override fun onCreate(articleId: Int) {
        val articleDao = ArticleDaoImpl()
        articlesList = articleDao.getArticlesList(articleId)
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
        imageDao.getPhotoUriFromID(holder, "${articlesList[position].objectId!!}_0")
        // TODO set Photo
    }

    private fun resetVariables(holder: DiscoverRowHolder){
        holder.setArticleTitle(" ")
        holder.setOnRowClickListener(0)
    }
}