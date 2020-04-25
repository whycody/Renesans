package pl.renesans.renesans.map

import com.google.android.gms.maps.model.LatLng

interface MapView {

    fun addClusterMarkerToMap(clusterMarker: ClusterMarker)

    fun onClusterItemClick(p0: ClusterMarker?): Boolean

    fun moveToLocation(location: LatLng?)

    fun turnOnMyLocationOnMap()
}