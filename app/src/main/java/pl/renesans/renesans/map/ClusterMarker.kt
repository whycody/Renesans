package pl.renesans.renesans.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import pl.renesans.renesans.data.PhotoArticle

class ClusterMarker(val photoArticle: PhotoArticle): ClusterItem, ClusterView {

    override fun getSnippet(): String {
        return photoArticle.objectType.toString()
    }

    override fun getTitle(): String {
        return if(photoArticle.shortTitle != null) photoArticle.shortTitle!!.toUpperCase()
        else photoArticle.title!!.toUpperCase()
    }

    override fun getPosition(): LatLng {
        return if(photoArticle.latLng != null) photoArticle.latLng!!
        else LatLng(1.0, 1.0)
    }

    override fun getClusterType(): Int {
        return photoArticle.objectType
    }

    override fun getFullTitle(): String {
        return photoArticle.title!!
    }
}