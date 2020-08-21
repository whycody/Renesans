package pl.renesans.renesans.sources

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
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
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_bottom_sheet_sources.view.*
import kotlinx.android.synthetic.main.dialog_bottom_sheet_suggestion.view.articlePhoto
import kotlinx.android.synthetic.main.dialog_bottom_sheet_suggestion.view.articleTitle
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl

class SourcesBottomSheetDialog: BottomSheetDialogFragment(), SourcesContract.SourcesView,
    ImageDaoContract.ImageDaoInterractor {

    private lateinit var article: Article
    private lateinit var articlePhoto: ImageView
    private lateinit var presenter: SourcesContract.SourcesPresenter
    private var deviceIsInLandscape = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_bottom_sheet_sources,
            container, false)
        if(arguments!=null) article = arguments!!.getSerializable("article") as Article
        deviceIsInLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        articlePhoto = view.articlePhoto
        view.articleTitle?.text = article.title
        presenter = SourcesPresenterImpl(activity!!.applicationContext, this)
        presenter.onCreate()
        val adapter = SourcesRecyclerAdapter(activity!!.applicationContext, presenter)
        val layoutManager =
            if(deviceIsInLandscape) GridLayoutManager(activity!!.applicationContext, 2)
            else LinearLayoutManager(activity!!.applicationContext)
        view.sourcesRecycler?.layoutManager = layoutManager
        view.sourcesRecycler?.adapter = adapter
        loadMainPhoto()
        return view
    }

    fun newInstance(article: Article): SourcesBottomSheetDialog{
        val args = Bundle()
        args.putSerializable("article", article)
        val sourcesSheet = SourcesBottomSheetDialog()
        sourcesSheet.arguments = args
        return sourcesSheet
    }

    override fun onResume() {
        super.onResume()
        activity?.overridePendingTransition(0, 0)
    }

    override fun onStart() {
        super.onStart()
        if(deviceIsInLandscape){
            val bottomSheetBehavior = BottomSheetBehavior.from(view?.parent as View)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun loadMainPhoto(){
        val imageDao = ImageDaoImpl(context!!, this)
        imageDao.loadPhoto(id = "Z0_0")
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

    override fun getArticleObject() = article

    override fun startUrlActivity(url: String) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
        activity?.overridePendingTransition(0, 0)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        if(context!=null) Glide.with(context!!).load(photoUri).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(context!=null) Glide.with(context!!).load(photoBitmap).into(articlePhoto)
    }
}