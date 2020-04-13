package pl.renesans.renesans.data

import android.graphics.Bitmap
import android.net.Uri

interface ImageDaoContract {

    interface ImageDao {

        fun loadPhotoInBothQualities(pos: Int = 0, id: String)

        fun loadPhoto(pos: Int = 0, id: String, highQuality: Boolean = true)
    }

    interface ImageDaoInterractor {

        fun loadPhotoFromUri(photoUri: Uri, pos: Int)

        fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int)
    }
}