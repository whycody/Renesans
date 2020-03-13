package pl.renesans.renesans.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusterMarker(val tit: String, val pos: LatLng): ClusterItem {

    override fun getSnippet(): String {
        return "None"
    }

    override fun getTitle(): String {
        return tit
    }

    override fun getPosition(): LatLng {
        return pos
    }
}