package pl.renesans.renesans.data.realm

import android.content.Context
import android.util.Log
import io.realm.Realm
import pl.renesans.renesans.data.*

class RealmMapperImpl(private val context: Context): RealmMapper {

    private lateinit var realm: Realm

    override fun onCreate() {
        Realm.init(context)
        realm = Realm.getDefaultInstance()
    }

    override fun getArticlesListFromRealm(articlesListRealm: ArticlesListRealm?): ArticlesList =
        ArticlesList(
            articlesListRealm?.id,
            articlesListRealm?.type,
            articlesListRealm?.name,
            articlesListRealm?.index,
            articlesListRealm?.objectType)

    override fun getArticlesListToRealm(articlesList: ArticlesList?): ArticlesListRealm {
        val articlesListRealm = realm.createObject(ArticlesListRealm::class.java)
        setPropertiesOfArticlesListRealm(articlesList, articlesListRealm)
        return articlesListRealm
    }

    override fun setPropertiesOfArticlesListRealm(articlesList: ArticlesList?,
                                                  articlesListRealm: ArticlesListRealm) {
        articlesListRealm.id = articlesList?.id
        articlesListRealm.index = articlesList?.index
        articlesListRealm.name = articlesList?.name
        articlesListRealm.type = articlesList?.type
        articlesListRealm.objectType = articlesList?.objectType
    }

    override fun getArticleFromRealm(articleRealm: ArticleRealm?): Article? {
        val article = Article(
            articleRealm?.objectId,
            articleRealm?.objectType,
            articleRealm?.typeOfScaling,
            articleRealm?.title,
            getHeaderFromRealm(articleRealm?.headerRealm),
            getSourceFromRealm(articleRealm?.sourceRealm))
        addAllRelatedArticlesIdsToArticle(article, articleRealm)
        addAllParagraphsToArticle(article, articleRealm)
        addAllPhotosToArticle(article, articleRealm)
        article.tour = getTourFromRealm(articleRealm?.tourRealm)
        return article
    }

    override fun getArticleItemFromRealm(articleRealm: ArticleRealm?): ArticleItem
            = ArticleItem(articleRealm?.objectId, articleRealm?.title)

    private fun addAllRelatedArticlesIdsToArticle(article: Article, articleRealm: ArticleRealm?){
        if(articleRealm?.listOfRelatedArticlesIds == null) return
        val relatedArticlesIdsList = mutableListOf<String>()
        articleRealm.listOfRelatedArticlesIds?.forEach{ relatedArticlesIdsList.add(it) }
        article.listOfRelatedArticlesIds = relatedArticlesIdsList
    }

    private fun addAllParagraphsToArticle(article: Article, articleRealm: ArticleRealm?){
        val paragraphsList = mutableListOf<Paragraph>()
        articleRealm?.listOfParagraphs?.forEach{ paragraphsList.add(getParagraphFromRealm(it)) }
        article.listOfParagraphs = paragraphsList.toList()
    }

    private fun addAllPhotosToArticle(article: Article, articleRealm: ArticleRealm?){
        val photosList = mutableListOf<Photo>()
        articleRealm?.listOfPhotos?.forEach{ photosList.add(getPhotoFromRealm(it)) }
        article.listOfPhotos = photosList.toList()
    }

    override fun getArticleToRealm(article: Article): ArticleRealm {
        val articleRealm = realm.createObject(ArticleRealm::class.java)
        setPropertiesOfArticleRealm(article, articleRealm)
        return articleRealm
    }

    override fun setPropertiesOfArticleRealm(article: Article?, articleRealm: ArticleRealm) {
        articleRealm.objectId = article?.objectId
        articleRealm.sourceRealm = getSourceToRealm(article?.source)
        articleRealm.objectType = article?.objectType
        articleRealm.title = article?.title
        articleRealm.headerRealm = getHeaderToRealm(article?.header)
        articleRealm.typeOfScaling = article?.typeOfScaling
        articleRealm.tourRealm = getTourToRealm(article?.tour)
        addAllRelatedArticlesIdsToArticleRealm(article, articleRealm)
        addAllParagraphsToArticleRealm(article, articleRealm)
        addAllPhotosToArticleRealm(article, articleRealm)
    }

    override fun getPhotoArticleToRealm(photoArticle: PhotoArticle): PhotoArticleRealm {
        val photoArticleRealm = realm.createObject(PhotoArticleRealm::class.java)
        setPropertiesOfPhotoArticleRealm(photoArticle, photoArticleRealm)
        return photoArticleRealm
    }

    override fun setPropertiesOfPhotoArticleRealm(photoArticle: PhotoArticle?,
                                                  photoArticleRealm: PhotoArticleRealm) {
        photoArticleRealm.objectId = photoArticle?.objectId
        photoArticleRealm.objectType = photoArticle?.objectType
        photoArticleRealm.yearOfBuild = photoArticle?.yearOfBuild
        photoArticleRealm.cityKey = photoArticle?.cityKey
        photoArticleRealm.title = photoArticle?.title
        photoArticleRealm.shortTitle = photoArticle?.shortTitle
        photoArticleRealm.zoom = photoArticle?.zoom
        photoArticleRealm.positionRealm = getPositionToRealm(photoArticle?.position)
        photoArticleRealm.headerRealm = getHeaderToRealm(photoArticle?.header)
        photoArticleRealm.paragraphRealm = getParagraphToRealm(photoArticle?.paragraph)
        photoArticleRealm.photoRealm = getPhotoToRealm(photoArticle?.photo)
        photoArticleRealm.sourceRealm = getSourceToRealm(photoArticle?.source)
    }

    private fun addAllRelatedArticlesIdsToArticleRealm(article: Article?, articleRealm: ArticleRealm){
        if(article?.listOfRelatedArticlesIds!=null)
            articleRealm.listOfRelatedArticlesIds?.addAll(article.listOfRelatedArticlesIds!!)
    }

    private fun addAllParagraphsToArticleRealm(article: Article?, articleRealm: ArticleRealm){
        if(article?.listOfParagraphs!=null)
            for(paragraph in article.listOfParagraphs!!)
                articleRealm.listOfParagraphs?.add(getParagraphToRealm(paragraph))
    }

    private fun addAllPhotosToArticleRealm(article: Article?, articleRealm: ArticleRealm){
        if(article?.listOfPhotos!=null)
            for(photo in article.listOfPhotos!!)
                articleRealm.listOfPhotos?.add(getPhotoToRealm(photo))
    }

    private fun getTourToRealm(tour: Tour?): TourRealm?{
        if(tour==null) return null
        val tourRealm = realm.createObject(TourRealm::class.java)
        tourRealm.title = tour.title
        for(photoArticle in tour.photosArticlesList!!)
            tourRealm.photosArticlesList?.add(getPhotoArticleToRealm(photoArticle))
        return tourRealm
    }

    private fun getHeaderToRealm(header: Header?): HeaderRealm?{
        if(header == null) return null
        val headerRealm = realm.createObject(HeaderRealm::class.java)
        for(key in header.content!!.keys){
            val headerLine = realm.createObject(HeaderLine::class.java)
            headerLine.firstValue = key
            headerLine.secondValue = header.content!!.getValue(key)
            headerRealm.content?.add(headerLine)
        }
        return headerRealm
    }

    private fun getPhotoToRealm(photo: Photo?): PhotoRealm?{
        if(photo == null) return null
        val photoRealm = realm.createObject(PhotoRealm::class.java)
        photoRealm.description = photo.description
        photoRealm.numberOfParagraph = photo.numberOfParagraph
        photoRealm.objectId = photo.objectId
        photoRealm.sourceRealm = getSourceToRealm(photo.source)
        return photoRealm
    }

    private fun getParagraphToRealm(paragraph: Paragraph?): ParagraphRealm?{
        if(paragraph == null) return null
        val paragraphRealm = realm.createObject(ParagraphRealm::class.java)
        paragraphRealm.content = paragraph.content
        paragraphRealm.subtitle = paragraph.subtitle
        paragraphRealm.sourceRealm = getSourceToRealm(paragraph.source)
        return paragraphRealm
    }

    private fun getSourceToRealm(source: Source?): SourceRealm?{
        if(source == null) return null
        val sourceRealm = realm.createObject(SourceRealm::class.java)
        sourceRealm.page = source.page
        sourceRealm.photoId = source.photoId
        sourceRealm.srcDescription = source.srcDescription
        sourceRealm.url = source.url
        return sourceRealm
    }

    private fun getPositionToRealm(position: Position?): PositionRealm?{
        if(position == null) return null
        val positionRealm = realm.createObject(PositionRealm::class.java)
        positionRealm.lat = position.lat
        positionRealm.lng = position.lng
        return positionRealm
    }

    private fun getTourFromRealm(tourRealm: TourRealm?): Tour?{
        if(tourRealm == null) return null
        val tour = Tour(tourRealm.title)
        val photosArticlesList = mutableListOf<PhotoArticle>()
        tourRealm.photosArticlesList!!.forEach{
            photosArticlesList.add(getPhotoArticleFromRealm(it))
        }
        tour.photosArticlesList = photosArticlesList.toList()
        return tour
    }

    override fun getPhotoArticleFromRealm(photoArticleRealm: PhotoArticleRealm?): PhotoArticle =
        PhotoArticle(
            photoArticleRealm?.objectId,
            photoArticleRealm?.objectType!!,
            photoArticleRealm.yearOfBuild,
            photoArticleRealm.cityKey,
            photoArticleRealm.title,
            photoArticleRealm.shortTitle,
            photoArticleRealm.zoom!!,
            getPositionFromRealm(photoArticleRealm.positionRealm),
            getHeaderFromRealm(photoArticleRealm.headerRealm),
            getParagraphFromRealm(photoArticleRealm.paragraphRealm),
            getPhotoFromRealm(photoArticleRealm.photoRealm),
            getSourceFromRealm(photoArticleRealm.sourceRealm))

    private fun getHeaderFromRealm(headerRealm: HeaderRealm?): Header?{
        if(headerRealm == null) return null
        val header = Header(hashMapOf())
        for(line in headerRealm.content!!)
            header.content!![line.firstValue!!] = line.secondValue!!
        return header
    }

    private fun getPhotoFromRealm(photoRealm: PhotoRealm?) =
        Photo(photoRealm?.objectId, photoRealm?.numberOfParagraph, photoRealm?.description,
            getSourceFromRealm(photoRealm?.sourceRealm))

    private fun getParagraphFromRealm(paragraphRealm: ParagraphRealm?) =
        Paragraph(paragraphRealm?.subtitle, paragraphRealm?.content,
            getSourceFromRealm(paragraphRealm?.sourceRealm))

    private fun getSourceFromRealm(sourceRealm: SourceRealm?): Source?{
        return if(sourceRealm?.url == null) null
        else Source(sourceRealm.srcDescription, sourceRealm.photoId, sourceRealm.url, sourceRealm.page)
    }

    private fun getPositionFromRealm(positionRealm: PositionRealm?): Position? {
        return if(positionRealm?.lat == null) null
        else Position(positionRealm.lat, positionRealm.lng)
    }
}