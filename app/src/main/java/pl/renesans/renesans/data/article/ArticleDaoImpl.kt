package pl.renesans.renesans.data.article

import android.content.Context
import pl.renesans.renesans.R
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment

class ArticleDaoImpl(private val context: Context? = null): ArticleDao {

    private val realmDao =
        if(context!=null) RealmDaoImpl(context)
        else null

    override fun getRelatedArticlesList(article: Article): List<Article> {
        val relatedArticles = mutableListOf<Article>()
        if(article.tour!=null)
            relatedArticles.add(Article(objectType = DiscoverRecyclerFragment.TOUR,
                title = context?.getString(R.string.tour), objectId = "Z3"))
        if(article.listOfPositions != null && article.listOfPositions?.size!! > 0)
            relatedArticles.add(Article(objectType = DiscoverRecyclerFragment.MAP,
                title = context?.getString(R.string.position), objectId = "Z4"))
        article.listOfRelatedArticlesIds?.forEach { relatedArticles.add(realmDao!!.getArticleWithId(it)) }
        if(getObjectTypeFromObjectId(article.objectId!!) != DiscoverRecyclerFragment.OTHER_ERAS)
            relatedArticles.add(realmDao!!.getArticleWithId("O4"))
        addSourcesToRelatedArticles(relatedArticles, article)
        return relatedArticles
    }

    private fun addSourcesToRelatedArticles(relatedArticles: MutableList<Article>, article: Article) {
        if(articleHasSources(article))
            relatedArticles.add(Article(objectType = DiscoverRecyclerFragment.SOURCES,
                title = context?.resources?.getString(R.string.sources), objectId = "Z0"))
    }

    override fun articleHasSources(article: Article): Boolean {
        if (article.source?.url != null) return true
        if (article.listOfPhotos != null)
            article.listOfPhotos!!.forEach { photo -> if (photo.source != null) return true }
        return false
    }

    override fun getObjectTypeFromObjectId(objectID: String) = when(objectID.first()) {
            'P' -> DiscoverRecyclerFragment.PEOPLE
            'A' -> DiscoverRecyclerFragment.ARTS
            'E' -> DiscoverRecyclerFragment.EVENTS
            'O' -> DiscoverRecyclerFragment.OTHER_ERAS
            'S' -> DiscoverRecyclerFragment.SOURCES
            else -> DiscoverRecyclerFragment.PHOTOS
    }

    override fun getPhotoArticlesListBuiltToYear(year: Int): List<PhotoArticle> {
        val photoArticles = getPhotoArticlesList()
        val filteredPhotoArticles = mutableListOf<PhotoArticle>()
        for(article in photoArticles)
            if (article.objectType == CITY_TYPE &&
                filteredPhotoArticles.any{ articleItem -> articleItem.cityKey == article.cityKey})
                filteredPhotoArticles.add(article)
            else if (article.yearOfBuild != null && article.yearOfBuild!! <= year)
                filteredPhotoArticles.add(article)
        return filteredPhotoArticles
    }

    override fun getPhotoArticlesListBuiltInYears(fromYear: Int, toYear: Int): List<PhotoArticle> {
        val photoArticles = getPhotoArticlesList()
        val filteredPhotoArticles = mutableListOf<PhotoArticle>()
        for(article in photoArticles)
            if (article.objectType == CITY_TYPE &&
                filteredPhotoArticles.any{ articleItem -> articleItem.cityKey == article.cityKey})
                filteredPhotoArticles.add(article)
            else if (article.yearOfBuild != null
                && article.yearOfBuild!! >= fromYear && article.yearOfBuild!! <= toYear)
                filteredPhotoArticles.add(article)
        return filteredPhotoArticles
    }

    override fun getPhotoArticlesList() = realmDao!!.getPhotoArticles()

    companion object{
        const val PLACE_TYPE = 10
        const val CITY_TYPE = 11
        const val BOOKMARK_TYPE = 12
    }
}