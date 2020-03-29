package pl.renesans.renesans.data

import com.google.firebase.storage.FirebaseStorage
import pl.renesans.renesans.discover.recycler.DiscoverRowHolder

class ImageDaoImpl: ImageDaoContract.ImageDao {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference

    override fun getPhotoUriFromID(holder: DiscoverRowHolder?, id: String, highQuality: Boolean) {
        var path: String = id
        if(highQuality) path += "h.jpg"
        else path += "b.jpg"
        storageReference.child(path).downloadUrl.addOnSuccessListener { photo ->
            holder?.setArticleHighQualityPhoto(photo)
        }
    }
}