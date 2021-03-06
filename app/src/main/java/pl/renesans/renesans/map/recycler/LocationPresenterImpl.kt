package pl.renesans.renesans.map.recycler

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import pl.renesans.renesans.R
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.map.ClusterMarker
import pl.renesans.renesans.map.MapView
import pl.renesans.renesans.settings.SettingsPresenterImpl

class LocationPresenterImpl(val mapView: MapView? = null, val activity: Activity): LocationPresenter,
        LocationListener{

    private var photoArticlesList = mutableListOf(ClusterMarker(PhotoArticle()))
    private var currentLocation: LatLng? = null
    private var locationManager: LocationManager? = null
    private var isWaitingToMoveToCurrentLocation = false
    private val sharedPrefs = activity.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
    private val articleDao = ArticleDaoImpl(activity.applicationContext)
    private lateinit var photoArticles: List<PhotoArticle>

    override fun addMarkers() {
        val mapModeSetting = sharedPrefs.getInt(SettingsPresenterImpl.MAP_MODE, SettingsPresenterImpl.ALL_BUILDINGS)
        photoArticles = getPhotoArticles(mapModeSetting)
        photoArticles.forEach{
            val cluster = ClusterMarker(it)
            mapView?.addClusterMarkerToMap(cluster)
        }
    }

    private fun getPhotoArticles(mapModeSetting: Int) =
        when (mapModeSetting) {
            SettingsPresenterImpl.ALL_BUILDINGS -> articleDao.getPhotoArticlesList()
            SettingsPresenterImpl.ALL_TO_CHOOSED_ERA -> articleDao.getPhotoArticlesListBuiltToYear(1630)
            else -> articleDao.getPhotoArticlesListBuiltInYears(1450, 1630)
        }

    override fun refreshMarkersList(photoArticlesList: MutableList<ClusterMarker>) {
        this.photoArticlesList = photoArticlesList
    }

    override fun getMarkersList() = photoArticlesList

    override fun getPhotoArticles() = photoArticles

    override fun getCurrentLocation() = currentLocation

    override fun itemClicked(pos: Int) {
        if(pos!=0) mapView?.onClusterItemClick(photoArticlesList[pos])
        else checkLocationPermission()
    }

    private fun checkLocationPermission(){
        val permissionGranted = (ContextCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        val locationManagerIsNull = locationManager == null
        val gpsIsTurnedOn = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val currentLocationIsNull = currentLocation == null

        if(permissionGranted) mapView?.turnOnMyLocationOnMap()
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else if(!locationManagerIsNull){
            if(!gpsIsTurnedOn!!) sendToast(activity.getString(R.string.turn_on_location))
            else if(currentLocationIsNull) isWaitingToMoveToCurrentLocation = true
            else mapView?.moveToLocation(currentLocation)
        } else{
            isWaitingToMoveToCurrentLocation = true
            setLocationManager()
        }
    }

    private fun sendToast(message: String) =
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

    override fun setLocationManager(){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 800, 1000f, this)
        }
    }

    override fun getItemCount() = photoArticlesList.size

    override fun onBindViewHolder(holder: LocationRowHolder, position: Int) {
        resetVariables(holder)
        setupLocationRowHolder(holder, position)
        holder.setOnRowClickListener(position)
    }

    private fun setupLocationRowHolder(holder: LocationRowHolder, position: Int) {
        if(position == 0)
            setMyLocationHolder(holder, position)
        else if(photoArticlesList[position].getClusterType() == ArticleDaoImpl.CITY_TYPE)
            setCityTypeHolder(holder, position)
        else holder.setText(photoArticlesList[position].getFullTitle())
    }

    private fun setMyLocationHolder(holder: LocationRowHolder, position: Int) {
        with(holder) {
            setText(activity.getString(R.string.my_location))
            setDrawable(ContextCompat.getDrawable(activity, R.drawable.sh_my_location_row)!!)
            setTextColor(R.color.colorGray)
        }
    }

    private fun setCityTypeHolder(holder: LocationRowHolder, position: Int) {
        with(holder) {
            setText(photoArticlesList[position].getFullTitle())
            setDrawable(ContextCompat.getDrawable(activity, R.drawable.sh_red_location_row)!!)
        }
    }

    private fun resetVariables(holder: LocationRowHolder) {
        with(holder) {
            setText(" ")
            setTextColor(Color.WHITE)
            setDrawable(ContextCompat.getDrawable(activity, R.drawable.sh_orange_location_row)!!)
            setOnRowClickListener(0)
        }
    }

    override fun onLocationChanged(p0: Location?) {
        if(p0!=null){
            currentLocation = LatLng(p0.latitude, p0.longitude)
            if(isWaitingToMoveToCurrentLocation){
                isWaitingToMoveToCurrentLocation = false
                mapView?.moveToLocation(currentLocation)
            }
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) { }

    override fun onProviderEnabled(p0: String?) { }

    override fun onProviderDisabled(p0: String?) { }
}