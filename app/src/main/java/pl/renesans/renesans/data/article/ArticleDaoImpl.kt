package pl.renesans.renesans.data.article

import com.google.android.gms.maps.model.LatLng
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.converter.ArticleConverterImpl
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

    override fun getAllArticlesWithTextInTitle(text: String): List<Article> {
        val allFilteredArticles = getAllArticles()
        allFilteredArticles.filter { article ->  article.title!!.toLowerCase().contains(text.toLowerCase())}
        return allFilteredArticles
    }

    override fun getAllArticles(): List<Article> {
        val allArticles = mutableListOf<Article>()
        allArticles.addAll(getImportantPeoples())
        allArticles.addAll(getImportantArts())
        allArticles.addAll(getImportantEvents())
        allArticles.addAll(getOtherEras())
        allArticles.addAll(getArticlesOfPhotoArticlesList())
        return allArticles
    }

    override fun getArticleFromId(objectId: String): Article {
        val articlesList = getArticlesList(getObjectTypeFromObjectId(objectId))
        return articlesList.find { it.objectId == objectId } ?: Article()
    }

    override fun getObjectTypeFromObjectId(objectID: String): Int{
        return when(objectID.first()){
            'P' -> DiscoverRecyclerFragment.PEOPLE
            'A' -> DiscoverRecyclerFragment.ARTS
            'E' -> DiscoverRecyclerFragment.EVENTS
            'O' -> DiscoverRecyclerFragment.OTHER_ERAS
            'S' -> DiscoverRecyclerFragment.SOURCES
            else -> DiscoverRecyclerFragment.PHOTOS
        }
    }

    override fun getArticlesList(articleId: Int): List<Article> {
        return when(articleId){
            DiscoverRecyclerFragment.PEOPLE -> getImportantPeoples()
            DiscoverRecyclerFragment.ARTS -> getImportantArts()
            DiscoverRecyclerFragment.EVENTS -> getImportantEvents()
            DiscoverRecyclerFragment.PHOTOS -> getArticlesOfPhotoArticlesList()
            else -> getOtherEras()
        }
    }

    override fun getImportantPeoples(): List<Article> {
        val articlesList = mutableListOf<Article>()
        articlesList.add(Article
            (title = "Mikołaj Kopernik",
            objectId = "P0",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Miko%C5%82aj_Kopernik"),
            header = Header(content = listOf(Pair(PROFESSIONS,"badacz, astronom, lekarz"), Pair(LIVE_YEARS, "1473 - 1543"))),
            listOfPhotos = listOf(Photo(objectId = "P0_0", description = "Mikołaj Kopernik", source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Miko%C5%82aj_Kopernik")),
                Photo(objectId = "P0_1", description = "Fragment \"O obrotach sfer niebieskich\", model heliocentryczny", numberOfParagraph = 1, source = Source(page = TVP_INFO, url = "https://www.tvp.info/12865831/przez-dwa-dni-mozna-ogladac-dzielo-kopernika"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Historia życia", content = "Mikołaj Kopernik to polski astronom, który swoją sławę zawdzięcza przede wszystkim swojemu dziełu \"O obrotach sfer niebieskich\" w którym szczegółowo przedstawił heliocentryczną wizję Wszechświata."),
                Paragraph(content = "Należy w tym miejscu wspomnieć, że koncepcja heliocentryzmu pojawiła się już w starożytnej Grecji, ale to właśnie dzieło Kopernika było przełomem w postrzeganiu naszej galaktyki."),
                Paragraph(subtitle = "Inne profesje", content = "Astronomia to dziedzina z której Kopernik był znany najbardziej, ale nie jedyna. Był renesansowym polihistorem, czyli osobą posiadającą rozległą wiedzę z wielu, różnych dziedzin. Interesował się matematyką, prawem, ekonomią, strategią wojskową czy też astrologią."))))
        articlesList.add(Article
            (title = "Michał Anioł",
            objectId = "P1",
            source = Source(srcDescription = MAIN_TEXT, page = NIEZLA_SZTUKA, url = "https://niezlasztuka.net/artysta/michal-aniol/"),
            header = Header(content = listOf(Pair(PROFESSIONS, "rzeźbiarz, malarz, poeta, architekt"),
                Pair(LIVE_YEARS, "1475 - 1564"),
                Pair(NATIONALITY, "włoska"))),
            listOfRelatedArticlesIds = listOf("A1"),
            listOfPhotos = listOf(Photo(objectId = "P1_0", description = "Michał Anioł",
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Micha%C5%82_Anio%C5%82")),
                Photo(objectId = "P1_1", description = "Caprese Michelangelo", numberOfParagraph = 0,
                    source = Source(page = VISITTUSCANY, url = "https://www.visittuscany.com/en/destinations/caprese-michelangelo/"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Młodość", content = "Michał Anioł urodził się 6 marca 1475 w, znajdującym się we Włoszech, Caprese a zmarł 18 lutego 1564 w Rzymie. Włoski artysta epoki renesansu, który również, dzięki swoim nowatorskim dziełom jest uznawany za prekursora baroku. Obecnie jest stawiany na równi z Leonardem da Vinci, jako jeden z najwybitniejszych twórców swoich czasów."),
            Paragraph(subtitle = "Początki", content = "Wybitny rzeźbiarz pochodził z mieszczańskiej rodziny, która zaraz po jego narodzinach przeprowadziła się do Florencji. Żyjąc tam miał okazję uczyć się rzeźbiarskiego rzemiosła od florenckich twórców."),
            Paragraph(content = "Już w wieku 25 lat stworzył Pietę a 4 lata później wyrzeźbił 5,5 metrowego Dawida. Artysta nie postanowił jednak zatrzymać się na rzeźbach. Swoją wszechstronność udowodnił tworząc m.in. freski na sklepieniu w Kaplicy Sykstyńskiej."))))
        articlesList.add(Article(title = "Rafael Santi", objectId = "P2"))
        articlesList.add(Article(title = "Mikołaj Sęp Szarzyński", objectId = "P3"))
        return articlesList
    }

    override fun getImportantArts(): List<Article> {
        val articlesList = mutableListOf<Article>()
        articlesList.add(Article
            (title = "Mona Lisa",
            objectId = "A0",
            source = Source(srcDescription = MAIN_TEXT, page = NIEZLA_SZTUKA, url = "https://niezlasztuka.net/o-sztuce/leonardo-da-vinci-mona-lisa/"),
            header = Header(content = listOf(Pair(CREATEOR, "Leonardo da Vinci"),
                Pair(CREATE_YEAR, "1503 - 1519"),
                Pair(ART_PLACE, "Luwr, Paryż"))),
            listOfPhotos = listOf(Photo(objectId = "A0_0", description = "Mona Lisa",
                source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Mona_Lisa")),
                Photo(objectId = "A0_1", description = "Cesare Maccari, Leonardo portretuje Giocondę", numberOfParagraph = 2,
                source = Source(page = THE_ATLANTIC, url = "https://www.theatlantic.com/entertainment/archive/2019/05/leonardo-da-vinci-500-years-later-theories-still-abound/588757/"))),
            listOfParagraphs = listOf(Paragraph(content = "Portret ukazuje młodą kobietę siedzącą na krześle. Jej dłonie ułożone są na widocznej u dołu obrazu poręczy krzesła. Ma ciemne włosy przykryte przezroczystym welonem, i równie ciemne oczny, nad którymi, co ciekawe, nie widać brwi."),
                Paragraph(subtitle = "Zagadkowy uśmiech", content = "Nie da się przejść obok obrazu Mona Lisy nie zwracając uwagi na jej tajemniczy uśmiech. Być może gdyby nie on, nikt dziś nie pamiętałby o tym dziele a ten opis można byłoby przypasować do większości kobiecych portretów."),
                Paragraph(content = "Od czasu incydentu z kradzieżą Monsa Lisy, obraz jest jednym z najbardziej chronionych dzieł sztuki na świecie co przenosi się też na jego popularność. Zagadkowy uśmiech był przyczyną kolejnych domysłów. Vasari uważał, że podczas malowania modelka była zabawiana przez muzyków i klaunów."),
                Paragraph(content = "Portret Mona Lisy pod względem malarskim to portret doskonały, który mógł wyjść tylko spod pędzla geniusza, jakim z pewnością był da Vinci."))))
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

    private fun getArticlesOfPhotoArticlesList(): List<Article>{
        val articleConverter = ArticleConverterImpl()
        val photoArticles = getPhotoArticlesList()
        val articlesList = mutableListOf<Article>()
        photoArticles.forEach{ photoArticle ->
            articlesList.add(articleConverter.convertPhotoArticleToArticle(photoArticle))
        }
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
                source = Source(page = FOTOGRAFICZNIE16,
                    url = "http://fotograficznie16.rssing.com/chan-38531473/all_p11.html"))
        ))

        return photoArticles
    }

    companion object{
        const val MAIN_TEXT = "Treść główna"
        const val PROFESSIONS = "Profesje"
        const val LIVE_YEARS = "Lata życia"
        const val NATIONALITY = "Narodowość"
        const val CREATE_YEAR = "Data powstania"
        const val CREATEOR = "Twórca"
        const val ART_PLACE = "Miejsce przebywania"

        const val WIKIPEDIA_PL = "pl.wikipedia.org"
        const val TVP_INFO = "tvp.info"
        const val FOTOGRAFICZNIE16 = "fotograficznie16.rssing.com"
        const val NIEZLA_SZTUKA = "niezlasztuka.net"
        const val VISITTUSCANY = "visittuscany.com"
        const val BELLITUDO = "bellitudo.pl"
        const val THE_ATLANTIC = "theatlantic.com"
    }
}