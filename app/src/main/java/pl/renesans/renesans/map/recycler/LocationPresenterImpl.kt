package pl.renesans.renesans.map.recycler

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.map.ClusterMarker
import pl.renesans.renesans.map.MapView

class LocationPresenterImpl(val mapView: MapView? = null, val activity: Activity): LocationPresenter {

    private var photoArticlesList = mutableListOf(ClusterMarker(PhotoArticle()))
    private val examplePhotoDescribe = Photo(describe = "Zamek Królewski na Wawelu")
    private val exampleParagraph = Paragraph(content = "Budowla była na przestrzeni wieków wielokrotnie rozbudowywana i odnawiana. Zamek wielokrotnie był poddawany różnym próbom takim jak pożary, grabieże i przemarsze obcych wojsk wobec czego był wielokrotnie był odbudowywany w kolejnych nowych stylach architektonicznych.")

    override fun onCreate() {
        addExampleMarkers()
    }

    private fun addExampleMarkers(){
        val firstCluster = ClusterMarker(PhotoArticle(title = "Kaplica Mariacka",
            latLng = LatLng(53.775711, 20.477980), paragraph = exampleParagraph,
            photo = examplePhotoDescribe))
        val secondCluster = ClusterMarker(PhotoArticle(title = "Zamek Królewski",
            latLng = LatLng(53.760, 20.475), paragraph = exampleParagraph,
            photo = examplePhotoDescribe))
        photoArticlesList.add(firstCluster)
        photoArticlesList.add(secondCluster)
        mapView?.addClusterMarkerToMap(firstCluster)
        mapView?.addClusterMarkerToMap(secondCluster)
    }

    override fun refreshMarkersList(photoArticlesList: MutableList<ClusterMarker>) {
        this.photoArticlesList = photoArticlesList
    }

    override fun getMarkersList(): MutableList<ClusterMarker> {
        return photoArticlesList
    }

    override fun itemClicked(pos: Int) {
        if(pos!=0) mapView?.openMarkerBottomSheet(photoArticlesList[pos])
        else checkLocationPermission()
    }

    private fun checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun getItemCount(): Int {
        return photoArticlesList.size
    }

    override fun onBindViewHolder(holder: LocationRowHolder, position: Int) {
        resetVariables(holder)
        if(position == 0){
            holder.setText(activity.getString(R.string.my_location))
            holder.setDrawable(activity.getDrawable(R.drawable.sh_my_location_row)!!)
            holder.setTextColor(R.color.colorGray)
        }else holder.setText(photoArticlesList[position].title)
        holder.setOnRowClickListener(position)
    }

    private fun resetVariables(holder: LocationRowHolder){
        holder.setText(" ")
        holder.setTextColor(Color.WHITE)
        holder.setDrawable(activity.getDrawable(R.drawable.sh_location_row)!!)
        holder.setOnRowClickListener(0)
    }
}