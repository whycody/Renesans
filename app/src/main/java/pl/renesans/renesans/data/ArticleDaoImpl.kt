package pl.renesans.renesans.data

import com.google.android.gms.maps.model.LatLng
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment

class ArticleDaoImpl: ArticleDao {

    override fun getRelatedArticlesList(article: Article): List<Article> {
        val relatedArticles = mutableListOf<Article>()
        if(articleHasSources(article))
            relatedArticles.add(Article(objectType = DiscoverRecyclerFragment.SOURCES,
                title = "Źródła", objectId = "Z0"))
        if(article.objectId != "O4") relatedArticles.add(0, getArticleFromId("O4"))
        article.listOfRelatedArticlesIds?.forEach { articleId ->
            relatedArticles.add(0, getArticleFromId(articleId))
        }
        return relatedArticles
    }

    override fun articleHasSources(article: Article): Boolean {
        if (article.source != null) return true
        if (article.listOfPhotos != null)
            article.listOfPhotos!!.forEach { photo -> if (photo.source != null) return true }
        return false
    }

    private fun getArticleFromId(objectId: String): Article{
        val articlesList = getArticlesList(getObjectTypeFromObjectId(objectId))
        return articlesList.find { it.objectId == objectId } ?: Article()
    }

    private fun getObjectTypeFromObjectId(objectID: String): Int{
        return when(objectID.first()){
            'P' -> 0
            'A' -> 1
            'E' -> 2
            'O' -> 3
            else -> 4
        }
    }

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
        articlesList.add(Article
            (title = "Mikołaj Kopernik",
            objectId = "P0",
            source = Source(srcDescription = "Treść główna", page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Miko%C5%82aj_Kopernik"),
            header = Header(content = listOf(Pair("Profesje","badacz, astronom, lekarz"), Pair("Lata życia", "1473 - 1543"))),
            listOfPhotos = listOf(Photo(objectId = "P0_0", description = "Mikołaj Kopernik", source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Miko%C5%82aj_Kopernik")),
                Photo(objectId = "P0_1", description = "Fragment \"O obrotach sfer niebieskich\", model heliocentryczny", numberOfParagraph = 1, source = Source(page = TVP_INFO, url = "https://www.tvp.info/12865831/przez-dwa-dni-mozna-ogladac-dzielo-kopernika"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Historia życia", content = "Mikołaj Kopernik to polski astronom, który swoją sławę zawdzięcza przede wszystkim swojemu dziełu \"O obrotach sfer niebieskich\" w którym szczegółowo przedstawił heliocentryczną wizję Wszechświata."),
                Paragraph(content = "Należy w tym miejscu wspomnieć, że koncepcja heliocentryzmu pojawiła się już w starożytnej Grecji, ale to właśnie dzieło Kopernika było przełomem w postrzeganiu naszej galaktyki."),
                Paragraph(subtitle = "Inne profesje", content = "Astronomia to dziedzina z której Kopernik był znany najbardziej, ale nie jedyna. Był renesansowym polihistorem, czyli osobą posiadającą rozległą wiedzę z wielu, różnych dziedzin. Interesował się matematyką, prawem, ekonomią, strategią wojskową czy też astrologią."))))
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
        articlesList.add(Article(title = "Renesans", objectId = "O4"))
        articlesList.add(Article(title = "Średniowiecze", objectId = "O0"))
        articlesList.add(Article(title = "Barok", objectId = "O1"))
        articlesList.add(Article(title = "Oświecenie", objectId = "O2"))
        articlesList.add(Article(title = "Romantyzm", objectId = "O3"))
        return articlesList
    }

    override fun getPhotoArticlesList(): List<PhotoArticle> {
        val photoArticles = mutableListOf<PhotoArticle>()
        photoArticles.add(PhotoArticle(title = "Wysoka Brama",
            objectId = "I0",
            source = Source(srcDescription = "Treść główna", page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Brama_G%C3%B3rna_w_Olsztynie"),
            latLng = LatLng(53.777574, 20.477587),
            paragraph = Paragraph(content = "Jest jedyną bramą pozostałą z trzech, które znajdowały się w murach obronnych otaczających miasto. Górna Brama powstała po wytyczeniu północnych granic miasta w drugim akcie lokacyjnym z 4 maja 1378. W roku 2012 prace archeologiczne odsłoniły pierwotne kamienne fundamenty bramy z XIV wieku, tuż na północ od jej obecnego położenia. Podczas wykopalisk odkryto ok. 40 monet z XIV i XV wieku oraz około 120 kul armatnich."),
            photo = Photo(objectId = "I0_0",
                description = "Brama Górna, Olsztyn",
                source = Source(page = ArticleDaoImpl.FOTOGRAFICZNIE16,
                    url = "http://fotograficznie16.rssing.com/chan-38531473/all_p11.html"))
        ))

        return photoArticles
    }

    companion object{
        const val WIKIPEDIA_PL = "pl.wikipedia.org"
        const val TVP_INFO = "tvp.info"
        const val FOTOGRAFICZNIE16 = "fotograficznie16.rssing.com"
    }
}