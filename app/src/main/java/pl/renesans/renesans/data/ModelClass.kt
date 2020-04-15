package pl.renesans.renesans.data

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class Source(var sourceType: String? = null,
                  var srcDescription: String? = null,
                  var photoId: String? = "Z1_0",
                  var url: String? = null,
                  var page: String? = null): Serializable

data class Photo(var objectId: String? = null,
                 var objectType: Int? = null,
                 var numberOfParagraph: Int? = null,
                 var description: String? = null,
                 var source: Source? = null): Serializable

data class Paragraph(var objectId: String? = null,
                var objectType: Int? = null,
                var subtitle: String? = null,
                var content: String? = null): Serializable

data class Header(var objectId: String? = null,
                var objectType: String? = null,
                var title: String? = null,
                var content: List<Pair<String, String>>? = null): Serializable

data class Article(var objectId: String? = null,
                   var objectType: Int? = null,
                   var title: String? = null,
                   var header: Header? = null,
                   var source: Source? = null,
                   var listOfRelatedArticlesIds: List<String>? = null,
                   var listOfParagraphs: List<Paragraph>? = null,
                   var listOfPhotos: List<Photo>? = null): Serializable

data class PhotoArticle(var objectId: String? = null,
                var objectType: Int? = null,
                var title: String? = null,
                var latLng: LatLng? = null,
                var header: Header? = null,
                var paragraph: Paragraph? = null,
                var photo: Photo? = null,
                var listOfSources: List<Source>? = null): Serializable

