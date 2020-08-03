package pl.renesans.renesans.photo

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_photo.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article

class PhotoActivity : AppCompatActivity() {

    private var photoAdapter: PhotoAdapter? = null
    private var pagerView: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        changeStatusBarColor()
        pagerView = photoPager
        photoAdapter = PhotoAdapter(applicationContext, getArticle())
        photoPager.adapter = photoAdapter
        photoPager.offscreenPageLimit = 10
        photoPager.currentItem = getPosition()
        photoPager.setPageTransformer(true, ZoomOutPageTransformer())
    }

    private fun changeStatusBarColor(){
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)
    }

    private fun getPosition() = intent.getIntExtra(POSITION, 0)

    private fun getArticle() = intent.getSerializableExtra(ARTICLE) as Article

    companion object {
        const val ARTICLE = "article"
        const val POSITION = "position"
    }
}
