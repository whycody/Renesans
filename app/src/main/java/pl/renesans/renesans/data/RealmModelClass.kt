package pl.renesans.renesans.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import pl.renesans.renesans.data.article.ArticleDaoImpl
import java.io.Serializable

open class ArticlesListRealm(var id: String? = null,
                             var type: String? = null,
                             var name: String? = null,
                             var index: Int? = null,
                             var objectType: Int? = null): RealmObject()

open class ArticleRealm(var objectId: String? = null,
                        var sourceRealm: SourceRealm? = null,
                        var headerRealm: HeaderRealm? = null,
                        var objectType: Int? = null,
                        var typeOfScaling: Int? = null,
                        var title: String? = null,
                        var index: Int? = null,
                        var tourRealm: TourRealm? = null,
                        var listOfRelatedArticlesIds: RealmList<String>? = null,
                        var listOfParagraphs: RealmList<ParagraphRealm>? = null,
                        var listOfPhotos: RealmList<PhotoRealm>? = null,
                        var listOfPositions: RealmList<PositionRealm>? = null): RealmObject()

open class PhotoArticleRealm(var objectId: String? = null,
                        var objectType: Int? = ArticleDaoImpl.PLACE_TYPE,
                        var yearOfBuild: Int? = null,
                        var cityKey: String? = null,
                        var title: String? = null,
                        var shortTitle: String? = null,
                        var zoom: Float? = 15f,
                        var positionRealm: PositionRealm? = null,
                        var headerRealm: HeaderRealm? = null,
                        var paragraphRealm: ParagraphRealm? = null,
                        var photoRealm: PhotoRealm? = null,
                        var sourceRealm: SourceRealm? = null): RealmObject()

open class TourRealm(var title: String? = null,
                var photosArticlesList: RealmList<PhotoArticleRealm>? = null): RealmObject()

open class PositionRealm(var lat: Double? = null,
                    var lng: Double? = null): RealmObject()

open class PhotoRealm(var objectId: String? = null,
                 var numberOfParagraph: Int? = null,
                 var description: String? = null,
                 var sourceRealm: SourceRealm? = null): RealmObject()

open class HeaderRealm(var content: RealmList<HeaderLine>? = null): RealmObject()

open class HeaderLine(var firstValue: String? = null,
                var secondValue: String? = null): RealmObject()

open class SourceRealm(var srcDescription: String? = null,
                  var photoId: String? = "Z1_0",
                  var url: String? = null,
                  var page: String? = null): RealmObject()

open class ParagraphRealm(var subtitle: String? = null,
                     var content: String? = null,
                     var sourceRealm: SourceRealm? = null): RealmObject()

open class DatabaseVersionRealm(var version: Int? = null): RealmObject()

open class SearchHistoryRealm(var listOfIdsOfLastSearchedItems: RealmList<String>? = null): RealmObject()