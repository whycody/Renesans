package pl.renesans.renesans.map

import com.google.android.gms.maps.model.LatLng

interface MapView {

    fun openMarkerBottomSheet(clusterMarker: ClusterMarker)

    fun addClusterMarkerToMap(clusterMarker: ClusterMarker)

    fun moveToPosition(location: LatLng?)

    fun turnOnMyLocationOnMap()
}