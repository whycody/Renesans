package pl.renesans.renesans.data.image

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import pl.renesans.renesans.settings.SettingsPresenterImpl
import java.io.File
import java.lang.Exception

class ImageDaoImpl(val context: Context, val interractor: ImageDaoContract.ImageDaoInterractor):
    ImageDaoContract.ImageDao {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private val externalStorage = android.os.Environment.getExternalStorageDirectory().path
    private val sharedPrefs = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
    private var downloadPhotos = true
    private var permissionGranted = false

    override fun loadPhotoInBothQualities(pos: Int, id: String) {
        permissionGranted = (ContextCompat.checkSelfPermission(context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        if(permissionGranted) loadPhoto(pos, id, false)
        loadPhoto(pos, id, true)
    }

    private fun loadPhoto(pos: Int, id: String, highQuality: Boolean){
        if(!highQuality) checkSavedPhoto(pos, id)
        else getHighQualityPhotoUriFromID(pos, id)
    }

    private fun checkSavedPhoto(pos: Int, id: String){
        val fileName = "${id}b.jpg"
        val fileExists = badQualityPhotoIsDownloaded(fileName)
        if(!fileExists){
            downloadPhotos = getValueOfDownloadingPhotos()
            if(downloadPhotos) downloadPhotoFromFirebase(id)
        }else loadBadQualityPhotoToHolder(pos, fileName)
    }

    private fun badQualityPhotoIsDownloaded(fileName: String): Boolean{
        val filePath = "$externalStorage/Renesans/$fileName"
        val file = File(filePath)
        return file.exists()
    }

    private fun getHighQualityPhotoUriFromID(pos: Int, id: String) {
        var path: String = id + "h.jpg"
        storageReference.child(path).downloadUrl.addOnSuccessListener { photo ->
            interractor.loadPhotoFromUri(photo, pos)
        }
    }

    private fun downloadPhotoFromFirebase(id: String){
        val fileName = "${id}b.jpg"
        val photoReference = storageReference.child(fileName)
        val rootPath = File(externalStorage, "Renesans")
        if(!rootPath.exists()) rootPath.mkdirs()
        createNoMediaFile(rootPath)
        val localFile = File(rootPath, fileName)
        photoReference.getFile(localFile)
    }

    private fun createNoMediaFile(rootPath: File){
        val noMediaFile = File(rootPath.path + "/.nomedia")
        try {
            noMediaFile.createNewFile()
        }catch(e: Exception){}
    }

    private fun loadBadQualityPhotoToHolder(pos: Int, fileName: String){
        val filePath = "$externalStorage/Renesans/$fileName"
        val file = File(filePath)
        if(file.exists()){
            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            if(myBitmap!=null) interractor.loadPhotoFromBitmap(myBitmap, pos)
            else downloadPhotoAgain(fileName)
        }
    }

    private fun downloadPhotoAgain(fileName: String){
        downloadPhotos = getValueOfDownloadingPhotos()
        if(downloadPhotos){
            val filePath = "$externalStorage/Renesans/$fileName"
            val file = File(filePath)
            if(file.delete()) downloadPhotoFromFirebase(fileName.substring(0, fileName.length - 5))
        }
    }

    private fun getValueOfDownloadingPhotos(): Boolean {
        return if(!permissionGranted) false
        else sharedPrefs.getBoolean(SettingsPresenterImpl.DOWNLOAD_PHOTOS, permissionGranted)
    }
}