package pl.renesans.renesans.map

import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.dialog_bottom_sheet_map.view.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.LastCameraPosition
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import java.lang.Exception

class MapBottomSheetDialog: BottomSheetDialogFragment(), ImageDaoContract.ImageDaoInterractor,
    OnMapReadyCallback {

    private lateinit var articlePhoto: ImageView
    private lateinit var article: Article
    private var mapFragment: SupportMapFragment? = null

    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<ClusterMarker>? = null
    private var clusterManagerRenderer: ClusterManagerRenderer? = null
    private var deviceIsInLandscape = false
    private var cameraPos: CameraPosition? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_bottom_sheet_map, container)
        checkBundle(savedInstanceState)
        initializeObjects()
        deviceIsInLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        articlePhoto = view!!.articlePhoto
        view.articleTitle?.text = article.title
        setupMap()
        loadMainPhoto()
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) setWhiteNavigationBar(dialog)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        if(deviceIsInLandscape){
            val bottomSheetBehavior = BottomSheetBehavior.from(view?.parent as View)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mapFragment != null) {
            try{
                fragmentManager!!.beginTransaction().remove(mapFragment!!).commit()
            }catch(e: Exception){}
        }
    }

    fun newInstance(article: Article): MapBottomSheetDialog {
        val args = Bundle()
        args.putSerializable("article", article)
        val mapSheet = MapBottomSheetDialog()
        mapSheet.arguments = args
        return mapSheet
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

    private fun checkBundle(savedInstanceState: Bundle?){
        if(savedInstanceState?.getSerializable("lastPosition") != null){
            val lastCameraPos =
                savedInstanceState.getSerializable("lastPosition") as LastCameraPosition
            cameraPos = CameraPosition(LatLng(lastCameraPos.lat!!, lastCameraPos.lng!!),
                lastCameraPos.cameraZoom!!, lastCameraPos.tilt!!, lastCameraPos.bearing!!)
        }
    }

    private fun initializeObjects(){
        if(arguments!=null)
            article = arguments!!.getSerializable("article") as Article
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setWhiteNavigationBar(dialog: Dialog) {
        val window: Window = dialog.window!!
        val metrics = DisplayMetrics()
        window.windowManager.defaultDisplay.getMetrics(metrics)
        val dimDrawable = GradientDrawable()
        val navigationBarDrawable = GradientDrawable()
        navigationBarDrawable.shape = GradientDrawable.RECTANGLE
        navigationBarDrawable.setColor(ContextCompat.getColor(activity!!, R.color.colorGray))
        val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
        val windowBackground = LayerDrawable(layers)
        windowBackground.setLayerInsetTop(1, metrics.heightPixels)
        window.setBackgroundDrawable(windowBackground)
    }

    private fun loadMainPhoto(){
        val imageDao = ImageDaoImpl(context!!, this)
        if(article.listOfPhotos!=null && article.listOfPhotos!![0].objectId!=null)
            imageDao.loadPhoto(id = article.listOfPhotos!![0].objectId!!)
        else imageDao.loadPhoto(0, article.objectId + "_0")
    }

    private fun setupMap(){
        mapFragment = fragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        if(context!=null)
            Glide.with(context!!).load(photoUri).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(context!=null) Glide.with(context!!).load(photoBitmap).into(articlePhoto)
    }

    override fun onMapReady(p0: GoogleMap?) {
        this.googleMap = p0
        setUiSettings()
        prepareManagers()
        addMarkers()
        if(cameraPos == null) moveCameraToFirstMarker()
        else googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos))
    }

    private fun moveCameraToFirstMarker(){
        val firstPosition = article.listOfPositions!![0]
        val firstPositionLatLng = LatLng(firstPosition.lat!!, firstPosition.lng!!)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPositionLatLng, 15f))
    }

    private fun addMarkers(){
        article.listOfPositions?.forEach {
            if(it.title == null) it.title = article.title
            val newPhotoArticle = PhotoArticle(title = it.title, position = it)
            val cluster = ClusterMarker(newPhotoArticle)
            clusterManager?.addItem(cluster)
            clusterManager?.cluster()
        }
    }

    private fun setUiSettings(){
        googleMap?.uiSettings?.isMapToolbarEnabled = false
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.isCompassEnabled = false
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
    }

    private fun prepareManagers(){
        if(clusterManager == null) clusterManager = ClusterManager(context, googleMap)
        if(clusterManagerRenderer == null){
            clusterManagerRenderer = ClusterManagerRenderer(context!!, googleMap!!, clusterManager!!)
            clusterManagerRenderer?.prepareMarker()
            clusterManager?.renderer = clusterManagerRenderer
        }
    }

}