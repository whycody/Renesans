package pl.renesans.renesans.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import pl.renesans.renesans.data.PhotoArticle

class ClusterMarker(val photoArticle: PhotoArticle): ClusterItem, ClusterView {

    override fun getSnippet() = photoArticle.objectType.toString()

    override fun getTitle(): String {
        return if(photoArticle.shortTitle != null) photoArticle.shortTitle!!.toUpperCase()
        else photoArticle.title!!.toUpperCase()
    }

    override fun getPosition(): LatLng {
        return if(photoArticle.position != null)
            LatLng(photoArticle.position!!.lat!!, photoArticle.position!!.lng!!)
        else LatLng(1.0, 1.0)
    }

    override fun getClusterType() = photoArticle.objectType

    override fun getFullTitle() = photoArticle.title!!
}