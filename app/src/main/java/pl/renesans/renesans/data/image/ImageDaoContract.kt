package pl.renesans.renesans.data.image

import android.graphics.Bitmap
import android.net.Uri

interface ImageDaoContract {

    interface ImageDao {

        fun loadPhotoInBothQualities(pos: Int = 0, id: String)

        fun loadPhoto(pos: Int = 0, id: String, highQuality: Boolean = false, bothQualities: Boolean = true)

        fun getBitmap(id: String? = null, fileName: String? = null): Bitmap?
    }

    interface ImageDaoInterractor {

        fun loadPhotoFromUri(photoUri: Uri, pos: Int)

        fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int)
    }
}