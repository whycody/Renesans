package pl.renesans.renesans.data.realm

import android.content.Context
import io.realm.Realm
import pl.renesans.renesans.data.*

class RealmMapperImpl(private val context: Context): RealmMapper {

    private lateinit var realm: Realm

    override fun onCreate() {
        Realm.init(context)
        realm = Realm.getDefaultInstance()
    }

    override fun getArticlesListFromRealm(articlesListRealm: ArticlesListRealm): ArticlesList {
        TODO("Not yet implemented")
    }

    override fun getArticlesListToRealm(articlesList: ArticlesList): ArticlesListRealm {
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
    }

    override fun getArticleFromRealm(articleRealm: ArticleRealm): Article {
        TODO("Not yet implemented")
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

    private fun getPhotoToRealm(photo: Photo?): PhotoRealm{
        val photoRealm = realm.createObject(PhotoRealm::class.java)
        photoRealm.description = photo?.description
        photoRealm.numberOfParagraph = photo?.numberOfParagraph
        photoRealm.objectId = photo?.objectId
        photoRealm.sourceRealm = getSourceToRealm(photo?.source)
        return photoRealm
    }

    private fun getParagraphToRealm(paragraph: Paragraph?): ParagraphRealm{
        val paragraphRealm = realm.createObject(ParagraphRealm::class.java)
        paragraphRealm.content = paragraph?.content
        paragraphRealm.subtitle = paragraph?.subtitle
        paragraphRealm.sourceRealm = getSourceToRealm(paragraph?.source)
        return paragraphRealm
    }

    private fun getSourceToRealm(source: Source?): SourceRealm{
        val sourceRealm = realm.createObject(SourceRealm::class.java)
        sourceRealm.page = source?.page
        sourceRealm.photoId = source?.photoId
        sourceRealm.srcDescription = source?.srcDescription
        sourceRealm.url = source?.url
        return sourceRealm
    }

    private fun getPositionToRealm(position: Position?): PositionRealm{
        val positionRealm = realm.createObject(PositionRealm::class.java)
        positionRealm.lat = position?.lat
        positionRealm.lng = position?.lng
        return positionRealm
    }

    private fun getPhotoFromRealm(photoRealm: PhotoRealm) =
        Photo(photoRealm.objectId, photoRealm.numberOfParagraph, photoRealm.description,
            getSourceFromRealm(photoRealm.sourceRealm))

    private fun getParagraphFromRealm(paragraphRealm: ParagraphRealm) =
        Paragraph(paragraphRealm.subtitle, paragraphRealm.content,
            getSourceFromRealm(paragraphRealm.sourceRealm))

    private fun getSourceFromRealm(sourceRealm: SourceRealm?) =
        Source(sourceRealm?.srcDescription, sourceRealm?.photoId, sourceRealm?.url, sourceRealm?.page)

    private fun getPositionFromRealm(positionRealm: PositionRealm?) =
        Position(positionRealm?.lat, positionRealm?.lng)
}