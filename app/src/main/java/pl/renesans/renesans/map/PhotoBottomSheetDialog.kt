package pl.renesans.renesans.map

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_tour.view.*
import kotlinx.android.synthetic.main.tour_slide_layout.view.*
import pl.renesans.renesans.R
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.photo.PhotoActivity
import pl.renesans.renesans.sources.SourcesActivity

class PhotoBottomSheetDialog(private val photoArticle: PhotoArticle): BottomSheetDialogFragment(),
    ImageDaoContract.ImageDaoInterractor{

    private lateinit var articlePhoto: ImageView
    private lateinit var article: Article
    private lateinit var sourcesBtn: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_bottom_sheet_photo, container, false)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        view!!.articleTitle.text = photoArticle.title
        view.articleParagraph.text = photoArticle.paragraph?.content
        view.photoDescription.text = photoArticle.photo?.description
        view.articlePhoto.setBackgroundColor(Color.LTGRAY)
        view.articlePhoto.setOnClickListener{ startPhotoViewActivity(photoArticle.photo?.objectId!!) }
        val articleConverter = ArticleConverterImpl()
        articlePhoto = view.findViewById(R.id.articlePhoto)!!
        sourcesBtn = view.findViewById(R.id.sourcesBtn)!!
        article = articleConverter.convertPhotoArticleToArticle(photoArticle)
        setupSourcesBtn()
        loadMainPhoto()
        return view
    }

    private fun loadMainPhoto(){
        val imageDao =
            ImageDaoImpl(context!!, this)
        if(photoArticle.photo!=null) imageDao.loadPhotoInBothQualities(id = photoArticle.photo!!.objectId!!)
        else imageDao.loadPhotoInBothQualities(0, photoArticle.objectId + "_0")
    }

    private fun setupSourcesBtn(){
        val articleDao = ArticleDaoImpl()
        if(!articleDao.articleHasSources(article)) sourcesBtn.visibility = View.GONE
        else sourcesBtn.setOnClickListener{ startSourceActivity() }
    }

    private fun startSourceActivity(){
        val intent = Intent(context, SourcesActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE, article)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun startPhotoViewActivity(id: String){
        val intent = Intent(context, PhotoActivity::class.java)
        intent.putExtra(PhotoActivity.ARTICLE_ID, id)
        startActivity(intent)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) setWhiteNavigationBar(dialog)
        return dialog
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setWhiteNavigationBar(dialog: Dialog) {
        val window: Window = dialog.window!!
        val metrics = DisplayMetrics()
        window.windowManager.defaultDisplay.getMetrics(metrics)
        val dimDrawable = GradientDrawable()
        val navigationBarDrawable = GradientDrawable()
        navigationBarDrawable.shape = GradientDrawable.RECTANGLE
        navigationBarDrawable.setColor(ContextCompat.getColor(activity!!, R.color.colorGray))
        val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
        val windowBackground = LayerDrawable(layers)
        windowBackground.setLayerInsetTop(1, metrics.heightPixels)
        window.setBackgroundDrawable(windowBackground)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        if(context!=null) Glide.with(context!!).load(photoUri).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(context!=null) Glide.with(context!!).load(photoBitmap).into(articlePhoto)
    }
}