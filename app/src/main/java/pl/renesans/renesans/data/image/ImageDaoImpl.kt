package pl.renesans.renesans.data.image

import android.content.Context
import android.graphics.BitmapFactory
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

    override fun loadPhotoInBothQualities(pos: Int, id: String) {
        loadPhoto(pos, id, false)
        loadPhoto(pos, id, true)
    }

    override fun loadPhoto(pos: Int, id: String, highQuality: Boolean){
        if(!highQuality) checkSavedPhoto(pos, id)
        else getHighQualityPhotoUriFromID(pos, id)
    }

    private fun checkSavedPhoto(pos: Int, id: String){
        val fileName = "${id}b.jpg"
        val fileExists = badQualityPhotoIsDownloaded(fileName)
        if(!fileExists){
            val downloadPhotos = sharedPrefs.getBoolean(SettingsPresenterImpl.DOWNLOAD_PHOTOS, true)
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
            interractor.loadPhotoFromBitmap(myBitmap, pos)
        }
    }
}