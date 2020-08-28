package pl.renesans.renesans.bookmark

import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_bottom_sheet_sources.view.*
import kotlinx.android.synthetic.main.dialog_bottom_sheet_suggestion.view.articleDescription
import kotlinx.android.synthetic.main.dialog_bottom_sheet_suggestion.view.articlePhoto
import kotlinx.android.synthetic.main.dialog_bottom_sheet_suggestion.view.articleTitle
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.data.realm.bookmark.BookmarkDaoImpl
import pl.renesans.renesans.sources.SourcesRecyclerAdapter
import pl.renesans.renesans.sources.SourcesRecyclerDecoration

class BookmarkBottomSheetDialog:
    BottomSheetDialogFragment(), ImageDaoContract.ImageDaoInterractor, BookmarkContract.BookmarkView {

    private lateinit var headerPhoto: ImageView
    private lateinit var headerDescription: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: BookmarkPresenterImpl
    private lateinit var adapter: SourcesRecyclerAdapter
    private lateinit var decoration: SourcesRecyclerDecoration
    private var deviceIsInLandscape = false
    private var behavior: BottomSheetBehavior<View>? = null
    private val MODE = "mode"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_bottom_sheet_sources, container)
        headerPhoto = view.articlePhoto
        headerDescription = view.articleDescription
        recyclerView = view.sourcesRecycler
        presenter = BookmarkPresenterImpl(context!!, this)
        checkBundle(savedInstanceState)
        adapter = SourcesRecyclerAdapter(context!!, presenter)
        view.articleTitle?.text = context?.getString(R.string.bookmarks)
        setDescription(presenter.getCurrentMode())
        recyclerView.adapter = adapter
        decoration = (SourcesRecyclerDecoration(context!!, presenter.getItemCount()))
        recyclerView.addItemDecoration(decoration)
        setLayoutManagerOfRecycler()
        loadMainPhoto()
        return view
    }

    private fun checkBundle(savedInstanceState: Bundle?){
        if(savedInstanceState?.getString(MODE) != null)
            presenter.setMode(savedInstanceState.getString(MODE)!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODE, presenter.getCurrentMode())
    }

    private fun loadMainPhoto() {
        val imageDao = ImageDaoImpl(context!!, this)
        imageDao.loadPhoto(id = "Z6_0")
    }

    private fun setLayoutManagerOfRecycler() {
        deviceIsInLandscape =
            resources.configuration.orientation== Configuration.ORIENTATION_LANDSCAPE
        val layoutManager =
            if(deviceIsInLandscape) GridLayoutManager(activity!!.applicationContext, 2)
            else LinearLayoutManager(activity!!.applicationContext)
        recyclerView.layoutManager = layoutManager
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) setWhiteNavigationBar(dialog)
        dialog.setOnKeyListener{ dialog, keycode, event -> presenter.onBackPressed() }
        dialog.setOnShowListener{
            val bottomSheet: BottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal: View =
                bottomSheet.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            behavior = BottomSheetBehavior.from(bottomSheetInternal)
            behavior?.peekHeight = context?.resources?.getDimension(R.dimen.bookmarkBottomSheetPeekHeight)!!.toInt()
            behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            setBottomSheetCallback()
        }
        return dialog
    }

    private fun setBottomSheetCallback(){
        behavior?.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                checkState(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    private fun checkState(newState: Int){
        if(presenter.getCurrentMode() == BookmarkDaoImpl.LISTS_MODE
            && newState == BottomSheetBehavior.STATE_EXPANDED)
            behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        else if(newState == BottomSheetBehavior.STATE_HIDDEN)
            dismiss()
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
        if(context!=null) Glide.with(context!!)
            .load(photoUri).placeholder(headerPhoto.drawable).into(headerPhoto)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(context!=null) Glide.with(context!!).load(photoBitmap).into(headerPhoto)
    }

    override fun notifyBookmarksDataSetChanged(mode: String) {
        adapter.notifyDataSetChanged()
        decoration.setItemCount(presenter.getItemCount())
        setDescription(mode)
        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setDescription(mode: String){
        if(mode == BookmarkDaoImpl.PLACES_MODE) headerDescription.text = context?.getText(R.string.saved_places)
        else if(mode == BookmarkDaoImpl.ALL_ARTICLES_MODE) headerDescription.text = context?.getText(R.string.all)
        else headerDescription.text = getString(R.string.saved_articles)
    }

    override fun startArticleActivity(articleId: String) {
        (activity as MainActivity).startArticleActivity(articleId)
    }

    override fun openPhotoBottomSheet(articleId: String) {
        dismiss()
        (activity as MainActivity).showPhotoArticleOnMap(articleId)
    }
}