package pl.renesans.renesans

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_bottom_sheet_suggestion.*
import kotlinx.android.synthetic.main.dialog_bottom_sheet_suggestion.view.*
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.Suggestion
import pl.renesans.renesans.data.firebase.FirebaseContract
import pl.renesans.renesans.data.firebase.FirebaseDaoImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl


class SuggestionBottomSheetDialog():
    BottomSheetDialogFragment(), ImageDaoContract.ImageDaoInterractor, TextWatcher {

    private lateinit var articlePhoto: ImageView
    private lateinit var paragraph: Paragraph
    private lateinit var article: Article
    private var numberOfParagraph: Int? = null
    private lateinit var firebaseInterractor: FirebaseContract.FirebaseInterractor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_bottom_sheet_suggestion,
            container, false)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        initializeObjects()
        articlePhoto = view.articlePhoto
        view.articleTitle?.text = article.title
        if(numberOfParagraph != null)
            view.articleDescription?.text = "Sugerowanie zmian ・akapit ${numberOfParagraph!! + 1}"
        else view.articleDescription?.text = "Sugerowanie zmian ・ nowy akapit"
        paragraph = Paragraph()
        if(numberOfParagraph != null) paragraph = article.listOfParagraphs!![numberOfParagraph!!]
        if(paragraph.subtitle != null) view.titleOfParagraphView?.setText(paragraph.subtitle)
        if(paragraph.content != null) view.contentOfParagraphView?.setText(paragraph.content)
        view.sendBtn.setOnClickListener{sendChangesToFirebase(
            Suggestion(article.objectId, numberOfParagraph,
                view.titleOfParagraphView.text.toString(),
                view.contentOfParagraphView.text.toString(),
                commentView.text.toString()))}
        view.titleOfParagraphView.addTextChangedListener(this)
        view.contentOfParagraphView.addTextChangedListener(this)
        view.commentView.addTextChangedListener(this)
        loadMainPhoto()
        return view
    }

    private fun initializeObjects(){
        if(arguments!=null){
            article = arguments!!.getSerializable("article") as Article
            if(arguments!!.containsKey("numberOfParagraph"))
                numberOfParagraph = arguments!!.getInt("numberOfParagraph")
            firebaseInterractor = arguments!!.getSerializable("firebaseInterractor")
                    as FirebaseContract.FirebaseInterractor
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) setWhiteNavigationBar(dialog)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetBehavior = BottomSheetBehavior.from(view?.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun newInstance(article: Article,
                    numberOfParagraph: Int?,
                    firebaseInterractor: FirebaseContract.FirebaseInterractor? = null): SuggestionBottomSheetDialog {
        val args = Bundle()
        args.putSerializable("article", article)
        if(numberOfParagraph!=null) args.putInt("numberOfParagraph", numberOfParagraph)
        args.putSerializable("firebaseInterractor", firebaseInterractor)
        val suggestSheet = SuggestionBottomSheetDialog()
        suggestSheet.arguments = args
        return suggestSheet
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

    private fun sendChangesToFirebase(suggestion: Suggestion){
        val firebaseDao = FirebaseDaoImpl(firebaseInterractor)
        firebaseDao.putSuggestionToFirebase(suggestion)
        dismiss()
    }

    private fun loadMainPhoto(){
        val imageDao = ImageDaoImpl(context!!, this)
        if(article.listOfPhotos!=null && article.listOfPhotos!![0].objectId!=null)
            imageDao.loadPhotoInBothQualities(id = article.listOfPhotos!![0].objectId!!)
        else imageDao.loadPhotoInBothQualities(0, article.objectId + "_0")
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        if(context!=null) Glide.with(context!!).load(photoUri).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(context!=null) Glide.with(context!!).load(photoBitmap).into(articlePhoto)
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val paragraphChanged = paragraph.content!=null &&
                view?.contentOfParagraphView?.text.toString() != paragraph.content!! ||
                (paragraph.content==null && view?.contentOfParagraphView?.text!!.isNotEmpty())
        val subtitleChanged = (paragraph.subtitle!=null &&
                view?.titleOfParagraphView?.text.toString() != paragraph.subtitle!!) ||
                (paragraph.subtitle==null && view?.titleOfParagraphView?.text!!.isNotEmpty())
        val commentFilled = view?.commentView?.text!!.isNotEmpty()

        view?.sendBtn?.isEnabled = paragraphChanged || subtitleChanged || commentFilled
    }
}