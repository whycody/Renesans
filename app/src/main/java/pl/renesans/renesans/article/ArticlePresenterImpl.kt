package pl.renesans.renesans.article

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.*
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R
import pl.renesans.renesans.article.recycler.RelatedAdapter
import pl.renesans.renesans.article.recycler.RelatedContract
import pl.renesans.renesans.article.recycler.RelatedPresenterImpl
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerDecoration
import java.lang.StringBuilder

class ArticlePresenterImpl(val activity: ArticleActivity,
                           private val articleFragmentView: ArticleContract.ArticleFragmentView,
                           private val articleActivityView: ArticleContract.ArticleActivityView? = null):
    ArticleContract.ArticlePresenter, ImageDaoContract.ImageDaoInterractor {

    private lateinit var article: Article
    private var listOfPhotos: List<Photo>? = null
    private lateinit var imageDao: ImageDaoContract.ImageDao
    private var articleMargin = 0
    private var articleBigUpMargin = 0
    private var articleSmallUpMargin = 0
    private var numberOfPhoto = 1

    override fun loadContent() {
        article = articleFragmentView.getArticleObject()
        listOfPhotos = article.listOfPhotos
        imageDao = ImageDaoImpl(activity, this)
        articleMargin = activity.resources.getDimension(R.dimen.articleMargin).toInt()
        articleBigUpMargin = activity.resources.getDimension(R.dimen.articleBigUpMargin).toInt()
        articleSmallUpMargin = activity.resources.getDimension(R.dimen.articleSmallUpMargin).toInt()
        articleActivityView?.setTitle(article.title!!)
        loadMainPhoto()
        loadHeader()
        loadParagraphs()
        loadRelations()
    }

    private fun loadMainPhoto(){
        if(listOfPhotos!=null) imageDao.loadPhoto(0, listOfPhotos!![0].objectId!!)
        else imageDao.loadPhoto(0, article.objectId + "_0")
    }

    private fun loadHeader(){
        addTitleToHeader()
        if(article.header?.content != null) addContentToHeader()
    }

    private fun addTitleToHeader(){
        val textView = TextView(activity)
        TextViewCompat.setTextAppearance(textView, R.style.ArticleHeaderTitleTextViewStyle)
        textView.text = article.title
        textView.alpha = .8f
        textView.setPadding(0, 0, 0, 10)
        articleFragmentView.addViewToHeaderLinear(textView)
    }

    private fun addContentToHeader(){
        val contentTextView = TextView(activity)
        TextViewCompat.setTextAppearance(contentTextView, R.style.ArticleHeaderContentTextViewStyle)
        createContentFromHeaderPairs(contentTextView)
        contentTextView.alpha = .8f
        contentTextView.setLineSpacing(10f, 1f)
        articleFragmentView.addViewToHeaderLinear(contentTextView)
    }

    private fun createContentFromHeaderPairs(textView: TextView){
        var content = ""
        article.header!!.content!!.keys.forEachIndexed { index, key ->
            if(index!=0) content += "\n"
            content += key + ": " + article.header!!.content!![key]
        }
        textView.text = content
    }

    private fun loadParagraphs(){
        article.listOfParagraphs?.forEachIndexed { index, paragraph ->
            loadImage(index-1)
            val subtitleIsAvailable = paragraph.subtitle!=null
            val paragraphTextView = getParagraphContentTextView(paragraph.content!!)
            val invisibleView = getInvisibleView(subtitleIsAvailable)
            paragraphTextView
                .setOnLongClickListener(getOnTextViewLongClick(paragraph, invisibleView, index))
            if(subtitleIsAvailable) addSubtitleOfParagraph(paragraph, invisibleView, index)
            articleFragmentView.addViewToArticleLinear(invisibleView)
            articleFragmentView.addViewToArticleLinear(paragraphTextView)
        }
        loadImage(article.listOfParagraphs?.size?.minus(1))
    }

    private fun loadImage(index: Int?) =
        loadImageAsParagraph(listOfPhotos?.find { it.numberOfParagraph == index })

    private fun getParagraphContentTextView(content: String): TextView{
        val textView = TextView(activity)
        TextViewCompat.setTextAppearance(textView, R.style.ArticleContentContentTextViewStyle)
        textView.text = content
        textView.setLineSpacing(25f, 1f)
        val linearParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        linearParams.setMargins(articleMargin, 0, articleMargin, 0)
        textView.layoutParams = linearParams
        return textView
    }

    private fun getInvisibleView(subtitleAvailable: Boolean): View{
        val view = View(activity)
        val linearParams = LinearLayout.LayoutParams(1, 1)
        if(!subtitleAvailable) linearParams.setMargins(articleMargin, articleBigUpMargin, articleMargin, 0)
        else linearParams.setMargins(articleMargin, articleSmallUpMargin, articleMargin, 0)
        view.layoutParams = linearParams
        view.setBackgroundColor(Color.TRANSPARENT)
        return view
    }

    private fun addSubtitleOfParagraph(paragraph: Paragraph, invisibleView: View, index: Int){
        val subtitleTextView = getParagraphTitleTextView(paragraph.subtitle!!)
        subtitleTextView.setOnLongClickListener(getOnTextViewLongClick(paragraph, invisibleView, index))
        articleFragmentView.addViewToArticleLinear(subtitleTextView)
    }

    private fun getParagraphTitleTextView(title: String): TextView{
        val textView = TextView(activity)
        TextViewCompat.setTextAppearance(textView, R.style.ArticleContentTitleTextViewStyle)
        textView.text = title
        textView.setPadding(articleMargin, articleBigUpMargin, articleMargin, 0)
        return textView
    }

    private fun getOnTextViewLongClick(paragraph: Paragraph, view: View, index: Int): View.OnLongClickListener{
        return View.OnLongClickListener {
            val popup = PopupMenu(activity, view)
            popup.menuInflater.inflate(R.menu.article_paragraph_popup_menu, popup.menu)
            popup.menu.getItem(0).setOnMenuItemClickListener { copyParagraph(paragraph)
                true
            }
            popup.menu.getItem(1).setOnMenuItemClickListener {
                activity.showSuggestionBottomSheet(index)
                true
            }
            popup.show()
            true
        }
    }

    private fun copyParagraph(paragraph: Paragraph){
        val stringBuilder = StringBuilder()
        if(paragraph.subtitle != null) stringBuilder.append("${paragraph.subtitle}. ")
        stringBuilder.append(paragraph.content)
        val clipboard: ClipboardManager? =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("Renesans", stringBuilder.toString())
        clipboard?.primaryClip = clip
    }

    private fun loadImageAsParagraph(photo: Photo?){
        if(photo!=null){
            val imageView = ImageView(activity)
            imageView.adjustViewBounds = true
            imageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            imageView.setPadding(0, articleBigUpMargin, 0, 0)
            articleFragmentView.addViewToArticleLinear(imageView)
            if(photo.description!=null) loadDescriptionOfPhoto(photo)
            imageDao.loadPhoto(numberOfPhoto, photo.objectId!!)
            numberOfPhoto++
        }
    }

    private fun loadDescriptionOfPhoto(photo: Photo){
        val descriptionTextView = TextView(activity)
        TextViewCompat.setTextAppearance(descriptionTextView, R.style.ArticleHeaderContentTextViewStyle)
        descriptionTextView.text = photo.description!!
        descriptionTextView.setLineSpacing(10f, 1f)
        descriptionTextView.setPadding(articleMargin, articleSmallUpMargin, articleMargin, 0)
        articleFragmentView.addViewToArticleLinear(descriptionTextView)
    }

    private fun loadRelations(){
        val relatedPresenter = RelatedPresenterImpl(activity, activity.supportFragmentManager, article)
        relatedPresenter.onCreate()
        if(relatedPresenter.getItemCount()!=0) loadRelationsView(relatedPresenter)
    }
    
    private fun loadRelationsView(relatedPresenter: RelatedContract.RelatedPresenter){
        val recyclerView = RecyclerView(activity)
        val relatedAdapter = RelatedAdapter(activity, relatedPresenter)
        recyclerView.addItemDecoration(DiscoverRecyclerDecoration(activity))
        recyclerView.layoutManager = LinearLayoutManager(activity.applicationContext,
            LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = relatedAdapter
        recyclerView.setPadding(0, articleSmallUpMargin, 0, 0)
        articleFragmentView.addViewToArticleLinear(getParagraphTitleTextView(activity.getString(R.string.relations)))
        articleFragmentView.addViewToArticleLinear(recyclerView)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) =
        articleFragmentView.loadUriToImage(photoUri, pos)

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) =
        articleFragmentView.loadBitmapToImage(photoBitmap, pos)
}