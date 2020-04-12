package pl.renesans.renesans.data

import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment

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
        articlesList.add(Article
            (title = "Mikołaj Kopernik",
            objectId = "P0",
            header = Header(content = listOf(Pair("Profesje","badacz, astronom, lekarz"), Pair("Lata życia", "1473 - 1543"))),
            listOfPhotos = listOf(Photo(objectId = "P0_1", describe = "Fragment \"O obrotach sfer niebieskich\", model heliocentryczny", numberOfParagraph = 1)),
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
        articlesList.add(Article(title = "Średniowiecze", objectId = "O0"))
        articlesList.add(Article(title = "Barok", objectId = "O1"))
        articlesList.add(Article(title = "Oświecenie", objectId = "O2"))
        articlesList.add(Article(title = "Romantyzm", objectId = "O3"))
        return articlesList
    }
}