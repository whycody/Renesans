package pl.renesans.renesans.data

import android.content.Context
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import pl.renesans.renesans.discover.recycler.DiscoverRowHolder
import java.io.File

class ImageDaoImpl(val context: Context): ImageDao {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private val externalStorage = android.os.Environment.getExternalStorageDirectory().path

    override fun loadPhotoToHolder(holder: DiscoverRowHolder?, id: String, highQuality: Boolean){
        if(!highQuality) checkBadQualityPhoto(holder, id)
        else getPhotoUriFromID(holder, id, highQuality)
    }

    private fun checkBadQualityPhoto(holder: DiscoverRowHolder?, id: String){
        val fileName = "${id}b.jpg"
        val fileExists = badQualityPhotoIsDownloaded(fileName)
        if(!fileExists){
            getPhotoUriFromID(holder, id, false)
            downloadPhotoFromFirebase(id)
        }else loadBadQualityPhotoToHolder(holder, fileName)
    }

    private fun getPhotoUriFromID(holder: DiscoverRowHolder?, id: String, highQuality: Boolean) {
        var path: String = id
        if(highQuality) path += "h.jpg"
        else path += "b.jpg"
        storageReference.child(path).downloadUrl.addOnSuccessListener { photo ->
            holder?.setArticleHighQualityPhoto(photo)
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

    private fun loadBadQualityPhotoToHolder(holder: DiscoverRowHolder?, fileName: String){
        val filePath = "$externalStorage/Renesans/$fileName"
        val file = File(filePath)
        if(file.exists()){
            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            holder?.setArticlePhoto(myBitmap)
        }
    }
}