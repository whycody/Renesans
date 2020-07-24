package pl.renesans.renesans.photo

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_photo.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl

class PhotoActivity : AppCompatActivity(), ImageDaoContract.ImageDaoInterractor {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        changeStatusBarColor()
        val imageDao = ImageDaoImpl(this, this)
        imageDao.loadPhoto(id = getPhotoId())
    }

    private fun changeStatusBarColor(){
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)
    }

    private fun getPhotoId(): String {
        return intent.getStringExtra(ARTICLE_ID)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        Glide.with(applicationContext).load(photoUri)
            .placeholder(photoImage.drawable)
            .override(Target.SIZE_ORIGINAL)
            .into(photoImage)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        Glide.with(applicationContext).load(photoBitmap).override(Target.SIZE_ORIGINAL).into(photoImage)
    }

    companion object {
        const val ARTICLE_ID = "article id"
    }
}
