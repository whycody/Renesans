package pl.renesans.renesans.map.recycler

import pl.renesans.renesans.map.ClusterMarker

interface LocationPresenter {

    fun onCreate()

    fun refreshMarkersList(photoArticlesList: MutableList<ClusterMarker>)

    fun getMarkersList(): MutableList<ClusterMarker>

    fun itemClicked(pos: Int)

    fun getItemCount(): Int

    fun onBindViewHolder(holder: LocationRowHolder, position: Int)

}