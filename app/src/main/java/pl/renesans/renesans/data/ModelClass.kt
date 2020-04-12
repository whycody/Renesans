package pl.renesans.renesans.data

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class Source(var sourceType: String? = null,
                var url: String? = null,
                var page: String? = null,
                var imageSource: String? = null): Serializable

data class Photo(var objectId: String? = null,
                var objectType: String? = null,
                var numberOfParagraph: Int? = null,
                var describe: String? = null,
                var path: String? = null,
                var source: Source? = null): Serializable

data class Paragraph(var objectId: String? = null,
                var objectType: Int? = null,
                var subtitle: String? = null,
                var content: String? = null): Serializable

data class Header(var objectId: String? = null,
                var objectType: String? = null,
                var title: String? = null,
                var content: List<Pair<String, String>>? = null): Serializable

data class RelationArticle(var relatedId: String? = null,
                var relatedType: String? = null,
                var relatedPhoto: Photo? = null,
                var relatedTitle: String? = null,
                var listOfSources: List<Source>? = null): Serializable

data class Article(var objectId: String? = null,
                var objectType: String? = null,
                var title: String? = null,
                var header: Header? = null,
                var listOfParagraphs: List<Paragraph>? = null,
                var listOfPhotos: List<Photo>? = null,
                var listOfSources: List<Source>? = null): Serializable

data class PhotoArticle(var objectId: String? = null,
                var objectType: String? = null,
                var title: String? = null,
                var latLng: LatLng? = null,
                var header: Header? = null,
                var paragraph: Paragraph? = null,
                var photo: Photo? = null,
                var listOfSources: List<Source>? = null): Serializable

