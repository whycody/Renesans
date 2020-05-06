package pl.renesans.renesans.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import pl.renesans.renesans.data.PhotoArticle

class ClusterMarker(val photoArticle: PhotoArticle): ClusterItem, ClusterView {

    override fun getSnippet(): String {
        return if(photoArticle.paragraph?.content != null) photoArticle.paragraph?.content!!
        else "Snippet"
    }

    override fun getTitle(): String {
        return if(photoArticle.title != null) photoArticle.title!!.toUpperCase()
        else "Title"
    }

    override fun getPosition(): LatLng {
        return if(photoArticle.latLng != null) photoArticle.latLng!!
        else LatLng(1.0, 1.0)
    }

    override fun getCLusterType(): Int {
        return photoArticle.objectType
    }
}