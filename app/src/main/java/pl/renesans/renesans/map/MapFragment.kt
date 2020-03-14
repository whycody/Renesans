package pl.renesans.renesans.map

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager

import pl.renesans.renesans.R
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.data.PhotoArticle

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    ClusterManager.OnClusterItemClickListener<ClusterMarker>, GoogleMap.CancelableCallback {

    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var clusterManager: ClusterManager<ClusterMarker>? = null
    private var clusterManagerRenderer: ClusterManagerRenderer? = null
    private var markersList = mutableListOf<ClusterMarker>()
    private val zoomLevel = 12f
    private val animatingCamera = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setUiSettings()
        prepareManagers()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location!=null) moveMapToLastLocation(location)
            else moveMapToOlsztyn()
        }.addOnFailureListener{ moveMapToOlsztyn() }
        googleMap.setOnCameraMoveListener(this)
        moveMapToOlsztyn()
    }

    private fun setUiSettings(){
        googleMap?.uiSettings?.isMapToolbarEnabled = false
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.isCompassEnabled = false
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
    }

    private fun prepareManagers(){
        if(clusterManager == null) clusterManager = ClusterManager(activity, googleMap)
        if(clusterManagerRenderer == null){
            clusterManagerRenderer = ClusterManagerRenderer(activity!!, googleMap!!, clusterManager!!)
            clusterManagerRenderer?.prepareMarker()
            clusterManager?.renderer = clusterManagerRenderer
            clusterManager?.setOnClusterItemClickListener(this)
            addExampleMarkers()
        }
    }

    private fun addExampleMarkers(){
        addMarker(ClusterMarker(PhotoArticle(title = "Kaplica Mariacka"), LatLng(53.775711, 20.477980)))
        addMarker(ClusterMarker(PhotoArticle(title = "Zamek Królewski"), LatLng(53.760, 20.475)))
    }

    private fun addMarker(clusterMarker: ClusterMarker){
        clusterMarker.photoArticle.photo = Photo(describe = "Zamek Królewski na Wawelu")
        clusterMarker.photoArticle.paragraph = Paragraph(content = "Budowla była na przestrzeni wieków wielokrotnie rozbudowywana i odnawiana. Zamek wielokrotnie był poddawany różnym próbom takim jak pożary, grabieże i przemarsze obcych wojsk wobec czego był wielokrotnie był odbudowywany w kolejnych nowych stylach architektonicznych.")
        markersList.add(clusterMarker)
        clusterManager?.addItem(clusterMarker)
        clusterManager?.cluster()
    }

    private fun moveMapToLastLocation(location: Location){
        val latitude = location.latitude
        val longitude = location.longitude
        val lastLocationLatLng = LatLng(latitude, longitude)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLatLng, zoomLevel))
    }

    private fun moveMapToOlsztyn(){
        val olsztyn = LatLng(53.775711, 20.477980)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(olsztyn, zoomLevel))
    }

    override fun onCameraMove() {
        if(clusterManager!=null && clusterManager?.markerCollection!=null){
            for(marker in clusterManager!!.markerCollection.markers)
                marker.isVisible = googleMap?.cameraPosition!!.zoom > 10.0f
        }
    }

    private var lastClusterMarker: ClusterMarker? = null

    override fun onClusterItemClick(p0: ClusterMarker?): Boolean {
        if(animatingCamera)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLng(p0?.position), 200, this)
        else
            PhotoBottomSheetDialog(p0?.photoArticle)
                .show(activity!!.supportFragmentManager, "photoBottomSheetDialog")
        lastClusterMarker = p0
        return true
    }

    override fun onFinish() {
        PhotoBottomSheetDialog(lastClusterMarker?.photoArticle)
            .show(activity!!.supportFragmentManager, "photoBottomSheetDialog")
    }

    override fun onCancel() {

    }

}
