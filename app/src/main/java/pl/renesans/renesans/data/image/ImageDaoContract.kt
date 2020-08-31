package pl.renesans.renesans.data.image

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface ImageDaoContract {

    interface ImageDao {

        fun loadPhoto(pos: Int = 0, id: String, bothQualities: Boolean = true)

        fun savePhotoToExternalStorage(id: String)

        fun highQualityPhotoIsAvailable(id: String): Boolean

        fun getPhotoFile(id: String): File?

        fun getPhotoUri(id: String)
    }

    interface ImageDaoInterractor {

        fun loadPhotoFromUri(photoUri: Uri, pos: Int)

        fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int)
    }

    interface ImageDaoDownloadInterractor {

        fun downloadFailed()

        fun downloadSuccess()

        fun photoExists()
    }
}