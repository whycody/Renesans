package pl.renesans.renesans.tour

import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.data.Tour

interface TourContract {

    interface TourView {

        fun getTourObject(): Tour

        fun loadPhotoFromUri(photoUri: Uri, pos: Int)

        fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int)
    }

    interface TourPresenter {

        fun onCreate()

        fun onPageSelected(position: Int)
    }
}