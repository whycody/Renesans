package pl.renesans.renesans.map

interface MapView {

    fun openMarkerBottomSheet(clusterMarker: ClusterMarker)

    fun addClusterMarkerToMap(clusterMarker: ClusterMarker)
}