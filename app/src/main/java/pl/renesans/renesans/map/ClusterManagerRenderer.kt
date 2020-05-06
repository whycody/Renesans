package pl.renesans.renesans.map

import android.content.Context
import android.graphics.Bitmap
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import pl.renesans.renesans.R
import pl.renesans.renesans.data.article.ArticleDaoImpl

class ClusterManagerRenderer(val context: Context, val map: GoogleMap,
                             clusterManager: ClusterManager<ClusterMarker>, val mapView: MapView? = null)
    : DefaultClusterRenderer<ClusterMarker>(context, map, clusterManager) {

    private lateinit var iconGenerator: IconGenerator
    private lateinit var textView: TextView

    fun prepareMarker(){
        iconGenerator = IconGenerator(context)
        setupTextView()
        iconGenerator.setContentView(textView)
    }

    private fun setupTextView(){
        textView = TextView(context)
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        textView.setPadding(10,5,10,5)
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
    }

    override fun onBeforeClusterItemRendered(item: ClusterMarker?, markerOptions: MarkerOptions?) {
        iconGenerator.setContentView(textView)
        textView.text = item?.title
        if(item?.getCLusterType() == ArticleDaoImpl.PLACE_TYPE)
            iconGenerator.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        else iconGenerator.setColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        val icon: Bitmap = iconGenerator.makeIcon()
        markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun onClusterItemRendered(clusterItem: ClusterMarker?, marker: Marker?) {
        super.onClusterItemRendered(clusterItem, marker)
        mapView?.onCameraMove()
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>?): Boolean {
        return false
    }
}