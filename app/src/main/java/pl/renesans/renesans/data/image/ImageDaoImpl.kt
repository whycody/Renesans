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

class ImageDaoImpl(private val context: Context, private val interractor:
    ImageDaoContract.ImageDaoInterractor):
    ImageDaoContract.ImageDao {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private val externalStorage = android.os.Environment.getExternalStorageDirectory().path
    private val sharedPrefs = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
    private var downloadPhotos = true
    private var permissionGranted = false

    override fun loadPhotoInBothQualities(pos: Int, id: String) {
        getPermission()
        if(permissionGranted) loadPhoto(pos, id, highQuality = false, bothQualities = true)
        loadPhoto(pos, id, highQuality = true, bothQualities = true)
    }

    override fun loadPhoto(pos: Int, id: String, highQuality: Boolean, bothQualities: Boolean){
        getPermission()
        if(highQuality) getPhotoUriFromID(pos, id, highQuality)
        else if(permissionGranted) checkSavedPhoto(pos, id, bothQualities)
    }

    private fun getPermission(){
        permissionGranted = (ContextCompat.checkSelfPermission(context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun checkSavedPhoto(pos: Int, id: String, bothQualities: Boolean){
        val fileName = "${id}b.jpg"
        val fileExists = badQualityPhotoIsDownloaded(fileName)
        if(!fileExists){
            downloadPhotos = getValueOfDownloadingPhotos()
            if(!bothQualities) getPhotoUriFromID(pos, id, false)
            if(downloadPhotos) downloadPhotoFromFirebase(id)
        }else loadBadQualityPhotoToHolder(pos, fileName)
    }

    private fun badQualityPhotoIsDownloaded(fileName: String): Boolean{
        val filePath = "$externalStorage/Renesans/$fileName"
        val file = File(filePath)
        return file.exists()
    }

    private fun getPhotoUriFromID(pos: Int, id: String, highQuality: Boolean) {
        val path = getPhotoPath(id, highQuality)
        storageReference.child(path).downloadUrl.addOnSuccessListener { photo ->
            interractor.loadPhotoFromUri(photo, pos)
        }
    }

    private fun getPhotoPath(id: String, highQuality: Boolean): String{
        return if (highQuality) id + "h.jpg"
        else id + "b.jpg"
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

    private fun createNoMediaFile(rootPath: File) {
        File(rootPath.path + "/.nomedia").createNewFile()
    }

    private fun loadBadQualityPhotoToHolder(pos: Int, fileName: String){
        val myBitmap = getBitmap(fileName = fileName)
        if(myBitmap!=null) interractor.loadPhotoFromBitmap(myBitmap, pos)
        else downloadPhotoAgain(fileName)
    }

    override fun getBitmap(id: String?, fileName: String?): Bitmap? {
        var nameOfFile = fileName
        if(nameOfFile == null) nameOfFile = "${id}b.jpg"
        val filePath = "$externalStorage/Renesans/$nameOfFile"
        val file = File(filePath)
        return if(file.exists()) BitmapFactory.decodeFile(file.absolutePath)
        else null
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