package pl.renesans.renesans.map.recycler

import android.content.Context
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.map.ClusterMarker
import pl.renesans.renesans.map.MapView

class LocationPresenterImpl(val mapView: MapView? = null, val context: Context): LocationPresenter {

    private val photoArticles = mutableListOf(ClusterMarker(PhotoArticle()))
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
        mapView?.addClusterMarkerToMap(firstCluster)
        mapView?.addClusterMarkerToMap(secondCluster)
    }

    override fun itemClicked(pos: Int) {
        if(pos!=0) mapView?.openMarkerBottomSheet(photoArticles[pos])
        else Toast.makeText(context, "Włącz lokalizację", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return photoArticles.size
    }

    override fun onBindViewHolder(holder: LocationRowHolder, position: Int) {
        if(position == 0){
            holder.setText("Moja lokalizacja")
            holder.setDrawable(context.getDrawable(R.drawable.sh_disabled_my_location_row)!!)
        }else holder.setText(photoArticles[position].title)
        holder.setOnRowClickListener(position)
    }
}