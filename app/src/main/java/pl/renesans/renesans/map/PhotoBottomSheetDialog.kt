package pl.renesans.renesans.map

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_tour.view.articlePhoto
import kotlinx.android.synthetic.main.tour_slide_layout.view.articleParagraph
import kotlinx.android.synthetic.main.tour_slide_layout.view.articleTitle
import kotlinx.android.synthetic.main.tour_slide_layout.view.photoDescription
import pl.renesans.renesans.R
import pl.renesans.renesans.SuggestionBottomSheetDialog
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.firebase.FirebaseContract
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.photo.PhotoActivity
import pl.renesans.renesans.sources.SourcesActivity
import java.lang.StringBuilder

class PhotoBottomSheetDialog: BottomSheetDialogFragment(),
    ImageDaoContract.ImageDaoInterractor, FirebaseContract.FirebaseInterractor {

    private lateinit var articlePhoto: ImageView
    private lateinit var article: Article
    private lateinit var sourcesBtn: Button
    private lateinit var articleTitle: TextView
    private lateinit var articleParagraph: TextView
    private lateinit var invisibleView: View
    private lateinit var photoArticle: PhotoArticle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_bottom_sheet_photo, container, false)
        if(arguments!=null) photoArticle = arguments!!.getSerializable("photoArticle") as PhotoArticle
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        view.articleTitle.text = photoArticle.title
        view.articleParagraph.text = photoArticle.paragraph?.content
        view.photoDescription.text = photoArticle.photo?.description
        view.articlePhoto.setBackgroundColor(Color.LTGRAY)
        view.articlePhoto.setOnClickListener{ startPhotoViewActivity(photoArticle.photo?.objectId!!) }
        val articleConverter = ArticleConverterImpl()
        articlePhoto = view.findViewById(R.id.articlePhoto)
        sourcesBtn = view.findViewById(R.id.sourcesBtn)
        articleTitle = view.findViewById(R.id.articleTitle)
        articleParagraph = view.findViewById(R.id.articleParagraph)
        invisibleView = view.findViewById(R.id.invisibleView)
        article = articleConverter.convertPhotoArticleToArticle(photoArticle)
        photoArticle.paragraph?.subtitle = photoArticle.title
        setupPopupMenuOnLongClick()
        setupSourcesBtn()
        loadMainPhoto()
        return view
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetBehavior = BottomSheetBehavior.from(view?.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun newInstance(photoArticle: PhotoArticle): PhotoBottomSheetDialog {
        val args = Bundle()
        args.putSerializable("photoArticle", photoArticle)
        val photoSheet = PhotoBottomSheetDialog()
        photoSheet.arguments = args
        return photoSheet
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

    private fun setupPopupMenuOnLongClick(){
        articleTitle.setOnLongClickListener(
            getOnTextViewLongClick(photoArticle.paragraph!!, invisibleView))
        articleParagraph.setOnLongClickListener(
            getOnTextViewLongClick(photoArticle.paragraph!!, invisibleView))
    }

    private fun getOnTextViewLongClick(paragraph: Paragraph, view: View): View.OnLongClickListener{
        return View.OnLongClickListener {
            val popup = PopupMenu(activity, view)
            popup.menuInflater.inflate(R.menu.article_paragraph_popup_menu, popup.menu)
            popup.menu.getItem(0).setOnMenuItemClickListener { copyParagraph(paragraph)
                true
            }
            popup.menu.getItem(1).setOnMenuItemClickListener {
                SuggestionBottomSheetDialog().newInstance(article, 0, this)
                    .show(activity!!.supportFragmentManager, "Suggest")
                true
            }
            popup.show()
            true
        }
    }

    override fun onSuccess() {
        if (activity!=null) showToast(activity!!.getString(R.string.suggestions_sent))
    }

    override fun onFail() {
        if (activity!=null) showToast(activity!!.getString(R.string.suggestions_fail))
    }

    private fun showToast(text: String){
        val view = activity!!.layoutInflater.inflate(R.layout.toast_suggestion,
            activity!!.findViewById(R.id.toastView))
        view.findViewById<TextView>(R.id.toastText).text = text
        val toast = Toast(activity!!.applicationContext)
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.view = view
        toast.show()
    }

    private fun copyParagraph(paragraph: Paragraph){
        val stringBuilder = StringBuilder()
        if(paragraph.subtitle != null) stringBuilder.append("${paragraph.subtitle}. ")
        stringBuilder.append(paragraph.content)
        val clipboard: ClipboardManager? =
            activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("Renesans", stringBuilder.toString())
        clipboard?.primaryClip = clip
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
        if(context!=null && !photoBitmap.isRecycled) Glide.with(context!!).load(photoBitmap).into(articlePhoto)
    }
}