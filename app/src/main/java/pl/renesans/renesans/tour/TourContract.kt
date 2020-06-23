package pl.renesans.renesans.tour

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import pl.renesans.renesans.data.Tour
import pl.renesans.renesans.map.ClusterMarker

interface TourContract {

    interface TourView {

        fun getTourObject(): Tour

        fun loadPhotoFromUri(photoUri: Uri, pos: Int)

        fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int)

        fun addClusterMarkerToMap(cluster: ClusterMarker)

        fun animateCamera(latLng: LatLng)

        fun moveCamera(latLng: LatLng)
    }

    interface TourPresenter {

        fun onCreate()

        fun getPhotoId(position: Int): String?

        fun onPageSelected(position: Int)

        fun addMarkers()

        fun mapReady()
    }
}