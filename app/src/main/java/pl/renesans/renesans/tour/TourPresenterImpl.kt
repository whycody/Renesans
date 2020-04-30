package pl.renesans.renesans.tour

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.data.Tour
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl

class TourPresenterImpl(val context: Context, val view: TourContract.TourView): TourContract.TourPresenter,
    ImageDaoContract.ImageDaoInterractor {

    private lateinit var imageDao: ImageDaoContract.ImageDao
    private lateinit var tour: Tour
    private var currentPage = 0

    override fun onCreate() {
        imageDao = ImageDaoImpl(context, this)
        tour = view.getTourObject()
        imageDao.loadPhotoInBothQualities(currentPage, tour.photosArticlesList!![0].objectId+"_0")
    }

    override fun onPageSelected(position: Int) {
        currentPage = position
        imageDao.loadPhotoInBothQualities(position, tour.photosArticlesList!![position].objectId+"_0")
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        if(currentPage == pos) view.loadPhotoFromUri(photoUri, pos)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(currentPage == pos) view.loadPhotoFromBitmap(photoBitmap, pos)
    }

}