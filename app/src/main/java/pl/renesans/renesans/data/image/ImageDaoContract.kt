package pl.renesans.renesans.data.image

import android.graphics.Bitmap
import android.net.Uri

interface ImageDaoContract {

    interface ImageDao {

        fun loadPhoto(pos: Int = 0, id: String, bothQualities: Boolean = true)
    }

    interface ImageDaoInterractor {

        fun loadPhotoFromUri(photoUri: Uri, pos: Int)

        fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int)
    }

    interface ImageDaoDownloadInterractor {

        fun downloadFailed()

        fun donwloadSuccess()

        fun photoExists()
    }
}