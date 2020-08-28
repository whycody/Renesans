package pl.renesans.renesans.map

import android.app.ActivityManager
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.fragment_map.view.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.LastCameraPosition
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.data.article.ArticleDaoImpl
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
    private var markersList = mutableListOf<ClusterMarker>()
    private val zoomLevel = 6.2f
    private val limitOfZoom = 11f
    private var cameraAnimations = false
    private var presenter: LocationPresenterImpl? = null
    private var adapter: LocationAdapter? = null
    private var limitOfMapFunctionality = true
    private var sharedPrefs: SharedPreferences? = null
    private var currentZoomIsMin = false
    private var animateCameraToLastMarker = false
    private var cameraPos: CameraPosition? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkBundle(savedInstanceState)
        sharedPrefs = context?.applicationContext?.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        limitOfMapFunctionality = sharedPrefs!!
            .getBoolean(SettingsPresenterImpl.MAP_FUNCTIONALITIES, !freeRamMemoryIsEnough())
        presenter = LocationPresenterImpl(this, activity!!)
        adapter = LocationAdapter(presenter!!, activity!!)
        view.locationRecycler.adapter = adapter
        view.locationRecycler.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
        view.locationRecycler.addItemDecoration(LocationRecyclerDecoration(activity!!))
        return view
    }

    private fun checkBundle(savedInstanceState: Bundle?){
        if(savedInstanceState?.getSerializable("lastPosition") != null){
            val lastCameraPos =
                savedInstanceState.getSerializable("lastPosition") as LastCameraPosition
            cameraPos = CameraPosition(LatLng(lastCameraPos.lat!!, lastCameraPos.lng!!),
                lastCameraPos.cameraZoom!!, lastCameraPos.tilt!!, lastCameraPos.bearing!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(googleMap!=null){
            val lastCameraPosition = getLastCameraPosition()
            outState.putSerializable("lastPosition", lastCameraPosition)
        }
    }

    private fun getLastCameraPosition(): LastCameraPosition{
        return LastCameraPosition(
            googleMap!!.cameraPosition.target.latitude,
            googleMap!!.cameraPosition.target.longitude,
            googleMap!!.cameraPosition.zoom,
            googleMap!!.cameraPosition.tilt,
            googleMap!!.cameraPosition.bearing)
    }

    private fun freeRamMemoryIsEnough(): Boolean {
        val mi = ActivityManager.MemoryInfo()
        val activityManager =
            activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        return (mi.availMem / 1048576L) >= 400
    }

    override fun reloadMap() {
        googleMap?.clear()
        clusterManager?.clearItems()
        markersList.clear()
        presenter?.addMarkers()
    }

    override fun changedOptionOfMapLimit() {
        limitOfMapFunctionality = sharedPrefs!!
            .getBoolean(SettingsPresenterImpl.MAP_FUNCTIONALITIES, freeRamMemoryIsEnough())
        if(limitOfMapFunctionality) refreshLocationMarkersListWithCities()
        else refreshLocationMarkersList()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(googleMap.cameraPosition.target, zoomLevel))
        setUiSettings()
        prepareManagers()
        googleMap.setOnCameraMoveListener(this)
        presenter?.addMarkers()
        presenter?.setLocationManager()
        if(animateCameraToLastMarker) animateCameraToBookmarkMarker()
        else prepareFusedLocationClient()
    }

    private fun setUiSettings(){
        googleMap?.uiSettings?.isMapToolbarEnabled = false
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.isCompassEnabled = false
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style))
    }

    private fun prepareFusedLocationClient(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location!=null) moveMapToLastLocation(location)
            else moveMapToSavedLocation()
            refreshLocationMarkersList()
        }.addOnFailureListener{ moveMapToSavedLocation() }
    }

    private fun prepareManagers(){
        if(clusterManager == null) clusterManager = ClusterManager(activity, googleMap)
        if(clusterManagerRenderer == null){
            clusterManagerRenderer = ClusterManagerRenderer(activity!!, googleMap!!, clusterManager!!, this)
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

    private fun moveMapToSavedLocation(){
        if(cameraPos!=null) googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos))
        else moveMapToPoland()
    }

    private fun moveMapToPoland(){
        val poland = LatLng(52.069322, 19.480311)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(poland, zoomLevel))
    }

    override fun onCameraMove() {
        if((currentZoomIsMin && googleMap?.cameraPosition!!.zoom >= limitOfZoom) ||
            (!currentZoomIsMin && googleMap?.cameraPosition!!.zoom <= limitOfZoom))
            refreshMap()
        if(!limitOfMapFunctionality || presenter?.getMarkersList()?.size == 1)
            refreshLocationMarkersList()
        else if (limitOfMapFunctionality) refreshLocationMarkersListWithCities()
    }

    override fun refreshMap() {
        if(clusterManager!=null && clusterManager?.markerCollection!=null) {
            clusterManager!!.markerCollection.markers.forEach { marker ->
                var objectType = ArticleDaoImpl.PLACE_TYPE
                if (marker.snippet != null) objectType = marker.snippet.toInt()
                if (objectType == ArticleDaoImpl.PLACE_TYPE)
                    marker.isVisible = googleMap?.cameraPosition!!.zoom > limitOfZoom
                else marker.isVisible = googleMap?.cameraPosition!!.zoom <= limitOfZoom
            }
        }
        currentZoomIsMin = googleMap?.cameraPosition!!.zoom <= limitOfZoom
    }

    private fun refreshLocationMarkersList(){
        val bounds = googleMap?.projection?.visibleRegion?.latLngBounds ?: return
        val newList = getListOfVisibleMarkers(bounds)
        if(newList!=null) refreshRecyclerView(newList)
    }

    private fun refreshLocationMarkersListWithCities(){
        val listOfCities = mutableListOf(ClusterMarker(PhotoArticle()))
        presenter?.getPhotoArticles()?.forEach { article ->
            if(article.objectType == ArticleDaoImpl.CITY_TYPE)
                listOfCities.add(ClusterMarker(article))
        }
        refreshRecyclerView(listOfCities)
    }

    private fun getListOfVisibleMarkers(bounds: LatLngBounds): MutableList<ClusterMarker>?{
        val newList = mutableListOf(ClusterMarker(PhotoArticle()))
        for(marker in markersList){
            if(marker.getClusterType() == ArticleDaoImpl.PLACE_TYPE &&
                bounds.contains(marker.position) && googleMap?.cameraPosition!!.zoom >= limitOfZoom)
                newList.add(marker)
            else if (marker.getClusterType() == ArticleDaoImpl.CITY_TYPE &&
                bounds.contains(marker.position) && googleMap?.cameraPosition!!.zoom <= limitOfZoom)
                newList.add(marker)
        }
        return newList
    }

    private fun refreshRecyclerView(newList: MutableList<ClusterMarker>){
        if(!listsEqual(presenter?.getMarkersList()!!, newList)) {
            val refreshedList: MutableList<ClusterMarker> = presenter?.getMarkersList()!!.toMutableList()
            for (marker in newList)
                if (!presenter?.getMarkersList()!!.contains(marker)
                    && marker.photoArticle.objectId != null) refreshedList.add(marker)
            for (marker in presenter?.getMarkersList()!!)
                if (!newList.contains(marker)
                    && marker.photoArticle.objectId != null) refreshedList.remove(marker)
            presenter?.refreshMarkersList(refreshedList)
            adapter?.notifyDataSetChanged()
        }
    }

    private fun listsEqual(list1: List<ClusterMarker>, list2: List<ClusterMarker>): Boolean {
        if (list1.size != list2.size) return false
        list1.forEach{ if(it.photoArticle.objectId != null && !list2.contains(it)) return false }
        return true
    }

    private var lastClusterMarker: ClusterMarker? = null

    override fun onClusterItemClick(p0: ClusterMarker?): Boolean {
        lastClusterMarker = p0
        cameraAnimations = sharedPrefs!!.getBoolean(SettingsPresenterImpl.MAP_ANIMATIONS, false)
        if(cameraAnimations && p0?.photoArticle?.objectType == ArticleDaoImpl.PLACE_TYPE)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLng(p0.position), 400, this)
        else if(p0?.photoArticle?.objectType == ArticleDaoImpl.BOOKMARK_TYPE){
            if(googleMap != null) animateCameraToBookmarkMarker()
            else animateCameraToLastMarker = true
        }else handleClusterMarkerClick(p0)
        return true
    }

    private fun animateCameraToBookmarkMarker() {
        animateCameraToLastMarker = false
        val cameraUpdate = CameraUpdateFactory
            .newLatLngZoom(lastClusterMarker?.position, lastClusterMarker?.photoArticle!!.zoom)
        googleMap?.animateCamera(cameraUpdate, getDurationOfAnimation(), this)
    }

    private fun getDurationOfAnimation(): Int {
        val bounds = googleMap?.projection?.visibleRegion?.latLngBounds
        return if(bounds!!.contains(lastClusterMarker?.position) &&
            googleMap?.cameraPosition!!.zoom >= limitOfZoom) 1000
        else 2000
    }

    override fun onFinish() {
        handleClusterMarkerClick(lastClusterMarker)
    }

    override fun onCancel() {

    }

    private fun handleClusterMarkerClick(clusterMarker: ClusterMarker?) {
        if(clusterMarker!=null && (clusterMarker.getClusterType() == ArticleDaoImpl.PLACE_TYPE ||
                    clusterMarker.getClusterType() == ArticleDaoImpl.BOOKMARK_TYPE))
            showPhotoBottomSheetDialog(clusterMarker.photoArticle)
        else if (clusterMarker!=null) moveToLocation(clusterMarker.position, clusterMarker.photoArticle.zoom)
    }

    private fun showPhotoBottomSheetDialog(photoArticle: PhotoArticle) {
        PhotoBottomSheetDialog().newInstance(photoArticle)
            .show(activity!!.supportFragmentManager, "photoBottomSheetDialog")
    }

    override fun addClusterMarkerToMap(clusterMarker: ClusterMarker) {
        markersList.add(clusterMarker)
        clusterManager?.addItem(clusterMarker)
        clusterManager?.cluster()
        adapter?.notifyDataSetChanged()
        onCameraMove()
    }

    override fun moveToLocation(location: LatLng?, zoom: Float) {
        if(location!=null){
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, zoom)
            googleMap?.animateCamera(cameraUpdate, 2000, null)
        }
    }

    override fun turnOnMyLocationOnMap() {
        googleMap?.isMyLocationEnabled = true
    }

}
