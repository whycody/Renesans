package pl.renesans.renesans.data

import android.content.Context
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ImageDaoImpl(val context: Context, val interractor: ImageDaoContract.ImageDaoInterractor):
    ImageDaoContract.ImageDao {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private val externalStorage = android.os.Environment.getExternalStorageDirectory().path

    override fun loadPhoto(pos: Int, id: String, highQuality: Boolean){
        if(!highQuality) checkSavedPhoto(pos, id)
        else getPhotoUriFromID(pos, id, highQuality)
    }

    private fun checkSavedPhoto(pos: Int, id: String){
        val fileName = "${id}b.jpg"
        val fileExists = badQualityPhotoIsDownloaded(fileName)
        if(!fileExists){
            getPhotoUriFromID(pos, id, false)
            downloadPhotoFromFirebase(id)
        }else loadBadQualityPhotoToHolder(pos, fileName)
    }

    private fun getPhotoUriFromID(pos: Int, id: String, highQuality: Boolean) {
        var path: String = id
        if(highQuality) path += "h.jpg"
        else path += "b.jpg"
        storageReference.child(path).downloadUrl.addOnSuccessListener { photo ->
            interractor.loadPhotoFromUri(photo, pos)
        }
    }

    private fun badQualityPhotoIsDownloaded(fileName: String): Boolean{
        val filePath = "$externalStorage/Renesans/$fileName"
        val file = File(filePath)
        return file.exists()
    }

    private fun downloadPhotoFromFirebase(id: String){
        val fileName = "${id}b.jpg"
        val photoReference = storageReference.child(fileName)
        val rootPath = File(externalStorage, "Renesans")
        if(!rootPath.exists()) rootPath.mkdirs()
        val localFile = File(rootPath, fileName)
        photoReference.getFile(localFile)
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