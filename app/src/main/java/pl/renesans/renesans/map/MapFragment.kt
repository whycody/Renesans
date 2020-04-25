package pl.renesans.renesans.map

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.fragment_map.view.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.map.recycler.LocationAdapter
import pl.renesans.renesans.map.recycler.LocationPresenterImpl
import pl.renesans.renesans.map.recycler.LocationRecyclerDecoration
import pl.renesans.renesans.settings.SettingsPresenterImpl


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    ClusterManager.OnClusterItemClickListener<ClusterMarker>, GoogleMap.CancelableCallback, MapView {

    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var clusterManager: ClusterManager<ClusterMarker>? = null
    private var clusterManagerRenderer: ClusterManagerRenderer? = null
    private val markersList = mutableListOf<ClusterMarker>()
    private val zoomLevel = 12f
    private var cameraAnimations = false
    private var presenter: LocationPresenterImpl? = null
    private var adapter: LocationAdapter? = null
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        presenter = LocationPresenterImpl(this, activity!!)
        adapter = LocationAdapter(presenter!!, activity!!)
        sharedPrefs = context!!.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        view.locationRecycler.adapter = adapter
        view.locationRecycler.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
        view.locationRecycler.addItemDecoration(LocationRecyclerDecoration(activity!!))
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setUiSettings()
        prepareManagers()
        prepareFusedLocationClient()
        googleMap.setOnCameraMoveListener(this)
        presenter?.addMarkers()
        presenter?.setLocationManager()
    }

    private fun setUiSettings(){
        googleMap?.uiSettings?.isMapToolbarEnabled = false
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.isCompassEnabled = false
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
    }

    private fun prepareFusedLocationClient(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location!=null) moveMapToLastLocation(location)
            else moveMapToOlsztyn()
            refreshLocationMarkersList()
        }.addOnFailureListener{ moveMapToOlsztyn() }
    }

    private fun prepareManagers(){
        if(clusterManager == null) clusterManager = ClusterManager(activity, googleMap)
        if(clusterManagerRenderer == null){
            clusterManagerRenderer = ClusterManagerRenderer(activity!!, googleMap!!, clusterManager!!)
            clusterManagerRenderer?.prepareMarker()
            clusterManager?.renderer = clusterManagerRenderer
            clusterManager?.setOnClusterItemClickListener(this)
        }
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
        refreshLocationMarkersList()
    }

    private fun refreshLocationMarkersList(){
        val bounds = googleMap?.projection?.visibleRegion?.latLngBounds
        val newList = getListOfVisibleMarkers(bounds!!)
        refreshRecyclerView(newList)
    }

    private fun getListOfVisibleMarkers(bounds: LatLngBounds): MutableList<ClusterMarker>{
        val newList = mutableListOf(ClusterMarker(PhotoArticle()))
        for(marker in markersList)
            if(bounds.contains(marker.position) && googleMap?.cameraPosition!!.zoom > 10f)
                newList.add(marker)
        return newList
    }

    private fun refreshRecyclerView(newList: MutableList<ClusterMarker>){
        if(!presenter?.getItemCount()?.equals(newList.size)!!) {
            presenter?.refreshMarkersList(newList)
            adapter?.notifyDataSetChanged()
        }
    }

    private var lastClusterMarker: ClusterMarker? = null

    override fun onClusterItemClick(p0: ClusterMarker?): Boolean {
        cameraAnimations = sharedPrefs.getBoolean(SettingsPresenterImpl.MAP_ANIMATIONS, false)
        if(cameraAnimations)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLng(p0?.position), 400, this)
        else openMarkerBottomSheet(p0)
        lastClusterMarker = p0
        return true
    }

    override fun onFinish() {
        openMarkerBottomSheet(lastClusterMarker)
    }

    override fun onCancel() {

    }

    private fun openMarkerBottomSheet(clusterMarker: ClusterMarker?) {
        if(clusterMarker!=null) PhotoBottomSheetDialog(clusterMarker.photoArticle)
            .show(activity!!.supportFragmentManager, "photoBottomSheetDialog")
    }

    override fun addClusterMarkerToMap(clusterMarker: ClusterMarker) {
        addMarker(clusterMarker)
    }

    override fun moveToLocation(location: LatLng?) {
        if(location!=null){
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 16f)
            googleMap?.animateCamera(cameraUpdate)
        }
    }

    override fun turnOnMyLocationOnMap() {
        googleMap?.isMyLocationEnabled = true
    }

    private fun addMarker(clusterMarker: ClusterMarker){
        markersList.add(clusterMarker)
        clusterManager?.addItem(clusterMarker)
        clusterManager?.cluster()
        adapter?.notifyDataSetChanged()
    }

}
