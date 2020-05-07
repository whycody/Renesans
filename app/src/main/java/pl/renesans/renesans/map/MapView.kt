package pl.renesans.renesans.map

import com.google.android.gms.maps.model.LatLng

interface MapView {

    fun reloadMap()

    fun addClusterMarkerToMap(clusterMarker: ClusterMarker)

    fun onClusterItemClick(p0: ClusterMarker?): Boolean

    fun moveToLocation(location: LatLng?, zoom: Float = 16f)

    fun onCameraMove()

    fun turnOnMyLocationOnMap()
}