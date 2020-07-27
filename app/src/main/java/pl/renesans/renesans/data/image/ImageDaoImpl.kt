package pl.renesans.renesans.data.image

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import pl.renesans.renesans.settings.SettingsPresenterImpl
import java.io.File

class ImageDaoImpl(private val context: Context,
                   private val interractor: ImageDaoContract.ImageDaoInterractor? = null,
                   private val downloadInterractor:
                   ImageDaoContract.ImageDaoDownloadInterractor? = null): ImageDaoContract.ImageDao {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private val externalStorage = context.filesDir.path
    private val sharedPrefs = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
    private var permissionGranted = false
    private var downloadPhotosMode = 0

    override fun loadPhoto(pos: Int, id: String, bothQualities: Boolean){
        getPermission()
        getDownloadPhotosMode()
        checkHighQualityPhoto(pos, id, bothQualities)
    }

    private fun checkHighQualityPhoto(pos: Int, id: String, bothQualities: Boolean){
        val fileName = getPhotoPath(id, true)
        val fileExists = photoIsDownloaded(fileName)
        if(!fileExists){
            checkBadQualityPhoto(pos, id, bothQualities)
            checkDownloadHighQualityPhotosSetting(id)
            if(bothQualities) getPhotoUriFromID(pos, id, bothQualities)
        }else loadBitmapPhotoToHolder(pos, fileName)
    }

    private fun checkDownloadHighQualityPhotosSetting(id: String){
        if(!permissionGranted) return
        if(downloadPhotosMode == SettingsPresenterImpl.DOWNLOAD_HIGH_QUALITY)
            downloadPhotoFromFirebase(id, true)
    }

    private fun checkBadQualityPhoto(pos: Int, id: String, bothQualities: Boolean){
        val fileName = getPhotoPath(id, false)
        val fileExists = photoIsDownloaded(fileName)
        if(fileExists) loadBitmapPhotoToHolder(pos, fileName)
        else if(!bothQualities) getPhotoUriFromID(pos, id, bothQualities)
        if(!fileExists) checkDownloadBadQualityPhotosSetting(id)
    }

    private fun checkDownloadBadQualityPhotosSetting(id: String){
        if(!permissionGranted || photoIsDownloaded(getPhotoPath(id, true))) return
        if(downloadPhotosMode == SettingsPresenterImpl.DOWNLOAD_BAD_QUALITY)
            downloadPhotoFromFirebase(id, false)
    }

    private fun photoIsDownloaded(fileName: String): Boolean{
        val file = File(getFilePath(fileName))
        return file.exists()
    }

    private fun getPhotoUriFromID(pos: Int, id: String, highQuality: Boolean) {
        val path = getPhotoPath(id, highQuality)
        storageReference.child(path).downloadUrl.addOnSuccessListener { photo ->
            interractor?.loadPhotoFromUri(photo, pos)
        }
    }

    private fun downloadPhotoFromFirebase(id: String, highQuality: Boolean){
        val fileName = getPhotoPath(id, highQuality)
        downloadPhotoFromFirebaseByFilename(fileName, highQuality, id)
    }

    private fun downloadPhotoFromFirebaseByFilename(fileName: String, highQuality: Boolean = false, id: String? = null){
        val photoReference = storageReference.child(fileName)
        val rootPath = File(externalStorage, "photos")
        if(!rootPath.exists()) rootPath.mkdirs()
        val localFile = File(rootPath, fileName)
        photoReference.getFile(localFile)
            .addOnSuccessListener {
                downloadInterractor?.donwloadSuccess()
                if(highQuality) deletePhotoFromDevice(getPhotoPath(id!!, false))
            }.addOnFailureListener { downloadInterractor?.downloadFailed() }
    }

    private fun loadBitmapPhotoToHolder(pos: Int, fileName: String){
        downloadInterractor?.photoExists()
        val myBitmap = getBitmap(fileName)
        if(myBitmap!=null) interractor?.loadPhotoFromBitmap(myBitmap, pos)
        else downloadPhotoAgain(fileName)
    }

    override fun getBitmap(fileName: String): Bitmap? {
        val filePath = "$externalStorage/photos/$fileName"
        val file = File(filePath)
        return if(file.exists()) BitmapFactory.decodeFile(file.absolutePath)
        else null
    }

    private fun getPhotoPath(id: String, highQuality: Boolean) =
        if (highQuality) id + "h.jpg"
        else id + "b.jpg"

    private fun downloadPhotoAgain(fileName: String) {
        if(downloadPhotosMode != SettingsPresenterImpl.NOT_DOWNLOAD)
            if(deletePhotoFromDevice(fileName)) downloadPhotoFromFirebaseByFilename(fileName)
    }

    private fun deletePhotoFromDevice(fileName: String) = File(getFilePath(fileName)).delete()

    private fun getFilePath(fileName: String) = "$externalStorage/photos/$fileName"

    private fun getPermission(){
        permissionGranted = (ContextCompat.checkSelfPermission(context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun getDownloadPhotosMode() {
        downloadPhotosMode = if(!permissionGranted) SettingsPresenterImpl.NOT_DOWNLOAD
        else sharedPrefs.getInt(SettingsPresenterImpl.DOWNLOAD_PHOTOS,
            SettingsPresenterImpl.DOWNLOAD_BAD_QUALITY)
    }
}