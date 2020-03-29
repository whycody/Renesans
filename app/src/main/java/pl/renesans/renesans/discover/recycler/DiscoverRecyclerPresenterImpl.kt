package pl.renesans.renesans.discover.recycler

import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleDaoImpl

class DiscoverRecyclerPresenterImpl(val objectType: Int): DiscoverRecyclerPresenter {

    private var articlesList = listOf<Article>()

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
        // TODO set Photo
    }

    private fun resetVariables(holder: DiscoverRowHolder){
        holder.setArticleTitle(" ")
        holder.setOnRowClickListener(0)
    }
}