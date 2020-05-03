package pl.renesans.renesans.tour

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.activity_tour.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Tour
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.map.ClusterManagerRenderer
import pl.renesans.renesans.map.ClusterMarker
import pl.renesans.renesans.settings.SettingsPresenterImpl


class TourActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    ImageDaoContract.ImageDaoInterractor, TourContract.TourView, OnMapReadyCallback {

    private lateinit var tour: Tour
    private lateinit var presenter: TourContract.TourPresenter
    private lateinit var sharedPrefs: SharedPreferences
    private var dots = mutableListOf<View>()
    private var markers = mutableListOf<ClusterMarker>()
    private var currentPage = 0

    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<ClusterMarker>? = null
    private var clusterManagerRenderer: ClusterManagerRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPrefs = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        val mapOpacity = sharedPrefs.getBoolean(SettingsPresenterImpl.MAP_OPACITY, true)
        if(mapOpacity) setTheme(R.style.TourTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour)
        setSupportActionBar(tourToolbar)
        setupMap()
        setupTheme(mapOpacity)
        tour = getTourObject()
        tourToolbar.title = "${tour.title}・Interaktywny szlak"
        presenter = TourPresenterImpl(applicationContext, this)
        val tourAdapter = TourAdapter(this, tour)
        tourPager.adapter = tourAdapter
        tourPager.addOnPageChangeListener(this)
        backBtn.setOnClickListener{ showPreviousPage() }
        nextBtn.setOnClickListener{ showNextPage() }
        addDotsIndicator()
        presenter.onCreate()
    }

    private fun setupTheme(mapOpacity: Boolean){
        if(mapOpacity)
            tourToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorDarkWhite))
        else shadowView.visibility = View.INVISIBLE
    }

    private fun setupMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        presenter.mapReady()
        setUiSettings()
        prepareManagers()
        presenter.addMarkers()
    }

    private fun setUiSettings(){
        googleMap?.uiSettings?.isMapToolbarEnabled = false
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.isCompassEnabled = false
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(applicationContext, R.raw.map_style))
    }

    private fun prepareManagers(){
        if(clusterManager == null) clusterManager = ClusterManager(this, googleMap)
        if(clusterManagerRenderer == null){
            clusterManagerRenderer = ClusterManagerRenderer(this, googleMap!!, clusterManager!!)
            clusterManagerRenderer?.prepareMarker()
            clusterManager?.renderer = clusterManagerRenderer
        }
    }

    override fun getTourObject(): Tour {
        return intent.getSerializableExtra(TOUR) as Tour
    }

    private fun showNextPage(){
        tourPager.currentItem = currentPage + 1
    }

    private fun showPreviousPage(){
        tourPager.currentItem = currentPage - 1
    }

    private fun addDotsIndicator(){
        for(i in 1.. tour.photosArticlesList!!.size){
            val view = getDefaultView()
            dots.add(view)
            dotsLayout.addView(view)
        }
        dots[0].background = getDrawable(R.drawable.sh_circle_gray)
    }

    private fun getDefaultView(): View {
        val view = View(this)
        val params = LinearLayout.LayoutParams(18, 18, 0.0f)
        params.setMargins(6, 1, 6, 0)
        view.layoutParams = params
        view.background = getDrawable(R.drawable.sh_circle_transp_gray)
        return view
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        checkUserHasEndedTour(position)
        if(position == dots.size - 2 && currentPage == dots.size - 1) return
        presenter.onPageSelected(position)
        dots[currentPage].background = getDrawable(R.drawable.sh_circle_transp_gray)
        dots[position].background = getDrawable(R.drawable.sh_circle_gray)
        currentPage = position
        setBackBtnProperties(position)
        setNextBtnProperties(position)
    }

    private fun checkUserHasEndedTour(position: Int){
        if(position == dots.size - 2 && currentPage == dots.size - 1)
            tourPager.currentItem = currentPage
        else if(position == dots.size - 1 && currentPage != dots.size - 1) showAllTour()
    }

    private fun setBackBtnProperties(position: Int){
        if(position==0 || position==dots.size-1) backBtn.visibility = View.INVISIBLE
        else backBtn.visibility = View.VISIBLE
    }

    private fun setNextBtnProperties(position: Int){
        if(position==dots.size-1){
            nextBtn.text = getString(R.string.end)
            nextBtn.setOnClickListener{ finish() }
        }else if(position==dots.size-2){
            nextBtn.text = getString(R.string.end)
            nextBtn.setOnClickListener{ showAllTour() }
        }else{
            nextBtn.text = getString(R.string.next)
            nextBtn.setOnClickListener{ showNextPage() }
        }
    }

    private fun showAllTour(){
        showNextPage()
        val params = mapConstraint.layoutParams as LinearLayout.LayoutParams
        params.weight = 2f
        articlePhoto.visibility = View.GONE
        animateWholeTourCamera()
    }

    private fun animateWholeTourCamera(){
        val builder = LatLngBounds.Builder()
        for (marker in markers) builder.include(marker.position)
        val bounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0)
        googleMap?.animateCamera(cameraUpdate)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        Glide.with(applicationContext).load(photoUri).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        Glide.with(applicationContext).load(photoBitmap).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    override fun addClusterMarkerToMap(clusterMarker: ClusterMarker) {
        markers.add(clusterMarker)
        clusterManager?.addItem(clusterMarker)
        clusterManager?.cluster()
    }

    override fun animateCamera(latLng: LatLng) {
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f), 1500, null)
    }

    override fun moveCamera(latLng: LatLng) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    companion object {
        const val TOUR = "tour"
    }
}
