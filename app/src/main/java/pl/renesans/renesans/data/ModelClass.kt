package pl.renesans.renesans.data

import android.graphics.drawable.Drawable
import pl.renesans.renesans.data.article.ArticleDaoImpl
import java.io.Serializable

data class Source(var srcDescription: String? = null,
                  var photoId: String? = "Z1_0",
                  var url: String? = null,
                  var page: String? = null): Serializable

data class Bookmark(var mode: String? = null,
                    var photoId: String? = null,
                    var bookmarkTitle: String? = null,
                    var articleId: String? = null,
                    var bookmarkDescription: String? = null): Serializable

data class Photo(var objectId: String? = null,
                 var numberOfParagraph: Int? = null,
                 var description: String? = null,
                 var source: Source? = null): Serializable

data class Paragraph(var subtitle: String? = null,
                var content: String? = null,
                var source: Source? = null): Serializable

data class Header(var content: HashMap<String, String>? = null): Serializable

data class ArticlesList(var id: String? = null,
                        var type: String? = null,
                        var name: String? = null,
                        var index: Int? = null,
                        var objectType: Int? = null): Serializable

data class Article(var objectId: String? = null,
                   var objectType: Int? = null,
                   var typeOfScaling: Int? = null,
                   var title: String? = null,
                   var index: Int? = null,
                   var header: Header? = null,
                   var source: Source? = null,
                   var listOfRelatedArticlesIds: List<String>? = null,
                   var listOfParagraphs: List<Paragraph>? = null,
                   var listOfPhotos: List<Photo>? = null,
                   var listOfPositions: List<Position>? = null,
                   var tour: Tour? = null): Serializable

data class PhotoArticle(var objectId: String? = null,
                        var objectType: Int = ArticleDaoImpl.PLACE_TYPE,
                        var yearOfBuild: Int? = null,
                        var cityKey: String? = null,
                        var title: String? = null,
                        var shortTitle: String? = null,
                        var zoom: Float = 15f,
                        var position: Position? = null,
                        var header: Header? = null,
                        var paragraph: Paragraph? = null,
                        var photo: Photo? = null,
                        var source: Source? = null): Serializable

data class ArticleItem(var objectId: String? = null,
                var title: String? = null,
                var searchHistoryItem: Boolean = false)

data class SettingsList(var description: String? = null,
                        var settings: MutableList<Setting> = mutableListOf())

data class Setting(var settingId: String? = null,
                   var settingTitle: String? = null,
                   var settingDescription: String? = null,
                   var booleanValue: Boolean = true,
                   var defaultValue: Boolean = false,
                   var listOfOptions: List<SettingListItem>? = null,
                   var defaultSettingsItemPos: Int = 0,
                   var imageDrawable: Drawable? = null,
                   var listDescription: String? = null)

data class SettingListItem(var settingsItemId: Int? = null,
                var settingItemTitle: String? = null,
                var settingItemDescription: String? = null)

data class Tour(var title: String? = null,
                var photosArticlesList: List<PhotoArticle>? = null) : Serializable

data class Suggestion(var objectId: String? = null,
                var numberOfParagraph: Int? = null,
                var paragraphSubtitle: String? = null,
                var paragraphContent: String? = null,
                var comment: String? = null)

data class LastCameraPosition(var lat: Double? = null,
                              var lng: Double? = null,
                              var cameraZoom: Float? = null,
                              var tilt: Float? = null,
                              var bearing: Float? = null): Serializable

data class Position(var lat: Double? = null,
                    var lng: Double? = null,
                    var title: String? = null): Serializable

data class DatabaseVersion(var version: Int? = null): Serializable
