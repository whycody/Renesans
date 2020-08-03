package pl.renesans.renesans.photo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl

class PhotoAdapter(private val context: Context, private val article: Article): PagerAdapter(),
    ImageDaoContract.ImageDaoInterractor {

    private val views = hashMapOf<Int, View>()
    private val imageDao = ImageDaoImpl(context, this)

    override fun isViewFromObject(view: View, `object`: Any) = view == `object` as ConstraintLayout

    override fun getCount() = article.listOfPhotos?.size!!

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.pager_photo, container, false)
        views[position] = view
        imageDao.loadPhoto(position, article.listOfPhotos!![position].objectId!!)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) =
        container.removeView(`object` as ConstraintLayout)

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        Glide.with(context)
            .load(photoUri)
            .placeholder(views[pos]!!.findViewById<PhotoView>(R.id.photoImage).drawable)
            .override(Target.SIZE_ORIGINAL)
            .into(views[pos]!!.findViewById<PhotoView>(R.id.photoImage))
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(photoBitmap.isRecycled) return
        Glide.with(context)
            .load(photoBitmap)
            .override(Target.SIZE_ORIGINAL)
            .into(views[pos]!!.findViewById<PhotoView>(R.id.photoImage))
    }
}