package pl.renesans.renesans.data

import pl.renesans.renesans.discover.recycler.fragment.DiscoverRecyclerFragment

class ArticleDaoImpl: ArticleDao {

    override fun getArticlesList(articleId: Int): List<Article> {
        return when(articleId){
            DiscoverRecyclerFragment.PEOPLE -> getImportantPeoples()
            DiscoverRecyclerFragment.ARTS -> getImportantArts()
            DiscoverRecyclerFragment.EVENTS -> getImportantEvents()
            else -> getOtherEras()
        }
    }

    override fun getImportantPeoples(): List<Article> {
        val articlesList = mutableListOf<Article>()
        articlesList.add(Article(title = "Mikołaj Kopernik", objectId = "P0"))
        articlesList.add(Article(title = "Michał Anioł", objectId = "P1"))
        articlesList.add(Article(title = "Rafael Santi", objectId = "P2"))
        articlesList.add(Article(title = "Mikołaj Sęp Szarzyński", objectId = "P3"))
        return articlesList
    }

    override fun getImportantArts(): List<Article> {
        val articlesList = mutableListOf<Article>()
        articlesList.add(Article(title = "Mona Lisa", objectId = "A0"))
        articlesList.add(Article(title = "Dawid", objectId = "A1"))
        articlesList.add(Article(title = "Ostatnia Wieczerza", objectId = "A2"))
        articlesList.add(Article(title = "Narodziny Wenus", objectId = "A3"))
        return articlesList
    }

    override fun getImportantEvents(): List<Article> {
        val articlesList = mutableListOf<Article>()
        articlesList.add(Article(title = "Niewola awiniońska papieży", objectId = "E0"))
        articlesList.add(Article(title = "Wynalazek druku", objectId = "E1"))
        articlesList.add(Article(title = "Upadek cesarstwa bizantyjskiego", objectId = "E2"))
        articlesList.add(Article(title = "Odkrycie Ameryki", objectId = "E3"))
        return articlesList
    }

    override fun getOtherEras(): List<Article> {
        val articlesList = mutableListOf<Article>()
        articlesList.add(Article(title = "Średniowiecze", objectId = "O0"))
        articlesList.add(Article(title = "Barok", objectId = "O1"))
        articlesList.add(Article(title = "Oświecenie", objectId = "O2"))
        articlesList.add(Article(title = "Romantyzm", objectId = "O3"))
        return articlesList
    }
}