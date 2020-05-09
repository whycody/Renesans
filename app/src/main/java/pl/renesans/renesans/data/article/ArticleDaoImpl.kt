package pl.renesans.renesans.data.article

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment

class ArticleDaoImpl: ArticleDao {

    override fun getRelatedArticlesList(article: Article): List<Article> {
        val relatedArticles = mutableListOf<Article>()
        if(article.tour!=null)
            relatedArticles.add(Article(objectType = DiscoverRecyclerFragment.TOUR,
                title = "Interaktywny szlak", objectId = "Z3"))
        article.listOfRelatedArticlesIds?.forEach { articleId ->
            relatedArticles.add(getArticleFromId(articleId))
        }
        if(getObjectTypeFromObjectId(article.objectId!!) != DiscoverRecyclerFragment.OTHER_ERAS)
            relatedArticles.add(getArticleFromId("O4"))
        if(articleHasSources(article))
            relatedArticles.add(Article(objectType = DiscoverRecyclerFragment.SOURCES,
                title = "Źródła", objectId = "Z0"))
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
            header = Header(content = listOf(Pair(PROFESSIONS,"badacz, astronom, lekarz"),
                    Pair(LIVE_YEARS, "1473 - 1543"),
                    Pair(NATIONALITY, "polska"))),
            listOfPhotos = listOf(Photo(objectId = "P0_0", description = "Mikołaj Kopernik", source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Miko%C5%82aj_Kopernik")),
                Photo(objectId = "P0_1", description = "Fragment \"O obrotach sfer niebieskich\", model heliocentryczny", numberOfParagraph = 1, source = Source(page = TVP_INFO, url = "https://www.tvp.info/12865831/przez-dwa-dni-mozna-ogladac-dzielo-kopernika"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Historia życia", content = "Mikołaj Kopernik urodził się 19 lutego 1473 w Toruniu, a zmarł przed 21 maja 1543 we Fromborku. Jest to polski astronom, który swoją sławę zawdzięcza przede wszystkim swojemu dziełu „O obrotach sfer niebieskich”, w którym szczegółowo przedstawił heliocentryczną wizję Wszechświata. Dzieło dokonało przełomu i wywołało jedną z najważniejszych rewolucji naukowych od czasów starożytnych, którą nazwano potem przewrotem kopernikańskim."),
                Paragraph(content = "Należy w tym miejscu wspomnieć, że koncepcja heliocentryzmu pojawiła się już w starożytnej Grecji, ale to właśnie dzieło Kopernika było przełomem w postrzeganiu naszej galaktyki."),
                Paragraph(subtitle = "Inne profesje", content = "Astronomia to dziedzina z której Kopernik był znany najbardziej, ale nie jedyna. Był renesansowym polihistorem, czyli osobą posiadającą rozległą wiedzę z wielu, różnych dziedzin. Interesował się matematyką, prawem, ekonomią, strategią wojskową czy też astrologią.")),
            tour = Tour("Mikołaj Kopernik", listOf(PhotoArticle(objectId = "T0_0", lat = 53.009311, lng = 18.603962, title = "Toruń, miejsce urodzenia", paragraph = Paragraph(content = "19 lutego 1473 najprawdopodobniej urodził się Mikołaj Kopernik, w rodzinie kupca Mikołaja i Barbary pochodzącej z rodziny Watzenrode."), photo = Photo(description = "Dom Mikołaja Kopernika")),
                PhotoArticle(objectId = "T0_1", lat = 53.010546, lng = 18.605117, title = "Toruń, miejsce przeprowadzki (kamienica pierwsza od prawej)", paragraph = Paragraph(content = "W 1480 r. Kopernikowie przeprowadzili się z domu przy ulicy św. Anny, kamienica nazywana jest także Lazurową i stoi przy Rynku Staromiejskim 36."), photo = Photo(description = "Kamienica pod Lwem")),
                PhotoArticle(objectId = "T0_2", lat = 53.009381, lng = 18.606256, title = "Toruń", paragraph = Paragraph(content = "Najprawdopodobniej Mikołaj Kopernik ukończył pierwsze nauki - w szkole parafialnej przy kościele śś. Janów w Toruniu."), photo = Photo(description = "Bazylika katedralna")),
                PhotoArticle(objectId = "T0_3", lat = 50.060948, lng = 19.934106, title = "Kraków, miejsce studiów", paragraph = Paragraph(content = "Dzięki protekcji Łukasza Watzenrodego w roku 1491 Andrzej i Mikołaj Kopernikowie rozpoczęli studia."), photo = Photo(description = "Uniwersytet Jagielloński")),
                PhotoArticle(objectId = "T0_4", lat = 44.496206, lng = 11.354158, title = "Bolonia, miejsce studiów", paragraph = Paragraph(content = "Dzięki staraniom wuja Łukasza w 1496 r. rozpoczął studia prawnicze, wpisując się w styczniu 1497 r. do albumu nacji niemieckiej bolońskiego Uniwersytetu Jurystów."), photo = Photo(description = "Uniwersytet Boloński")),
                PhotoArticle(objectId = "T0_5", title = "Brak przydzielonego miejsca", paragraph = Paragraph(content = "20 października 1497 Kopernik pełnoprawnie objął kanonię warmińską, co zapewniło mu utrzymanie do końca życia."), photo = Photo(description = "Bazylika Metropolitalna")),
                PhotoArticle(objectId = "T0_6", lat = 41.902703, lng = 12.496248, title = "Podróż do Rzymu", paragraph = Paragraph(content = "W 1500 r. Mikołaj odbył wraz z bratem Andrzejem Kopernikiem podróż, gdzie wygłosił kilka prywatnych wykładów. Tam, w nocy z 5 na 6 listopada 1500, obserwował zaćmienie Księżyca."), photo = Photo(description = "Rzym")),
                PhotoArticle(objectId = "T0_7", lat = 45.406736, lng = 11.877444, title = "Padwa, miejsce studiów", paragraph = Paragraph(content = "Kopernik rozpoczął swoje kolejne studia, na kierunku medycyny. Najprawdopodobniej uzyskał stopień licencjata, który później pozwalał mu na prowadzenie praktyki lekarskiej."), photo = Photo(description = "Uniwersytet w Padwie")),
                PhotoArticle(objectId = "T0_8", lat = 54.125747, lng = 20.583082, title = "Lidzbark", paragraph = Paragraph(content = "W 1507 r., po kilku latach pobytu w Polsce z Łukaszem Watzenrode, astronom został na stałe skierowany, by wspierać swojego wuja."), photo = Photo(description = "Zamek biskupów warmińskich")),
                PhotoArticle(objectId = "T0_9", lat = 50.054637, lng = 19.935467, title = "Kraków", paragraph = Paragraph(content = "Najprawdopodobniej był obecny 7 stycznia 1507, na koronacji Zygmunta I Starego, natomiast w 1509 r. uczestniczył w sejmie krakowskim. Kopernik był angażowany do niemal wszelkich spraw dyplomatycznych, sądowych i administracyjnych."), photo = Photo(description = "Katedra wawelska")),
                PhotoArticle(objectId = "T0_10", lat = 54.356836, lng = 19.681768, title = "Frombork", paragraph = Paragraph(content = "W pierwszej połowie 1510 r. przeniósł się astronom opuszczając Lidzbark z powodu konfliktu między nim a wujem. Od 8 listopada 1510 do 8 listopada 1513 pełnił tam rolę kanclerza kapituły."), photo = Photo(description = "Zespół katedralny")),
                PhotoArticle(objectId = "T0_11", lat = 53.777832, lng = 20.474515, title = "Olsztyn", paragraph = Paragraph(content = "Rezydował Mikołaj po tym jak został administratorem dóbr kapituły. Sprawował ten urząd do 1519 r. Rok później został mianowany administratorem dóbr Olsztyna."), photo = Photo(description = "Zamek Kapituły Warmińskiej")),
                PhotoArticle(objectId = "T0_10",  lat = 54.356836, lng = 19.681768, title = "Frombork", paragraph = Paragraph(content = "Powrócił Kopernik, gdzie mógł spokojnie oddać się pracy naukowej. Nikt nie podzielał astronomicznych zainteresowań Mikołaja, był za to ceniony jako ekonomista i lekarz."), photo = Photo(description = "Zespół katedralny")),
                PhotoArticle(objectId = "T0_13",  lat = 49.451838, lng = 11.076397, title = "Norymberga, miejsce druku dzieła", paragraph = Paragraph(content = "W połowie 1542 r. Kopernik wysłał dzieło De revolutionibus do druku."), photo = Photo(description = "Norymberga")),
                PhotoArticle(objectId = "T0_14",  lat = 54.357551, lng = 19.682014, title = "Frombork, miejsce śmierci", paragraph = Paragraph(content = "Kopernik zmarł przed 21 maja 1543, niemal w tym samym czasie, kiedy w Norymerdze wydrukowano epokowe dzieło De revolutionibus orbium coelestium."), photo = Photo(description = "Frombork")),
                PhotoArticle(objectId = "T0",  title = "Szlak Mikołaja Kopernika", paragraph = Paragraph(content = "Rozegrały się najważniejsze wydarzenia z życia Mikołaja Kopernika. Dziękujemy, że prześledziłeś te chwile z nami."), photo = Photo(description = "Europa")))
            )
        ))
        articlesList.add(Article
            (title = "Leonardo da Vinci",
            objectId = "P4",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Leonardo_da_Vinci"),
            header = Header(content = listOf(Pair(PROFESSIONS, "malarz, architekt, rzeźbiarz, wynalazca"),
                Pair(LIVE_YEARS, "1452 - 1519"),
                Pair(NATIONALITY, "włoska"))),
            listOfRelatedArticlesIds = listOf("A0", "A2", "P1", "P2"),
            listOfPhotos = listOf(Photo(objectId = "P4_0", description = "Leonardo da Vinci",
                    source = Source(page = FOCUS_PL, url = "https://www.focus.pl/artykul/choroba-wyczytana-z-reki-leonarda")),
                Photo(objectId = "P4_1", description = "Vinci, Włochy", numberOfParagraph = 0,
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Vinci_(W%C5%82ochy)")),
                Photo(objectId = "P4_2", description = "Kodeks Hammera", numberOfParagraph = 3,
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Leonardo_da_Vinci"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Człowiek renesansu", content = "Leonardo da Vinci urodził się 15 kwietnia 1452 a zmarł 2 maja 1519. Miejsce jego urodzenia to Anchiano znajdujące się w gminie Vinci - stąd też wziął się jego przydomek. Studiował anatomię, matematykę, mechanikę, botanikę, a także optykę. Z tego wynika też jego bogaty zasób profesji wymienionych w nagłówku."),
                Paragraph(subtitle = "Uczeń przerasta mistrza", content = "Pochodzenie artysty uniemożliwiało mu zdobycie uniwersyteckiego wykształcenia, ale prawdopodobnie to dzięki kontaktom zawodowym ojca mógł zacząć się uczyć u cenionego malarza i rzeźbiarza Andreę del Verrocchia."),
                Paragraph(content = "Podczas nauki z Verrocchim mógł nauczyć się m.in. podstawowych zasad perspektywy i rysowania postaci, jednak z czasem, po kilku latach spędzonych w pracowni stał się dla nauczyciela równorzędnym kolegą."),
                Paragraph(subtitle = "Notatki lustrzane", content = "Leonardo, dążąc do doskonałości w swoich dziełach, prowadził bardzo szczegółowe notatki, w których zawierał nie tylko obserwacje naukowe i artystyczne, ale także osobiste przemyślenia. Sporządzał je pismem lustrzanym, co prawdopodobnie wynikało z jego leworęczności. Dzięki takiemu sposobowi pisania nie rozmazywał atramentu."),
                Paragraph(content = "Do dziś wiadomo o istnieniu o ok. 7000 stron notatek. Większość znajduje się w publicznych zbiorach, część należy do prywatnych kolekcji. Jedną z najbardziej znanych pozycji jest \"Kodeks Hammera\" kupiony w 1994 r. przez Billa Gateasa za 30 800 000 \$."))))
        articlesList.add(Article
            (title = "Michał Anioł",
            objectId = "P1",
            source = Source(srcDescription = MAIN_TEXT, page = NIEZLA_SZTUKA, url = "https://niezlasztuka.net/artysta/michal-aniol/"),
            header = Header(content = listOf(Pair(PROFESSIONS, "rzeźbiarz, malarz, poeta, architekt"),
                Pair(LIVE_YEARS, "1475 - 1564"),
                Pair(NATIONALITY, "włoska"))),
            listOfRelatedArticlesIds = listOf("A1", "P4", "P2"),
            listOfPhotos = listOf(Photo(objectId = "P1_0", description = "Michał Anioł",
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Micha%C5%82_Anio%C5%82")),
                Photo(objectId = "P1_1", description = "Caprese Michelangelo", numberOfParagraph = 0,
                    source = Source(page = VISITTUSCANY, url = "https://www.visittuscany.com/en/destinations/caprese-michelangelo/"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Młodość", content = "Michał Anioł urodził się 6 marca 1475 w, znajdującym się we Włoszech, Caprese a zmarł 18 lutego 1564 w Rzymie. Włoski artysta epoki renesansu, który również, dzięki swoim nowatorskim dziełom jest uznawany za prekursora baroku. Obecnie jest stawiany na równi z Leonardem da Vinci, jako jeden z najwybitniejszych twórców swoich czasów."),
            Paragraph(subtitle = "Początki", content = "Wybitny rzeźbiarz pochodził z mieszczańskiej rodziny, która zaraz po jego narodzinach przeprowadziła się do Florencji. Żyjąc tam miał okazję uczyć się rzeźbiarskiego rzemiosła od florenckich twórców."),
            Paragraph(content = "Już w wieku 25 lat stworzył Pietę a 4 lata później wyrzeźbił 5,5 metrowego Dawida. Artysta nie postanowił jednak zatrzymać się na rzeźbach. Swoją wszechstronność udowodnił tworząc m.in. freski na sklepieniu w Kaplicy Sykstyńskiej."))))
        articlesList.add(Article
            (title = "Rafael Santi",
            objectId = "P2",
            source = Source(srcDescription = MAIN_TEXT, page = NIEZLA_SZTUKA, url = "https://niezlasztuka.net/o-sztuce/rafael-santi-madonna-sykstynska/"),
            header = Header(content = listOf(Pair(PROFESSIONS, "malarz, architekt"),
                Pair(LIVE_YEARS, "1483 - 1520"),
                Pair(NATIONALITY, "włoska"))),
            listOfRelatedArticlesIds = listOf("P4", "P1"),
            listOfPhotos = listOf(Photo(objectId = "P2_0", description = "Rafael Santi",
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Rafael_Santi")),
                Photo(objectId = "P2_1", description = "Urbino, Włochy", numberOfParagraph = 0,
                    source = Source(page = THETIMES_CO_UK, url = "https://www.thetimes.co.uk/article/step-back-in-time-in-urbino-italys-secret-renaissance-city-bqntlwc82")),
                Photo(objectId = "P2_2", description = "Dzieła Rafaela Santi", numberOfParagraph = 3,
                    source = Source(page = RADIO90_PL, url="https://www.radio90.pl/wirtualna-wystawa-dziel-rafaela-santi.html"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Doceniany za życia", content = "Rafael (właściwie Raffaello Santi lub też Raffaello Sanzio) urodził się w 6 kwietnia 1483 roku w Urbino a zmarł tego samego dnia 1520 roku w Rzymie. Już podczas swojego życia zdobył miano wszechstronnie utalentowanego człowieka, a przede wszystkim malarza. Słynny kronnikarz artystów, Giorgio Vasari wyraził swoją fascynację nim w następujących słowach: „Jak dalece niebo okazało się szczodre i błogosławione, obdarzając łaskawie tylko jednego człowieka swymi skarbami, dzielonymi przez wieki między wielu, to właśnie widzi się w osobie świetnego i pełnego wdzięku Rafaela Sanzio z Urbino”"),
                Paragraph(content = "Jest coś co wyróżniało Rafaela z czołówki najlepszych malarzy ówczesnej Europy. Artysta nie tylko tworzył swoje dzieła, ale też żywo był zainteresowany odkryciami archeologicznymi. Uznaje się go za jednego z pierwszych konserwatorów i obrońcę dzieł antycznych."),
                Paragraph(subtitle = "U progu wieku XVI", content = "Wraz z nadchodzącym nowym stuleciem, pod koniec lat 90. XV w., aktywność zawodowa malarza rosła razem z popularnością, wreszcie sławą. Obracał się głównie w środowisku uczonych i intelektualistów. Zaczął podróżować tam, gdzie otrzymywał zlecenia - wolny od zobowiązań rodzinnych z powodu śmierci obu swoich rodziców."),
                Paragraph(content = "Sanzio był niezwykle pracowity. Nie ubierał się w znaną wówczas maskę kapryśnego artysty. Trzymał się powierzonych mu zadań i sumiennie z nich wywiązywał, co również dodawało mu chwały i klientów."),
                Paragraph(subtitle = "Wizyta w Wenecji", content = "Aby przybliżyć sobie dzieła tworzone przez równolegle żyjących artystów - Leonarda da Vinci oraz Michała Anioła, Rafael wybrał się w podróż do Florencji. Cały czas pracował nad swoim unikatowym, tak rozpoznawalnym dzisiaj, stylem."),
                Paragraph(content = "To właśnie jego zapał, konserwatywność, ale także to, że umiał wykroczyć poza oficjalne konwencje sprawiły, że jest uznawany za „księcia malarstwa”."))))
        articlesList.add(Article
            (title = "Mikołaj Sęp Szarzyński",
            objectId = "P3",
            source = Source(srcDescription = MAIN_TEXT, page = CULTURE_PL, url = "https://culture.pl/pl/tworca/mikolaj-sep-szarzynski"),
            header = Header(content = listOf(Pair(PROFESSIONS, "poeta"),
                Pair(LIVE_YEARS, "1550 - 1581"),
                Pair(NATIONALITY, "polska"))),
            listOfPhotos = listOf(Photo(objectId = "P3_0", description = "Mikołaj Sęp Sarzyński",
                    source = Source(page = CULTURE_PL, url = "https://culture.pl/pl/tworca/mikolaj-sep-szarzynski")),
                Photo(objectId = "P3_1", description = "Stare Miasto we Lwowie", numberOfParagraph = 1,
                    source = Source(page = PODROZE_ONET, url = "https://podroze.onet.pl/ciekawe/stare-miasto-we-lwowie-najwazniejsze-informacje/nj564rg")),
                Photo(objectId = "P3_2", description = "Strona z pierwszego wydania Rytmów abo wierszy polskich z 1601", numberOfParagraph = 3,
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Rytmy_abo_wiersze_polskie"))),
            listOfParagraphs = listOf(Paragraph(content = "Mikołaj Sęp Szarzyński prawdopodobnie urodził się około roku 1550 (dokładna data nie jest znana) w Zimnej Wodzie we Lwowie. Jest uznawany za jednego z najważniejszych autorów późnego renesansu bądź początków baroku i równolegle omawiany w podręcznikach akademickich dotyczących obydwu epok."),
                Paragraph(content = "Na temat jego życia można mieć jedynie pewność co do kilku faktów z jego biografii, takich jak jego data urodzenia, czy śmierci. Z dokumentów wynika też, że kilka razy stawał przed sądem w sprawach majątkowych, reprezentując ojca."),
                Paragraph(subtitle = "Wykształcenie", content = "Pisarz swoją edukację zaczął w rodzinnym Lwowie gdzie nauczył się czytania i pisania, greki i łaciny. Później zaczął studiować w Writemberdze, na protestanckim uniwersytecie, a następnie w Lipsku. Przyjaźnił się z protestancką rodziną Starzechowskich, skąd domysły o jego zmianie wyznania."),
                Paragraph(subtitle = "Twórczość", content = "Jego twórczość należy do nurtu poezji metafizycznej. W swoich wierszach porusza tematy sensu życia i wiary. Zgłębia się w tajniki ludzkiej natury, wyraża obawy, wynikające ze świadomości przemijania i niedoskonałości."),
                Paragraph(content = "Do dzisiejszych czasów przetrwało niewiele jego utworów. Zachowały się utwory ze zbioru \"Rytmy abo wiersze polskie\", który wydał, dwadzieścia lat po jego śmierci, jego brat.",
                    source = Source(srcDescription = MAIN_TEXT, photoId = "Z2_0", page = POLSKINA5_PL, url = "https://www.polskina5.pl/mikolaj_sep_szarzynski")))))
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
            listOfRelatedArticlesIds = listOf("P4"),
            listOfPhotos = listOf(Photo(objectId = "A0_0", description = "Mona Lisa",
                source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Mona_Lisa")),
                Photo(objectId = "A0_1", description = "Cesare Maccari, Leonardo portretuje Giocondę", numberOfParagraph = 2,
                source = Source(page = THE_ATLANTIC, url = "https://www.theatlantic.com/entertainment/archive/2019/05/leonardo-da-vinci-500-years-later-theories-still-abound/588757/"))),
            listOfParagraphs = listOf(Paragraph(content = "Portret ukazuje młodą kobietę siedzącą na krześle. Jej dłonie ułożone są na widocznej u dołu obrazu poręczy krzesła. Ma ciemne włosy przykryte przezroczystym welonem, i równie ciemne oczny, nad którymi, co ciekawe, nie widać brwi."),
                Paragraph(subtitle = "Zagadkowy uśmiech", content = "Nie da się przejść obok obrazu Mona Lisy nie zwracając uwagi na jej tajemniczy uśmiech. Być może gdyby nie on, nikt dziś nie pamiętałby o tym dziele a ten opis można byłoby przypasować do większości kobiecych portretów."),
                Paragraph(content = "Od czasu incydentu z kradzieżą Monsa Lisy, obraz jest jednym z najbardziej chronionych dzieł sztuki na świecie co przenosi się też na jego popularność. Zagadkowy uśmiech był przyczyną kolejnych domysłów. Vasari uważał, że podczas malowania modelka była zabawiana przez muzyków i klaunów."),
                Paragraph(content = "Portret Mona Lisy pod względem malarskim to portret doskonały, który mógł wyjść tylko spod pędzla geniusza, jakim z pewnością był da Vinci."))))
        articlesList.add(Article
            (title = "Dawid",
            objectId = "A1",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Dawid_(rze%C5%BAba)"),
            listOfRelatedArticlesIds = listOf("P1"),
            header = Header(content = listOf(Pair(CREATEOR, "Michał Anioł"),
                Pair(CREATE_YEAR, "1501 - 1504"),
                Pair(ART_PLACE, "Galleria dell'Accademia, Florencja"))),
            listOfPhotos = listOf(Photo(objectId = "A1_0", description = "Dawid",
                    source = Source(page = LATIMES_COM, url = "https://www.latimes.com/entertainment/arts/culture/la-et-cm-michelangelo-david-statue-florence-20140502-story.html")),
                Photo(objectId = "A1_1", description = "Posąg Dawida w Galleria dell'Accademia", numberOfParagraph = 0,
                    source = Source(page = FAKTY_INTERA_PL, url = "https://fakty.interia.pl/nauka/news-naukowcy-rzezba-dawida-dluta-michala-aniola-zagrozona,nId,1418818"))),
            listOfParagraphs = listOf(Paragraph(content = "Rzeźba Michała Anioła przedstawia biblijnego Dawid bezpośrednio przed walką z Goliatem. Jest uznawana za jedno z najważniejszych dzieł renesansowej rzeźby. Artysta stworzył tą rzeźbę z dbałością o najmniejsze szczegóły, na Dawidze widać nawet naczynia krwionośne."),
                Paragraph(subtitle = "Przebieg pracy", content = "To od katedry florenckiej wiosną 1501 Michał dostał zlecenie na wykonanie rzeźby, którą zaczął 13 września. Do swojego dzieła wykorzystał złom marmuru. Podobno kiedy zobaczył kamień, powiedział: „Widzę rzeźbę, teraz muszę tylko odrzucić to, co zbędne”."),
                Paragraph(content = "Po ukończeniu, 8 września 1504 posąg stanął obok wejścia do Palazzo Vecchio we Florencji. Miał symbolizować wolność zdobytą przez mieszkańców miasta i ich gotowość do jej obrony. Dawid stał tam aż 369 lat, do 1873 roku. Obecnie stoi tam jego kopia."))))
        articlesList.add(Article
            (title = "Ostatnia Wieczerza",
            objectId = "A2",
            source = Source(srcDescription = MAIN_TEXT, page = NIEZLA_SZTUKA, url = "https://niezlasztuka.net/o-sztuce/ostatnia-wieczerza-leonarda-da-vinci/"),
            listOfRelatedArticlesIds = listOf("P4"),
            header = Header(content = listOf(Pair(CREATEOR, "Leonardo da Vinci"),
                Pair(CREATE_YEAR, "1495 - 1498"),
                Pair(ART_PLACE, "Muzeum Santia Maria delle Grazie, Mediolan"))),
            listOfPhotos = listOf(Photo(objectId = "A2_0", description = "Ostatnia Wieczerza",
                    source = Source(page = GALERIA_ZDJEC_COM, url = "https://galeria-zdjec.com/ostatnia-wieczerza-leonardo-da-vinci/")),
                Photo(objectId = "A2_1", description = "Kościół Santa Maria delle Grazie, Mediolan", numberOfParagraph = 1,
                    source = Source(page = GETYOURGUIDE_PL, url = "https://www.getyourguide.pl/discovery/-l4955/?utm_force=0"))),
            listOfParagraphs = listOf(Paragraph(content = "Ostatnia Wieczerza Leonarda da Vinci została namalowana w latach 1495-1498 na zlecenie księcia Mediolanu, Ludovico Sforzy. Zostało wykonane w jadali klasztoru dominikanów przy kościele Santa Maria delle Grazie w Mediolanie."),
                Paragraph(content = "Ukazuje scenę ostatniego wspólnego posiłku Chrystusa z apostołami przed pojmaniem na Górze Oliwnej i ukrzyżowaniem. Artysta ukazał tu moment tuż po tym, jak Jezus wypowiedział słowa: „Zaprawdę powiadam wam: jeden z was mnie zdradzi”. To zdanie wywołało duże poruszenie pośród apostołów, co postanowił uwiecznić Leonardo."),
                Paragraph(subtitle = "Technika Malarska", content = "Leonardo użył tutaj farb temperowych wymieszanych z olejnymi, które nakładał na zagruntowaną ścianę. To mu z kolei umożliwiło zadbać o każdy szczegół obrazu, bez koniecznego pośpiechu, który byłby konieczny przy zastosowaniu techniki fresku, w której specjalna farba odporna wymagała nakładania na mokry tynk."),
                Paragraph(content = "Niestety nowatorskie pomysły Leonarda z techniką malarską i stosowanymi farbami sprawiły, że Ostatnia Wieczerza z czasem zaczęła blaknąć i łuszczyć się już kilka lat po jej wykonaniu."))))
        articlesList.add(Article
            (title = "Narodziny Wenus",
            objectId = "A3",
            source = Source(srcDescription = MAIN_TEXT, page = NIEZLA_SZTUKA, url = "https://niezlasztuka.net/o-sztuce/sandro-botticelli-narodziny-wenus/"),
            header = Header(content = listOf(Pair(CREATEOR, "Sandro Botticelli"),
                Pair(CREATE_YEAR, "ok. 1485"),
                Pair(ART_PLACE, "Galeria Uffizi, Włochy"))),
            listOfPhotos = listOf(Photo(objectId = "A3_0", description = "Narodziny Wenus",
                    source = Source(page = NIEZLA_SZTUKA, url = "https://niezlasztuka.net/o-sztuce/sandro-botticelli-narodziny-wenus/")),
                Photo(objectId = "A3_1", description = "Cypr", numberOfParagraph = 1,
                    source = Source(page = DZIECKOWDORDZE_COM, url = "https://dzieckowdrodze.com/cypr-10-rzeczy-ktore-musisz-zobaczyc/"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Symbol Renesansu", content = "Narodziny Wenus włoskiego malarza Sandro Botticeli jest przykładem odrodzenia antycznych motywów mitologicznych w malarstwie wczesnego renesansu floranckiego. To właśnie dzieła o takiej tematyce tworzył Botticelli, stworzył również m.in. Wenus i Marsa."),
                Paragraph(content = "Nie została dokładnie ustalona data stworzenia dzieła, większość historyków sztuki wskazuje lata 1482 - 1485. Obraz został namalowany na tańszym materiale, płótnie, co może wskazywać na to, że miał być przeznaczony do rezydencji wiejskiej."),
                Paragraph(subtitle = "Kontekst mitologiczny", content = "Według greckiego poety Hezjoda bogini miłości Wenus powstała z piany morskiej, kiedy Krotos obciął swojemu ojcu genitalia i wrzucił je do oceanu, aby ukarać go za okrucieństwo. Wenus wynurzyła się spomiędzy fal i skierowała w stronę brzegu jednej z greckich wysp – Cypru lub Cytery."))))
        return articlesList
    }

    override fun getImportantEvents(): List<Article> {
        val articlesList = mutableListOf<Article>()
        articlesList.add(Article
            (title = "Niewola awiniońska papieży",
            objectId = "E0",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Niewola_awinio%C5%84ska"),
            header = Header(content = listOf(Pair(TIME, "1309 - 1377"),
                Pair(PLACE, "Awinion, Francja"))),
            listOfPhotos = listOf(Photo(objectId = "E0_0", description = "Niewola awiniońska papieży",
                    source = Source(page = POLITYKA_PL, url = "https://www.polityka.pl/pomocnikhistoryczny/1640215,1,papieze-w-niewoli.read")),
                Photo(objectId = "E0_1", description = "Gulden, Lubeka, 1341 rok", numberOfParagraph = 0,
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Gulden"))),
            listOfParagraphs = listOf(Paragraph(content = "Niewolą awiniońską papieży nazywa się okres rezydowania papieży w Awinionie - miasta położonego na południu Francji. Awinion stał się rezydencją papieży w 1309 roku, kiedy osiadł tam papież Klemens V wraz z Kurią. Dopiero jednak w roku 1348 roku stało się ich własnością, kiedy papież Klemens VI kupił je za 80 tys. złotych guldenów od królowej Joanny Neapolitańskiej. Rezydencja ostatniego papieża trwała aż do roku 1377 roku."),
                Paragraph(subtitle = "Wsparcie królów Francji", content = "Papieże korzystali ze wsparcia królów Francji, lecz byli też od nich uzależnieni i zagrożeni politycznie i militarnie ze strony silnej monarchii francuskiej. Dopiero Grzegorz XI, wsłuchując się w napomnienia św. Katarzyny Sieneńskiej, powrócił do Rzymu w 1377 roku."))))
        articlesList.add(Article
            (title = "Wynalazek druku",
            objectId = "E1",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Johannes_Gutenberg"),
            header = Header(content = listOf(Pair(CREATEOR, "Johannes Gutenberg"),
                    Pair(TIME, "ok. 1445"))),
            listOfPhotos = listOf(Photo(objectId = "E1_0", description = "Wynalazek druku",
                    source = Source(page = YOUTUBE_COM, url = "https://www.youtube.com/watch?v=CtDnTj8XGUM")),
                Photo(objectId = "E1_1", description = "Kaszta zecerska pełna czcionek", numberOfParagraph = 1,
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Czcionka"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Twórca", content = "Jan Gutenberg to niemiecki rzemieślnik, złotnik i drukarz. To on jest twórcą pierwszej przemysłowej metody druku na świecie. Data powstania pierwszych druków wciąż pozostaje dla nas zagadką, badacze spierają się, czy pierwsze druki powstały podczas jego pobytu w Strasburgu (1434 - 1444), czy dopiero w drukarni założonej przez niego w 1448 roku w Moguncji."),
                Paragraph(content = "Rok 1440 lub 1450 umownie uznaje się za rok, w którym po raz pierwszy Gutenberg użył swojej ruchomej czcionki. Jego najdoskonalszą i najpopularniejszą wydrukowaną publikacją jest Biblia Gutenberga, która została wydrukowana w latach 1452 - 1455."),
                Paragraph(subtitle = "Prędkość druku", content = "Dla dzisiejszych możliwości technicznych tempo w drukarni Gutenberga było bardzo powolne – wydrukowanie jednej strony zajmowało trzy dni. – Jeden dzień drukarz moczył papier, drugi drukował, a trzeci dzień suszył stronę, dopiero potem zadrukowane strony trafiały do oprawy, czasami nawet w ciągu 20 lat – wyjaśniał Tadeusz Sarocki."),
                Paragraph(content = "Jednak był to nadzwyczajny wynalazek biorąc pod uwagę, że średniowiecznemu mnichowi-skrybie przepisanie odręczne jednej księgi zabierało czasami nawet połowę życia, a księgi te trafiały tylko do najbogatszych – na dwory panujących.",
                    source = Source(photoId = "Z2_0", srcDescription = "Prędkość druku", page = POLSKIE_RADIO_PL, url = "https://www.polskieradio.pl/39/156/Artykul/1038182,Wynalazek-pana-Gutenberga")))))
        articlesList.add(Article
            (title = "Upadek cesarstwa bizantyjskiego",
            objectId = "E2",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Upadek_Konstantynopola#Bibliografia"),
            header = Header(content = listOf(Pair(TIME, "2 kwietnia - 29 maja 1453"),
                    Pair(PLACE, "Konstantynopol"))),
            listOfPhotos = listOf(Photo(objectId = "E2_0", description = "Upadek cesarstwa bizantyjskiego",
                    source = Source(page = ARCHIWUM_RP_PL, url = "https://archiwum.rp.pl/artykul/952943-Zmierzch-cesarstwa--bizantyjskiego.html")),
                Photo(objectId = "E2_1", description = "Mapa Konstantynopola", numberOfParagraph = 1,
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Upadek_Konstantynopola#Bibliografia"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Śmierć cesarza", content = "We wtorek 29 maja 1453 roku wojska Imperium Osmańskiego zajęły Konstantynopol. Zdobyte miasto stało się stolicą tego Imperium i pozostało nią do powstania republiki w 1923 roku. Zdobycie miasta oraz śmierć ostatniego cesarza bizantyjskiego Konstantyna XI pociągneły za sobą nieodwracalny już upadek Cesarstwa wschodniorzmskiego."),
                Paragraph(content = "Zwycięstwo Turków zapewniło im panowanie nad wschodnim basenem Morza Śródziemnego i otworzyło drogę do podboju Europy. Turcy zaczęli też stosować w odniesieniu do miasta nazwę Stambuł, co pochodzi od zniekształconego w ustach Turków greckiego wyrażenia i oznacza „do miasta”.",
                    source = Source(photoId = "Z2_0", srcDescription = "Pochodzenie nazwy Stambuł", page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Stambu%C5%82#Nazwa")),
                Paragraph(subtitle = "Dwie strony muru", content = "Armia turecka składała się z około 80 tysięcy regularnego wojska, około 20 tysięcy baszybuzuków (żołnierzy nieregularnego wojska tureckiego, pochodzących z ochotniczego naboru) oraz kilku tysięcy żołnierzy służ tyłowych i 70 dział."),
                Paragraph(content = "Wszystkich zdolnych do walki żołnierzy bizantyjskich było około 7 tysięcy, w tym 2 tysiące sprzymierzeńców. Pomoc dla Bizancjum zaoferował Giovanni Giustiniani Longo wystawiając około 700 ludzi oraz Katalonia."))))
        articlesList.add(Article
            (title = "Odkrycie Ameryki",
            objectId = "E3",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Odkrycie_Ameryki"),
            header = Header(content = listOf(Pair(EXPLORER, "Krzysztof Kolumb"),
                    Pair(DATE, "12 października 1492"))),
            listOfPhotos = listOf(Photo(objectId = "E3_0", description = "Odkrycie Ameryki",
                    source = Source(page = WOJCIECH_PIESTRAK_PL, url = "https://wojciechpiestrak.pl/odkrycie-ameryki-przez-kolumba/")),
                Photo(objectId = "E3_1", description = "Podróże Krzysztofa Kolumba", numberOfParagraph = 2,
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Odkrycie_Ameryki"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "„Odkrycie” Ameryki", content = "Bardzo popularne określenie faktu dotarcia Kolumba do Ameryki nazywa się odkryciem tego kontynentu. W rzeczywistości jednak jest to trochę naciągnięte. To mongoloidzi (ludność rasy żółtej) przybywając z Azji jako pierwsi dotarli do kontynentu amerykańskiego. Z kolei za pierwszego europejczyka, który dotarł do Ameryki, uznaje się Islandczyka Leifa Erikssona (na początku XI w.)."),
                Paragraph(subtitle = "Podróż do Indii", content = "Krzysztof Kolumb postawił sobie cel dotarcia do Indii, jednak zamierzał przepłynąć przez Ocean Atlantycki w kierunku zachodnim. Na podstawie dzieła greckiego geografa Ptolemeusza, przyjął, że Ziemia jest kulista. Po wielu latach w końcu udało mu się przekonać hiszpańską królową Izabelę Kastylijską do sfinansowania jego pomysłu i 3 sierpnia 1492 r. wyruszył w dziewiczy rejs przez Atlantyk."),
                Paragraph(content = "12 października trzy statki, z flagową „Santa Marią” na czele, dotarły do brzegów wyspy San Salvador, którą uznano za jedną z wysp japońskich. Po opłynięciu jeszcze kilku innych wysp wrócił do Hiszpanii, gdzie w Barcelonie został przyjęty z gorącym entuzjazmem. Z czasem zorganizował tam jeszcze kolejne trzy podróże. Tym razem dotarł do wybrzeży Wenezueli, Hondurasu i Panamy, które ponownie zostały uznane przez odkrywców za kontynent azjatycki."),
                Paragraph(content = "Dopiero 20 lat później podróżujący do Ameryki Południowej żeglarz Amerigo Vespucci stwierdził jako pierwszy, że odkryte przez Kolumba lądy to nie Azja, lecz nowy, nieznany do tego czasu ląd. Nazwano go od jego imienia Ameryką."))))
        return articlesList
    }

    override fun getOtherEras(): List<Article> {
        val articlesList = mutableListOf<Article>()
        articlesList.add(Article
            (title = "Renesans",
            objectId = "O4",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Renesans"),
            header = Header(content = listOf(Pair(TIME_FRAME_ITALY, "XIV w. - XVI w."),
                Pair(TIME_FRAME_EUROPE, "koniec XV w. - koniec XVI w."),
                Pair(TIME_FRAME_POLAND, "XVI w. - lata 30. XVII w."))),
            listOfRelatedArticlesIds = listOf("E2", "E3", "P0"),
            listOfPhotos = listOf(Photo(objectId = "O4_0", description = "Renesans",
                source = Source(page = LICEALISTA_PL, url = "http://licealista.pl/renesans/")),
                Photo(objectId = "O4_1", description = "Ostateczne zerwanie Marcina Lutra z Kościołem katolickim", numberOfParagraph = 2,
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Reformacja")),
                Photo(objectId = "P0_1", description = "Fragment \"O obrotach sfer niebieskich\", model heliocentryczny", numberOfParagraph = 3,
                    source = Source(page = TVP_INFO, url = "https://www.tvp.info/12865831/przez-dwa-dni-mozna-ogladac-dzielo-kopernika"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Znaczenie nazwy", content = "Termin „renesans” jest równoznaczny z terminem „odrodzenie”. Oba te słowa stosuje się wymiennie na określenie epoki w dziejach kultury. Pierwsze z nich, renesans, pochodzi od francuskiego słowa renaissance, użytego przez Jules’a Micheleta i Jakoba Burckhardta w połowie XIX w. Określenie renaissance przyjęło się w wielu językach europejskich, w tym francuskim, angielskim i niemieckim."),
                Paragraph(subtitle = "Ramy czasowe", content = "Początek epoki pokrywa się z końcem średniowiecza, dlatego za jej początek można uznać takie daty jak upadek Konstantynopola, koniec wojny stuletniej, czy odkrycie Ameryki. Ustalenie początku epoki renesansu utrudnia fakt, że do różnych krajów dotarł on w różnym czasie, a w żadnej dziedzinie kultury nie wyparł do końca form średniowiecznych."),
                Paragraph(content = "Równie sporna jest data końca renesansu. Często wymienia się „okrągłą” datę 1600, lecz również daty różnych wydarzeń, takich jak wystąpienie Lutra (1517), sobór trydencki (1548), czy koniec wojny trzydziestoletniej (1648)."),
                Paragraph(subtitle = "Odrodzenie sztuk i nauk", content = "Wiek XIX postrzegał odrodzenie jako epokę postępu naukowo-technicznego, wyrafinowanej kultury materialnej, indywidualizmu, a nawet ateizmu, przeciwstawiając go ostro średniowieczu. Renesans jest często określany jako „odrodzenie sztuk i nauk”. Nowa epoka oznaczała przede wszystkim inne spojrzenie na wszechświat. Dzięki m.in. odkryciu Kopernika zaczęto obdarzać większym zaufaniem naukę i rozum."),
                Paragraph(content = "Mniejszym zaufaniem z kolei obdarzono religię. Przez m.in. nurt reformacji zapoczątkowany przez Marcina Lutra pozycja Kościoła osłabła, co również rozbiło jedność religijną Europy. Należy również w kontekście renesansu wspomnieć o takich ważnych faktach jak odkrycia geograficzne, czy rozpowszechnienie druku."))))
        articlesList.add(Article
            (title = "Średniowiecze",
            objectId = "O0",
            source = Source(srcDescription = MAIN_TEXT, page = CIEKAWOSTKI_HISTORYCZNE_PL, url = "https://ciekawostkihistoryczne.pl/category/epoka/sredniowiecze/"),
            header = Header(content = listOf(Pair(TIME_FRAME_EUROPE, "V w. - XV w."))),
            listOfRelatedArticlesIds = listOf("E2"),
            listOfPhotos = listOf(Photo(objectId = "O0_0", description = "Średniowiecze",
                    source = Source(page = ALEKLASA_PL, url = "http://aleklasa.pl/category/liceum/c155-powtorka-z-epok-literackich/c159-sredniowiecze")),
                Photo(objectId = "O0_1", description = "Statek wikngów XX w, model 3D", numberOfParagraph = 1,
                    source = Source(page = MOZAWEB_COM, url = "https://www.mozaweb.com/pl/Extra-Modele_3D-Statek_Wikingow_X_wiek-45104"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Ramy czasowe", content = "Czas trwania epoki datuje się na od V wieku do XV. Za początek średniowiecza uważa się upadek cesarstwa zachodniorzymskiego, a za koniec takie wydarzeni jak upadek Konstantynopola (1453), czy wynalezienie druku. Jest środkowym okresem w podziale Europy na starożytność, średniowiecze i nowożytność. Sama również dzieli się na trzy etapy: średniowiecze wczesne, pełne (dojrzałe) i późne."),
                Paragraph(subtitle = "Nazwa epoki", content = "Nazwa epoki wywodzi się od łacińskiego zwrotu „media aetas”, co oznacza „wieki średnie”. Nadali ją tej epoce twórcy renesansowi na przełomie XV/XVI w. Miała ona sugerować pośredniość epoki pomiędzy tym, co autentyczne a tym, co renesansowe. Używano ją więc w sensie negatywnym. Oprócz tej nazwy funkcjonuje również metaforyczne okreslenie średniowiecza jako wieków ciemnych.",
                    source = Source(srcDescription = "Znaczenie nazwy epoki", photoId = "Z2_0", page = SCIAGA_PL, url = "https://sciaga.pl/tekst/61216-62-wyjasnienie_nazwy_sredniowiecze_i_zakres_uzycia")),
                Paragraph(subtitle = "Niepochlebne zabarwienie", content = "Termin średniowiecza od początku miał wyraźnie oskarżycielskie, krytyczne zabarwienie. Średniowiecze zawsze postrzegano w ściśle negatywnych kategoriach: jako czas ciemnoty i barbarzyństwa, a także bezprecedensowego zacofania cywilizacyjnego. W wieku XVIII, a więc okresie tak zwanego oświecenia, rozpowszechnił się z kolei nie mniej negatywny termin „mrocznych wieków”."),
                Paragraph(content = "W świetle obecnej wiedzy wcześniejsze wyobrażenie o dziesięciu wiekach europejskiej ciemnoty ma niewiele wspólnego z rzeczywistością. Choć nadal większość postrzega Średniowiecze jako nic postępowego, to jednak jest to okres kluczowych przemian, ale nie stałego i głębokiego regresu. W tej epoce następowały istotne innowacje w przeróżnych dziedzinach, takich jak transport, rolnictwo i handlu. Był to również czas wielkich odkryć geograficznych, jak choćby dopłynięcie wikingów do Ameryki."))))
        articlesList.add(Article
            (title = "Barok",
            objectId = "O1",
            source = Source(srcDescription = MAIN_TEXT, page = KLP_PL, url = "https://klp.pl/barok/"),
            header = Header(content = listOf(Pair(TIME_FRAME, "koniec XVI w. - XVIII w."))),
            listOfPhotos = listOf(Photo(objectId = "O1_0", description = "Barok",
                    source = Source(page = EPODRECZNIKI_PL, url = "https://epodreczniki.pl/a/od-renesansu-do-baroku/Ds47GuVAU")),
                Photo(objectId = "O1_1", description = "Mapa Rzeczypospolitej z 1701 roku", numberOfParagraph = 4,
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Historia_Polski_(1697%E2%80%931763)")),
                Photo(objectId = "O1_2", description = "Fontanna di Trevi", numberOfParagraph = 5,
                    source = Source(page = EPODRECZNIKI_PL, url = "https://epodreczniki.pl/a/barok-w-europie-i-polsce/DQDA2uZal")),
                Photo(objectId = "O1_3", description = "Perły o nieregularnych kształtach", numberOfParagraph = 0,
                    source = Source(page = STAGE_ODBICIA_BLOGSPOT_COM, url = "https://stage-odbicia.blogspot.com/2011/09/barocco.html"))),
            listOfParagraphs = listOf(Paragraph(subtitle = "Nazwa epoki", content = "Nazwa epoki baroku wywodzi się z portugalskiego lub włoskiego słowa „barocco”, co oznacza „perłę o nieregularnych kształtach”. W takim znaczeniu po raz pierwszy użył go przyrodnik Garcia de Orta. Początkowo sporadycznie używana nazwa „barok” miała negatywne zabarwienie."),
                Paragraph(content = "Nie zawsze patrzono na barok jako na ciekawą i wartościową epokę. Rozpatrywano go jedynie jako przykład zepsucia smaku w odniesieniu do renesansu, był utożsamiany ze złym gustem i zacofaniem. W myśl epoki oświecienia, nie zasługiwał nawet na miano osobnego nurtu."),
                Paragraph(content = "Należne miejsce sztuce baroku przywróciły dopiero badania XX-wieczne. To dwaj niemieccy literaturoznawcy: Heinrich Wölfflin i Fritz Strich byli jednymi z pierwszych, którzy zaczęli patrzeć na barok jako na osobną, pełną wartościowego stylu epokę."),
                Paragraph(subtitle = "Powrót do religijności", content = "Główną cechą baroku było powrót do wartości duchowych, religijnych, ale nie tylko katolickich. Był to czas wyłonienia się nowych niezależnych nurtów wyznaniowych i myślowych takich jak luteranizm, kalwinizm czy arianizm."),
                Paragraph(subtitle = "Świetność Rzeczypospolitej", content = "Okres baroku to najlepsze czasy w historii Rzeczypospolitej. Była wówczas jednym z największych mocarstw Europy. Jednocześnie Polska stawała się największym państwem chrześcijańskim w Europie. Rosnąca potęga międzynarodowa nie oznaczała jednak, że wewnątrz państwa ma być równie wspaniale, czego przykładem jest chociażby wybuch powstania Chmielnickiego."),
                Paragraph(subtitle = "Sztuka baroku", content = "Artyści renesansu byli dość niekonsekwentni. Artyści uważali się za następców sztuki renesansu, jednak naruszali jego zasady i ustalenia. Harmona, jasność, logika i racjonalność ubiegłej epoki zaczęły zanikać. Barok szukał czegoś nowego, kreatywnego. Był to nurt stawiający przede wszystkim na przepych i blask. Miał za zadanie oddziaływać na fantazję i zachwycać swoich odbiorców."),
                Paragraph(content = "Renesansowy spokój i harmonia, proste formy ze sztuki renesansu zostały zastąpione przez dynamiczne krzywizny, oryginalne, wykwintne i dziwne formy. Niewątpliwe pozwalało to stworzyć wrażenie ruchu, pulsowania."))))
        articlesList.add(Article
            (title = "Oświecenie",
            objectId = "O2",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/O%C5%9Bwiecenie_(epoka)"),
            header = Header(content = listOf(Pair(TIME_FRAME, "koniec XVII w. - początek XIX w."))),
            listOfRelatedArticlesIds = listOf("O4"),
            listOfPhotos = listOf(Photo(objectId = "O2_0", description = "Oświecenie",
                    source = Source(page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/O%C5%9Bwiecenie_w_Polsce")),
                Photo(objectId = "O2_1", description = "Repliki pierwszego mikroskopu", numberOfParagraph = 3,
                    source = Source(page = MICROSCOPE_ANITQUES_COM, url = "http://www.microscope-antiques.com/vlleiden.html"))),
            listOfParagraphs = listOf(Paragraph(content = "Oświecenie określa się jako wiek rozumu, lub wiek filozofów i przypada na koniec XVII wieku do początku XIX. W tym okresie zaczął się szerzyć deizm, naturalizm, czy, podobnie jak w epoce renesansu, krytyka kościoła. Głównym mottem epoki stała się sentencja, autorstwa Horacego, „Miej odwagę być mądrym”, którą rozsławił niemiecki filozof Immanuel Kant.",
                    source = Source(srcDescription = "Motto epoki", photoId = "Z2_0", page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Sapere_aude")),
                Paragraph(subtitle = "Nazwa epoki", content = "Nazwa epoki we wszystkich językach europejskich nawiązuje do światła lub świateł, rozumianego jako siła rozpraszająca mrok i zabobon. Światło jest jedną z głównych metafor całej epoki. Oświecenie w każdym z krajów przybierało swoją specyficzną formę, wynikającą z lokalnych warunków."),
                Paragraph(subtitle = "Nauka", content = "W epoce oświecenie dokonało się wiele kluczowych wynalazków. Był to również okres doskonalenia dokonań poprzedniej epoki, w której rozpoczęła się rewolucja i powstała nowożytna nauka. Doszło także do wyodrębnienia niektórych dziedzin naukowych."),
                Paragraph(content = "Kluczowe znaczenie w chemii miało odkrycie tlenu przez Carla Wilhelma Scheele'a i Josepha Priestleya, a następnie badania nad nim przez Antoine'a Lavoisiera, który nadał mu łacińską nazwę oxygen. W dziedzinie biologii natomiast przełomowy okazał się wynalazek mikroskopu dokonany przez Antoniego van Leeuwenhoeka."),
                Paragraph(content = "Oświecenie jest również epoką w której żył Izaak Newton - matematyk, fizyk, alchemik, teolog, autor trzech praw dynamiki oraz prawa powszechnego ciążenia. Jego dokonania pozwoliły wyjaśnić prawa Keplera na temat ruchu planet oraz ostatecznie potwierdziły heliocentryczny system Kopernika."))))
        articlesList.add(Article
            (title = "Romantyzm",
            objectId = "O3",
            source = Source(srcDescription = MAIN_TEXT, page = KLP_PL, url = "https://klp.pl/romantyzm/"),
            header = Header(content = listOf(Pair(TIME_FRAME, "lata 90. XVIII w. - lata 40. XIX w."))),
            listOfPhotos = listOf(Photo(objectId = "O3_0", description = "Romantyzm",
                    source = Source(page = RADYKALNYSLON_ORG, url = "http://radykalnyslon.org/romantyzm-epoka-trentowskiego-i-mickiewicza/")),
                Photo(objectId = "O3_1", description = "Krajobraz nordycki. Wiosna, Caspar David Friedriech", numberOfParagraph = 3,
                    source = Source(page = RYNEKISZTUKA_PL, url = "https://rynekisztuka.pl/2014/09/25/10-ponadczasowych-romantycznych-przedstawien/")),
                Photo(objectId = "O3_2", description = "Caspar David Friedrich, Wędrowiec nad morzem mgły", numberOfParagraph = 0,
                    source = Source(page = PRZEKROJ_PL, url = "https://przekroj.pl/kultura/modlitwa-bez-slow-anna-arno"))),
            listOfParagraphs = listOf(Paragraph(content = "Można pokusić się na powiedzenie, że romantyzm ma równą ilość gorących zwolenników, co zagorzałych przeciwników. Przyczyną może być światopogląd romantycznych bohaterów, których autorzy kreowali w oparciu o własne doświadczenia, wnioski podjęte w trakcie częstych podróży. Wygłaszali kontrowersyjne opinie z punktu widzenia współczesnego czytelnika. Niezrozumiałe wydaje nam się dziś przekonanie o konieczności oddania życia czy pokutowania do ostatnich chwil za zbrodnię popełnioną w młodości."),
                Paragraph(subtitle = "Epoka skrajności", content = "Z jednej strony ludowość z jej archaicznym językiem, religijnością czy barwnymi strojami i obrzędami (Romantyczność, Ballady i romanse Mickiewicza), z drugiej egzotyka przepełniona tajemnicą, grzechem i seksualnością (Giaur Byrona). Z jednej strony chęć oddania życia za ojczyznę (Kordian Słowackiego), z drugiej negacja wszystkiego (Cierpienia młodego Wertera Goethego)."),
                Paragraph(subtitle = "Malarstwo", content = "Natchnieniem dla malarzy romantycznych stała się natura, jej tajemniczość, potęga i żywiołowość. Artyści zorientowali się, że nie ujmą tych cech przyrody bez zmian warsztatowych. Postanowili znacznie rozszerzyć paletę barw. Zmianom uległy również zasady kompozycji, dla malarzy bardzo ważny stał się konstrast."),
                Paragraph(content = "Tematem wielu dzieł stały się zjawiska fantastyczne, lecz malowano również wiele pejzaży. Malarze większą uwagę także zwrócili na to aby ich dzieła niosły ze sobą pewną symbolikę. Za najważniejsze uważano ekspresję, co spowodowało to, że każdy artysta mógł wypracować swój własny styl."),
                Paragraph(content = "Poza przyrodą dużym zainteresowaniem malarzy romantycznych cieszył się człowiek i jego uczuciowość oraz patriotyczne wydarzenia historyczne. Za romantycznych malarzy uchodzą takie postacie jak: Johann Füssli, Francisco Goya, czy Joseph Mallord William Turner."))))
        return articlesList
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

    private fun getArticlesOfPhotoArticlesList(): List<Article>{
        val articleConverter = ArticleConverterImpl()
        val photoArticles = getPhotoArticlesList()
        val articlesList = mutableListOf<Article>()
        photoArticles.forEach{ photoArticle ->
            if(photoArticle.objectType != CITY_TYPE)
                articlesList.add(articleConverter.convertPhotoArticleToArticle(photoArticle))
        }
        return articlesList
    }

    override fun getPhotoArticlesList(): List<PhotoArticle> {
        val photoArticles = mutableListOf<PhotoArticle>()
        photoArticles.add(PhotoArticle
            (title = "Wysoka Brama",
            shortTitle = "W. Brama",
            objectId = "I0",
            cityKey = "OLS",
            yearOfBuild = 1400,
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Brama_G%C3%B3rna_w_Olsztynie"),
            latLng = LatLng(53.777574, 20.477587),
            paragraph = Paragraph(content = "Jest jedyną bramą pozostałą z trzech, które znajdowały się w murach obronnych otaczających miasto. Górna Brama powstała po wytyczeniu północnych granic miasta w drugim akcie lokacyjnym z 4 maja 1378. W roku 2012 prace archeologiczne odsłoniły pierwotne kamienne fundamenty bramy z XIV wieku, tuż na północ od jej obecnego położenia. Podczas wykopalisk odkryto ok. 40 monet z XIV i XV wieku oraz około 120 kul armatnich."),
            photo = Photo(objectId = "I0_0", description = "Brama Górna, Olsztyn",
                source = Source(page = FOTOGRAFICZNIE16, url = "http://fotograficznie16.rssing.com/chan-38531473/all_p11.html"))))
        photoArticles.add(PhotoArticle
            (title = "Zamek Kapituły Warmińskiej",
            shortTitle = "Zamek K.W.",
            objectId = "I1",
            cityKey = "OLS",
            yearOfBuild = 1353,
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Zamek_Kapitu%C5%82y_Warmi%C5%84skiej_w_Olsztynie"),
            latLng = LatLng(53.777832, 20.474515),
            paragraph = Paragraph(content = "Zamek Kapituły Warmińskiej został wybudowany w połowie XIV wieku w stylu gotyckim przez kapitułę warmińską, która była jego właścicielem do roku 1772. Budowla pełniła funkcje obronne oraz była siedzibą min. administratora dóbr kapituły. Najsławniejszym mieszkańcem zamku był Mikołaj Kopernik, który pełnił obowiązki administratora w latach 1516–1521. Na jednej ze ścian zachowała się po nim oryginalna tablica astronomiczna."),
            photo = Photo(objectId = "I1_0", description = "Zamek w Olsztynie, około 1900 rok",
                source = Source(page = ZAMKIPOLSKIE_COM, url = "http://www.zamkipolskie.com/olszt/olszt.html"))))
        photoArticles.add(PhotoArticle
            (title = "Stary Ratusz",
            shortTitle = "S. Ratusz",
            objectId = "I2",
            cityKey = "OLS",
            yearOfBuild = 1500,
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Stary_Ratusz_w_Olsztynie"),
            latLng = LatLng(53.776378, 20.475753),
            paragraph = Paragraph(content = "Początkowo budynek – jego południowe skrzydło – zbudowany był w stylu późnego gotyku jako budowla o planie prostokąta, piętrowa. Do dziś zachowały się gotyckie ściany północna, wschodnia i południowa, a po stronie południowej również gotycka elewacja. W 1620 roku uległ spaleniu, podobnie jak większa część miasta. Cztery lata później został odbudowany. Jego oblicze zmieniło się wówczas całkowicie. Władze miasta zajmowały wówczas całe piętro budynku, podczas gdy parter przeznaczony był dla celów handlowych."),
            photo = Photo(objectId = "I2_0", description = "Rynek i stary ratusz w Olsztynie, około 1910-1914 rok",
                source = Source(page = POLSKA_ORG_PL, url = "https://polska-org.pl/7802416,foto.html"))))
        photoArticles.add(PhotoArticle
            (title = "Teatr Stefana Jaracza",
            shortTitle = "Teatr S.J.",
            objectId = "I3",
            cityKey = "OLS",
            yearOfBuild = 1925,
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Teatr_im._Stefana_Jaracza_w_Olsztynie"),
            latLng = LatLng(53.780122, 20.479785),
            paragraph = Paragraph(content = "Teatr został założony w 1925 jako niem. Treudank-Theater w podzięce dla ówczesnych mieszkańców za wygrany plebiscyt. Teatr funkcjonował pod tą nazwą do 1945, wystawiane wówczas sztuki grano wyłącznie w języku niemieckim. Architektem teatru był August Feddersen. Po II wojnie światowej 18 listopada 1945 roku działalność Teatru jako polskiej sceny zainaugurowano premierą spektaklu „Moralność pani Dulskiej” Gabrieli Zapolskiej, w reżyserii Artura Młodnickiego."),
            photo = Photo(objectId = "I3_0", description = "Teatr im. Stefana Jaracza w Olsztynie, około 1930-1935 rok",
                source = Source(page = POLSKA_ORG_PL, url = "https://polska-org.pl/7900593,foto.html"))))
        photoArticles.add(PhotoArticle
            (title = "Dąbrowszczaków",
            objectId = "I4",
            cityKey = "OLS",
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/%C5%9Ar%C3%B3dmiejskie_ulice_i_place_w_Olsztynie"),
            latLng = LatLng(53.778578, 20.482421),
            paragraph = Paragraph(content = "Pierwotnie do roku 1904 nazwa ulicy brzmiała Wartenburger-Straße (ulica Wartemborska). W roku 1904 zmieniono jej nazwę na Kaiser-Straße (ulica Cesarska), na cześć cesarza Wilhelma I, którego pomnik wzniesiono przed gmachem Gimnazjum Męskiego. Po 1945 roku nosiła nazwę ulicy Stalina, lecz po jego śmierci w okresie 'odwilży' 1956 roku zmieniono nazwę na Dąbrowszczaków."),
            photo = Photo(objectId = "I4_0", description = "Tramwaje w Olsztynie, ulica Dąbrowszczaków (dawniej Kaiserstrasse), 1910",
                source = Source(page = OLSZTYN_FOTOPOLSKA_EU, url = "https://olsztyn.fotopolska.eu/560824,foto.html"))))
        photoArticles.add(PhotoArticle
            (title = "Urząd Pocztowy",
            shortTitle = "U. Pocztowy",
            objectId = "I5",
            cityKey = "OLS",
            yearOfBuild = 1887,
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Poczta_G%C5%82%C3%B3wna_w_Olsztynie"),
            latLng = LatLng(53.776478, 20.479686),
            paragraph = Paragraph(content = "Budynek został zbudowany w 1887 na parceli wykupionej od gminy katolickiej, w stylu neogotyckim, z bogatą dekoracją zewnętrzną z glazurowanej cegły. Na dachu obiektu znajdowały się wieżyczki odciągowe, od których od 1892 rozchodziły się napowietrzne linie telefoniczne. W 1907 budynek został rozbudowany o dodatkowe skrzydło, przeznaczone wyłącznie do obsługi paczek nadsyłanych do żołnierzy służących w garnizonie w Olsztynie."),
            photo = Photo(objectId = "I5_0", description = "Urząd Pocztowy w Olsztynie, 1915",
                source = Source(page = OLSZTYN_FOTOPOLSKA_EU, url = "https://olsztyn.fotopolska.eu/612/612079/803/Olsztyn_Urzad_Pocztowy_Olsztyn_1.jpg?m=1433563294"))))
        photoArticles.add(PhotoArticle
            (title = "Remiza Straży Pożarnej",
            shortTitle = "Remiza S.P.",
            objectId = "I6",
            cityKey = "OLS",
            yearOfBuild = 1910,
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Remiza_Stra%C5%BCy_Po%C5%BCarnej_w_Olsztynie"),
            latLng = LatLng(53.772541, 20.476943),
            paragraph = Paragraph(content = "Remiza Straży Pożarnej w Olsztynie została wybudowana na początku XX wieku w pobliżu Placu Roosevelta. Jej powstanie datuje się na lata 1908-1909. W 1990 roku przeszedł generalny remont. Jest to piętrowy budynek z użytkowym poddaszem o cechach stylu renesansowego z elementami baroku."),
            photo = Photo(objectId = "I6_0", description = "Remiza Straży Pożarnej w Olsztynie, 1920",
                source = Source(page = OLSZTYN_FOTOPOLSKA_EU, url = "https://olsztyn.fotopolska.eu/46573,obiekt.html?map_z=18&f=1405774-foto"))))
        photoArticles.add(PhotoArticle
            (title = "B. Rejencji",
            objectId = "I7",
            cityKey = "OLS",
            yearOfBuild = 1908,
            source = Source(srcDescription = MAIN_TEXT, page = WIKIPEDIA_PL, url = "https://pl.wikipedia.org/wiki/Budynek_Rejencji_w_Olsztynie"),
            latLng = LatLng(53.776454, 20.486388),
            paragraph = Paragraph(content = "Gmach wybudowano w latach 1908–1911 na potrzeby urzędu Rejencji Olsztyńskiej (Regierungsbezirk Allenstein) wydzielonej w południowej części Prus Wschodnich w roku 1905 (odpowiednik polskiego województwa). W gmachu urzędował prezydent Rejencji oraz inni jej urzędnicy, znajdowały się taż kasy ubezpieczeń społecznych oraz mieścił się Urząd Powiatów Prus Wschodnich."),
            photo = Photo(objectId = "I7_0", description = "Urząd Marszałkowski oraz Wojewódzki Sąd Administracyjny w Olsztynie, 1912",
                source = Source(page = OLSZTYN_FOTOPOLSKA_EU, url = "https://olsztyn.fotopolska.eu/7842,obiekt.html?map_z=16&f=837097-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Olsztyn",
            latLng = LatLng(53.776948, 20.480047),
            cityKey = "OLS",
            objectType = CITY_TYPE))

        photoArticles.add(PhotoArticle
            (title = "Ratusz Staromiejski",
            shortTitle = "Ratusz",
            objectId = "I8",
            cityKey = "TOR",
            yearOfBuild = 1390,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zwiedzanie-miasta/zabytki/ratusz-staromiejski"),
            latLng = LatLng(53.010652, 18.604079),
            paragraph = Paragraph(content = "Ratusz wzniesiono w stylu gotyckim w końcu XIV w. włączając w jego mury zbudowaną ponad wiek wcześniej wieżę, którą dodatkowo podwyższono i ozdobiono blaszanym hełmem. W ten sposób powstał jednopiętrowy budynek z obszernym dziedzińcem, naśladujący najlepsze wzorce architektury flandryjskiej. Jego wnętrze codziennie zapełniało się tłumem kramarzy, kupców, rajców i rzezimieszków. W wieży krył się skarbiec, miejskie archiwum i więzienie, zaś w obszernych piwnicach szynkowano piwo i wino."),
            photo = Photo(objectId = "I8_0", description = "Ratusz Staromiejski w Toruniu, 1912",
                source = Source(page = NOWOSCI_COM_PL, url = "https://nowosci.com.pl/ratusz-staromiejski-na-zdjeciach-z-archiwum-galeria/ga/11712120/zd/22354776"))))
        photoArticles.add(PhotoArticle
            (title = "Pomnik Mikołaja Kopernika",
            shortTitle = "Pomnik M.K.",
            objectId = "I9",
            cityKey = "TOR",
            yearOfBuild = 1853,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zwiedzanie-miasta/zabytki/pomnik-mikolaja-kopernika"),
            latLng = LatLng(53.010261, 18.604582),
            paragraph = Paragraph(content = "Odlany w pracowni znanego berlińskiego rzeźbiarza Fryderyka Tiecka, został ustawiony w obecnym miejscu w 1853 roku. Spiżowy pomnik przedstawia astronoma jako postać ubraną w profesorską togę i trzymającą w lewej ręce narzędzie astronomiczne zwane sferą armilarną (astrolabium sferyczne). Inaczej niż na większości innych pomników i portretów, młodzieńcza twarz Kopernika ozdobiona jest delikatnym, widocznym z bliska wąsem."),
            photo = Photo(objectId = "I9_0", description = "Pomnik Mikołaja Kopernika w Toruniu",
                source = Source(page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zwiedzanie-miasta/zabytki/pomnik-mikolaja-kopernika"))))
        photoArticles.add(PhotoArticle
            (title = "Dom Mikołaja Kopernika",
            shortTitle = "Dom M.K.",
            objectId = "I10",
            cityKey = "TOR",
            yearOfBuild = 1350,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zwiedzanie-miasta/zabytki/dom-mikolaja-kopernika"),
            latLng = LatLng(53.009309, 18.603962),
            paragraph = Paragraph(content = "Dom Mikołaja Kopernika to średniowieczna kamienica, która w drugiej połowie XV wieku należała do rodziny Koperników. Wielu historyków wskazuje ją jako miejsce, gdzie w 1473 roku przyszedł na świat wielki astronom. Budowla swój obecny wygląd otrzymał w XV wieku. Fasada kamienicy ozdobiona jest ostrołukowym portalem, ceglanymi fryzami i pionowymi wnękami, w których namalowano charakterystyczne dla średniowiecznego Torunia dwubarwne wzory."),
            photo = Photo(objectId = "I10_0", description = "Dom Mikołaja Kopernika w Toruniu",
                source = Source(page = YOUTUBE_COM, url = "https://www.youtube.com/watch?v=j1HWY4XKJUY"))))
        photoArticles.add(PhotoArticle
            (title = "Krzywa Wieża",
            shortTitle = "K. Wieża",
            objectId = "I11",
            cityKey = "TOR",
            yearOfBuild = 1350,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zwiedzanie-miasta/zabytki/krzywa-wieza"),
            latLng = LatLng(53.008410, 18.602081),
            paragraph = Paragraph(content = "Krzywa Wieża została wzniesiona w XIV wieku jako jedna z kilkudziesięciu podobnych baszt w murach obronnych dawnego Torunia. Jej pochylenie było prawdopodobnie wynikiem osunięcia się piaszczystego podłoża. W przeszłości nie brakowało jednak i takich, którzy widzieli w skrzywieniu wieży karę boską za rzekomo bluźniercze odkrycie Kopernika. Wiadomość o tym dotarła nawet do Rzymu i była poważnym argumentem za wpisaniem książki toruńskiego astronoma do indeksu ksiąg zakazanych."),
            photo = Photo(objectId = "I11_0", description = "Krzywa Wieża w Toruniu, 1910",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Krzywa_Wieza_Torun?f=671539-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Katedra Świętych Janów",
            shortTitle = "Katedra Ś.J.",
            objectId = "I12",
            cityKey = "TOR",
            yearOfBuild = 1240,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zwiedzanie-miasta/zabytki/koscioly-na-starowce/katedra-swietych-janow"),
            latLng = LatLng(53.009383, 18.606259),
            paragraph = Paragraph(content = "Gotycka katedra pod wezwaniem św. Jana Chrzciciela i św. Jana Ewangelisty swoją formę uzyskała w wyniku wieloetapowej budowy, którą po blisko 200 latach przerwano na przełomie XV i XVI w. Już w średniowieczu był to najważniejszy z toruńskich kościołów, pełniący funkcję staromiejskiej fary, w której skupiało się religijne życie mieszkańców miasta."),
            photo = Photo(objectId = "I12_0", description = "Katedra Świętych Janów w Toruniu, 1905 - 1915",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/21417,foto.html?o=b4642"))))
        photoArticles.add(PhotoArticle
            (title = "Brama Mostowa",
            shortTitle = "Brama M.",
            objectId = "I13",
            cityKey = "TOR",
            yearOfBuild = 1432,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zabytki/bramy-i-baszty/brama-mostowa"),
            latLng = LatLng(53.008622, 18.608895),
            paragraph = Paragraph(content = "Brama Mostowa była najbardziej nowoczesną ze wszystkich średniowiecznych bram strzegących miasta od strony portu. Jej zaokrąglone boki zmniejszały skutki artyleryjskiego ataku, zaś specjalnie przygotowane otwory strzelnicze umożliwiały użycie dział do bronienia dostępu do miasta. Brama była jednocześnie najniżej położonym punktem murów obronnych Torunia, stąd też to właśnie na niej oznaczano poziom wody w Wiśle w czasie najwyższych powodzi."),
            photo = Photo(objectId = "I13_0", description = "Brama Mostowa w Toruniu, 1915",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Torun/b69874,Mury_miejskie.html?f=344927-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Brama Klasztorna",
            shortTitle = "Brama K.",
            objectId = "I14",
            cityKey = "TOR",
            yearOfBuild = 1350,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zwiedzanie-miasta/zabytki/bramy-i-baszty/brama-klasztorna"),
            latLng = LatLng(53.008283, 18.603601),
            paragraph = Paragraph(content = "Mimo drobnych przebudowań, brama zachowała do dzisiaj oryginalną formę gotyckiej wieży bramnej z trzema ostrołukowymi wnękami. Zewnętrzna skrywała drewnianą bronę, spuszczaną w razie oblężenia. Za środkową znajduje się pusta przestrzeń, przez którą usadowieni na szczycie bramy obrońcy zrzucali ciężkie przedmioty lub wylewali gorące płyny. Najczęściej była to wrząca, długo trzymająca ciepło i niemiłosiernie parząca kasza, od której przydatny w walce otwór nazwano kaszownikiem."),
            photo = Photo(objectId = "I14_0", description = "Brama Klasztorna w Toruniu, 1910 - 1915",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/1000653,foto.html?o=b4493"))))
        photoArticles.add(PhotoArticle
            (title = "Brama Żeglarska",
            shortTitle = "Brama Ż.",
            objectId = "I15",
            cityKey = "TOR",
            yearOfBuild = 1250,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zwiedzanie-miasta/zabytki/bramy-i-baszty/brama-zeglarska"),
            latLng = LatLng(53.008260, 18.606192),
            paragraph = Paragraph(content = "Brama Żeglarska zbudowana została w średniowieczu, jednak jej obecny kształt jest rezultatem gruntownej dziewiętnastowiecznej przebudowy. Otwierając się z jednej strony na port, zaś z drugiej na ulicę prowadzącą do farnego kościoła św. Janów i staromiejskiego rynku, brama ta była najważniejszym wejściem do miasta. To właśnie tutaj, przy dźwięku wielkiego dzwonu Tuba Dei, uroczyście witano przybywających do Torunia polskich królów."),
            photo = Photo(objectId = "I15_0", description = "Brama Żeglarska w Toruniu, 1950",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Brama_Zeglarska_Torun?f=1127184-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Ruiny Zamku Krzyżackiego",
            shortTitle = "Ruiny Z.K.",
            objectId = "I16",
            cityKey = "TOR",
            yearOfBuild = 1250,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zabytki/zamki/ruiny-zamku-krzyzackiego"),
            latLng = LatLng(53.009367, 18.610718),
            paragraph = Paragraph(content = "Zamek, początkowo drewniany, później wznoszony z kamienia i cegły, był rozbudowywany aż do połowy XV wieku. Wraz z przedzamczem i znajdującymi się na nim budynkami gospodarczymi zajmował przestrzeń między Starym i Nowym Miastem Toruniem. Toruński zamek miał duże znaczenie strategiczne i związane z tym silne fortyfikacje. Za wysokim murem głównego zamku znajdował się kilkupiętrowy budynek z kaplicą, refektarzem i dormitorium, w którym mieszkali krzyżaccy rycerze."),
            photo = Photo(objectId = "I16_0", description = "Rekonstrukcja wyglądu zamku krzyżackiego w Toruniu przed zburzeniem w 1454 roku, 1450",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Torun/b4790,Zamek_Krzyzacki.html?f=288318-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Zamek Dybowski",
            shortTitle = "Zamek D.",
            objectId = "I17",
            cityKey = "TOR",
            yearOfBuild = 1423,
            source = Source(srcDescription = MAIN_TEXT, page = TORUN_PL, url = "https://www.torun.pl/pl/turystyka/zabytki/zamki/zamek-dybowski"),
            latLng = LatLng(53.001166, 18.599937),
            paragraph = Paragraph(content = "Zamek Dybowski został wzniesiony na lewym brzegu Wisły przez Władysława Jagiełłę jako siedziba polskich starostów i strategiczny punkt militarny do kontroli ruchu na Wiśle i granicy polsko-krzyżackiej. Jego budowę rozpoczęto prawdopodobnie ok. 1423-1425 r., ale już w początkowym okresie był kilkakrotnie przebudowywany."),
            photo = Photo(objectId = "I17_0", description = "Ruiny zamku Dybów i panorama Torunia, 1927-1933",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Torun/b46845,Zamek_Dybow.html?f=1019098-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Toruń",
            latLng = LatLng(53.008477, 18.606382),
            zoom = 15.1f,
            cityKey = "TOR",
            objectType = CITY_TYPE))

        photoArticles.add(PhotoArticle
            (title = "Wzgórze Katedralne",
            shortTitle = "Wzgórze K.",
            objectId = "I18",
            cityKey = "FRO",
            yearOfBuild = 1330,
            source = Source(srcDescription = MAIN_TEXT, page = FROMBORK_ART_PL, url = "http://frombork.art.pl/pl/wzgorze-katedralne/"),
            latLng = LatLng(54.356699, 19.681739),
            paragraph = Paragraph(content = "Wzgórze Katedralne we Fromborku należy do zabytków najwyższej klasy. Wielokrotnie niszczone i przebudowywane zachowało podstawowe elementy średniowiecznego założenia architektonicznego. Wyjątkowe znaczenie tego miejsca podnoszą tradycje historyczne i postać Mikołaja Kopernika. Wiele zgromadzonych zabytków wspaniałej przeszłości przyciąga do Fromborka ludzi z całego świata."),
            photo = Photo(objectId = "I18_0", description = "Bazylika archikatedralna na Wzgórzu Katedralnym we Fromborku, 1930 - 1935",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Frombork/b5298,Wzgorze_Katedralne.html?f=285086-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Bazylika Katedralna WNMP i św. Andrzeja",
            shortTitle = "Bazylika K.",
            objectId = "I19",
            cityKey = "FRO",
            yearOfBuild = 1330,
            source = Source(srcDescription = MAIN_TEXT, page = FROMBORK_ART_PL, url = "http://frombork.art.pl/pl/wzgorze-katedralne/"),
            latLng = LatLng(54.356882, 19.682534),
            paragraph = Paragraph(content = "Budynek jest najstarszy na Wzgórzu i został zbudowany w latach 1329-1388. Posiada kilka przybudówek, w tym dwie kaplice: św. Jerzego (tzw. Kaplica Polska) – powstała ok. 1500 r., Zbawiciela (zwana kaplicą Szembeka) – barokowa z 1735 r. Katedra warmińska jest budowlą gotycką, halową, trójnawową o długości około 97 m, szerokości od 12 m (prezbiterium) do 22 m (główny korpus) i wysokości od posadzki do zwornika sklepienia 16,5 m."),
            photo = Photo(objectId = "I19_0", description = "Bazylika archikatedralna na Wzgórzu Katedralnym we Fromborku, 1920 - 1930",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Frombork/b4085,Bazylika_archikatedralna_Wniebowziecia_Najswietszej_Maryi_Panny_i_sw_Andrzeja.html?f=325848-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Dzwonnica",
            objectId = "I20",
            cityKey = "FRO",
            yearOfBuild = 1550,
            source = Source(srcDescription = MAIN_TEXT, page = FROMBORK_ART_PL, url = "http://frombork.art.pl/pl/wzgorze-katedralne/"),
            latLng = LatLng(54.356225, 19.681040),
            paragraph = Paragraph(content = "Dzwonnica zwana Wieżą Radziejowskiego jest najwyższą budowlą Wzgórza Katedralnego o zabudowaniu gotycko-barokowym (XVI-XVII w.). Spłonęła w 1945 r. i została odbudowana w latach 1972-1973. W dzwonnicy zawieszone jest Wahadło Foucaulta – przyrząd do obserwacji ruchu wirowego Ziemi. Na wysokości 70 m n.p.m. z kolei znajduje się taras widokowy, z którego można podziwiać panoramę okolic Fromborka."),
            photo = Photo(objectId = "I20_0", description = "Wieża Radziejowskiego na Wzgórzu Katedralnym we Fromborku, 1920 - 1930",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Frombork/b88076,Wieza_Radziejowskiego.html?f=479051-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Wieża Kopernika",
            shortTitle = "Wieża K.",
            objectId = "I21",
            cityKey = "FRO",
            yearOfBuild = 1390,
            source = Source(srcDescription = MAIN_TEXT, page = FROMBORK_ART_PL, url = "http://frombork.art.pl/pl/wzgorze-katedralne/"),
            latLng = LatLng(54.356754, 19.680781),
            paragraph = Paragraph(content = "Budowla jest najstarszym elementem fortyfikacji Wzgórza Katedralnego. Została zbudowana przed 1400 r., wyższe kondygnacje wielokrotnie były przebudowywane w XV – XVIII w. W 1945 roku została spalona, a potem rekonstruowana – prace zakończono w 1965 r. Wieża była własnością Mikołaja Kopernika w latach 1504-1543."),
            photo = Photo(objectId = "I21_0", description = "Mury obronne i Wieża Kopernika na Wzgórzu Katedralnym we Fromborku, 1973 - 1975",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Frombork/b66744,Wieza_Kopernika_-_Muzeum_Mikolaja_Kopernika.html?f=360536-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Brama Południowa",
            shortTitle = "Brama P.",
            objectId = "I22",
            cityKey = "FRO",
            yearOfBuild = 1350,
            source = Source(srcDescription = MAIN_TEXT, page = POLSKIESZLAKI_PL, url = "https://www.polskieszlaki.pl/wzgorze-katedralne-we-fromborku.htm/"),
            latLng = LatLng(54.356469, 19.682343),
            paragraph = Paragraph(content = "Na teren Wzgórza Katedralnego wchodzi się i wychodzi poprzez Bramę Południową, pochodzącą z XIV wieku, a rozbudowaną w połowie XIX wieku. Dziś mieści kasę główną Muzeum oraz punkt sprzedaży wydawnictw muzealnych."),
            photo = Photo(objectId = "I22_0", description = "Brama Południowa Wzgórza Katedralnego we Fromborku, 1852",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Frombork/b88074,Brama_Poludniowa.html?f=444524-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Wieża Wodna",
            shortTitle = "Wieża W.",
            objectId = "I23",
            cityKey = "FRO",
            yearOfBuild = 1571,
            source = Source(srcDescription = MAIN_TEXT, page = "wiezawodna.pl", url = "http://www.wiezawodna.pl/"),
            latLng = LatLng(54.357738, 19.680539),
            paragraph = Paragraph(content = "Wieża Wodna to charakterystyczny, integralny element tzw. Kanału Kopernika.W latach 1571-1572 budowlę wyposażono w specjalnie zaprojektowany, częściowo mosiężny mechanizm czerpakowy, dzięki czemu woda z kanału była podnoszona na znaczną wysokość (około 25 m), skąd dzięki różnicy ciśnień i wykorzystaniu zasady naczyń połączonych, była doprowadzana na fromborskie wzgórze i do kanonii zewnętrznych, leżących poza murami katedralnych umocnień."),
            photo = Photo(objectId = "I23_0", description = "Wieża Wodna we Fromborku, 1959",
                source = Source(page = FOTOPOLSKA_EU, url = "https://fotopolska.eu/Wieza_wodna_Frombork?f=610855-foto"))))
        photoArticles.add(PhotoArticle
            (title = "Frombork",
            latLng = LatLng(54.357647, 19.681490),
            zoom = 16.5f,
            cityKey = "FRO",
            objectType = CITY_TYPE))
        return photoArticles
    }

    companion object{
        const val MAIN_TEXT = "Treść główna"
        const val PROFESSIONS = "Profesje"
        const val LIVE_YEARS = "Lata życia"
        const val NATIONALITY = "Narodowość"
        const val CREATE_YEAR = "Data powstania"
        const val CREATEOR = "Twórca"
        const val EXPLORER = "Odkrywca"
        const val ART_PLACE = "Miejsce przebywania"
        const val TIME = "Okres"
        const val TIME_FRAME = "Ramy czasowe"
        const val TIME_FRAME_POLAND = "Ramy czasowe w Polsce"
        const val TIME_FRAME_EUROPE = "Ramy czasowe w Europie"
        const val TIME_FRAME_ITALY = "Ramy czasowe we Włoszech"
        const val PLACE = "Miejsce"
        const val DATE = "Data"

        const val WIKIPEDIA_PL = "pl.wikipedia.org"
        const val TVP_INFO = "tvp.info"
        const val FOTOGRAFICZNIE16 = "fotograficznie16.rssing.com"
        const val NIEZLA_SZTUKA = "niezlasztuka.net"
        const val VISITTUSCANY = "visittuscany.com"
        const val THE_ATLANTIC = "theatlantic.com"
        const val FOCUS_PL = "focus.pl"
        const val THETIMES_CO_UK = "thetimes.co.uk"
        const val RADIO90_PL = "radio90.pl"
        const val CULTURE_PL = "culture.pl"
        const val PODROZE_ONET = "podroze.onet.pl"
        const val POLSKINA5_PL = "polskina5.pl"
        const val LATIMES_COM = "latimes.com"
        const val FAKTY_INTERA_PL = "fakty.interia.pl"
        const val GALERIA_ZDJEC_COM = "galeria-zdjec.com"
        const val GETYOURGUIDE_PL= "getyourguide.pl"
        const val DZIECKOWDORDZE_COM = "dzieckowdrodze.com"
        const val POLITYKA_PL = "polityka.pl"
        const val YOUTUBE_COM = "youtube.com"
        const val ARCHIWUM_RP_PL = "archiwum.rp.pl"
        const val WOJCIECH_PIESTRAK_PL = "wojciechpiestrak.pl"
        const val POLSKIE_RADIO_PL = "polskieradio.pl"
        const val LICEALISTA_PL = "licealista.pl"
        const val ALEKLASA_PL = "aleklasa.pl"
        const val EPODRECZNIKI_PL = "epodreczniki.pl"
        const val RADYKALNYSLON_ORG = "radykalnyslon.org"
        const val SCIAGA_PL = "sciaga.pl"
        const val CIEKAWOSTKI_HISTORYCZNE_PL = "ciekawostkihistoryczne.pl"
        const val MOZAWEB_COM = "mozaweb.com"
        const val KLP_PL = "klp.pl"
        const val MICROSCOPE_ANITQUES_COM = "microscope-antiques.com"
        const val RYNEKISZTUKA_PL = "rynekisztuka.pl"
        const val PRZEKROJ_PL = "przekroj.pl"
        const val STAGE_ODBICIA_BLOGSPOT_COM = "stage-odbicia.blogspot.com"
        const val ZAMKIPOLSKIE_COM = "zamkipolskie.com"
        const val POLSKA_ORG_PL = "polska-org.pl"
        const val OLSZTYN_FOTOPOLSKA_EU = "olsztyn.fotopolska.eu"
        const val FOTOPOLSKA_EU = "fotopolska.eu"
        const val TORUN_PL = "torun.pl"
        const val NOWOSCI_COM_PL = "nowosci.com.pl"
        const val FROMBORK_ART_PL = "frombork.art.pl"
        const val POLSKIESZLAKI_PL = "polskieszlaki.pl"

        const val PLACE_TYPE = 10
        const val CITY_TYPE = 11
    }
}