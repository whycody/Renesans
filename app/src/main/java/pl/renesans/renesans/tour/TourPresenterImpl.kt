package pl.renesans.renesans.tour

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.data.Tour
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.map.ClusterMarker

class TourPresenterImpl(val context: Context, val view: TourContract.TourView): TourContract.TourPresenter,
    ImageDaoContract.ImageDaoInterractor {

    private lateinit var imageDao: ImageDaoContract.ImageDao
    private lateinit var tour: Tour
    private var currentPage = 0

    override fun onCreate() {
        imageDao = ImageDaoImpl(context, this)
        tour = view.getTourObject()
    }

    override fun onPageSelected(position: Int) {
        currentPage = position
        val photoId = getPhotoId(position)
        if (photoId != null) imageDao.loadPhoto(position, photoId)
        val photoArticle = tour.photosArticlesList!![position]
        if(photoArticle.position!=null)
            view.animateCamera(LatLng(photoArticle.position!!.lat!!, photoArticle.position!!.lng!!))
    }

    override fun getPhotoId(position: Int): String? {
        return when {
            tour.photosArticlesList!![position].photo?.objectId != null ->
                tour.photosArticlesList!![position].photo?.objectId!!
            tour.photosArticlesList!![position].objectId != null ->
                tour.photosArticlesList!![position].objectId+"_0"
            else -> null
        }
    }

    override fun addMarkers() {
        tour.photosArticlesList?.forEach { photoArticle ->
            if(photoArticle.position != null) {
                val newPhotoArticle = PhotoArticle(
                    title = photoArticle.photo!!.description,
                    position = photoArticle.position
                )
                val cluster = ClusterMarker(newPhotoArticle)
                view.addClusterMarkerToMap(cluster)
            }
        }
    }

    override fun mapReady() {
        onPageSelected(currentPage)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        if(currentPage == pos) view.loadPhotoFromUri(photoUri, pos)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(currentPage == pos) view.loadPhotoFromBitmap(photoBitmap, pos)
    }

}