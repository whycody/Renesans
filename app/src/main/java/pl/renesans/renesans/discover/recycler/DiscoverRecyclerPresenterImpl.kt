package pl.renesans.renesans.discover.recycler

import pl.renesans.renesans.data.Article

class DiscoverRecyclerPresenterImpl(val objectType: Int): DiscoverRecyclerPresenter {

    private val articlesList = mutableListOf<Article>()

    override fun onCreate(articleId: Int) {
        articlesList.add(Article(title = "Mikołaj Kopernik"))
        articlesList.add(Article(title = "Michał Anioł"))
        articlesList.add(Article(title = "Rafael Santi"))
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