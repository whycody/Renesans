package pl.renesans.renesans.map.recycler

import com.google.android.gms.maps.model.LatLng
import pl.renesans.renesans.map.ClusterMarker

interface LocationPresenter {

    fun addMarkers()

    fun refreshMarkersList(photoArticlesList: MutableList<ClusterMarker>)

    fun getMarkersList(): MutableList<ClusterMarker>

    fun getCurrentLocation(): LatLng?

    fun itemClicked(pos: Int)

    fun setLocationManager()

    fun getItemCount(): Int

    fun onBindViewHolder(holder: LocationRowHolder, position: Int)

}