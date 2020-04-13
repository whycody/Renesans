package pl.renesans.renesans.article

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ImageDaoContract
import pl.renesans.renesans.data.ImageDaoImpl
import pl.renesans.renesans.data.Photo

class ArticlePresenterImpl(val context: Context, val articleView: ArticleContract.ArticleView):
    ArticleContract.ArticlePresenter, ImageDaoContract.ImageDaoInterractor {

    private lateinit var article: Article
    private var listOfPhotos: List<Photo>? = null
    private lateinit var imageDao: ImageDaoContract.ImageDao
    private var articleMargin = 0
    private var articleBigUpMargin = 0
    private var articleSmallUpMargin = 0
    private var numberOfPhoto = 1

    override fun loadContent() {
        article = articleView.getArticleObject()
        listOfPhotos = article.listOfPhotos
        imageDao = ImageDaoImpl(context, this)
        articleMargin = context.resources.getDimension(R.dimen.articleMargin).toInt()
        articleBigUpMargin = context.resources.getDimension(R.dimen.articleBigUpMargin).toInt()
        articleSmallUpMargin = context.resources.getDimension(R.dimen.articleSmallUpMargin).toInt()
        articleView.setTitle(article.title!!)
        loadMainPhoto()
        loadHeader()
        loadParagraphs()
    }

    private fun loadMainPhoto(){
        if(listOfPhotos!=null) imageDao.loadPhotoInBothQualities(0, listOfPhotos!![0].objectId!!)
        else imageDao.loadPhotoInBothQualities(0, article.objectId + "_0")
    }

    private fun loadHeader(){
        addTitleToHeader()
        if(article.header?.content != null) addContentToHeader()
    }

    private fun addTitleToHeader(){
        val textView = TextView(context)
        TextViewCompat.setTextAppearance(textView, R.style.ArticleHeaderTitleTextviewStyle)
        textView.text = article.title
        textView.alpha = .8f
        textView.setPadding(0, 0, 0, 10)
        articleView.addViewToHeaderLinear(textView)
    }

    private fun addContentToHeader(){
        val contentTextView = TextView(context)
        TextViewCompat.setTextAppearance(contentTextView, R.style.ArticleHeaderContentTextviewStyle)
        createContentFromHeaderPairs(contentTextView)
        contentTextView.alpha = .8f
        contentTextView.setLineSpacing(10f, 1f)
        articleView.addViewToHeaderLinear(contentTextView)
    }

    private fun createContentFromHeaderPairs(textView: TextView){
        var content = ""
        article.header!!.content!!.forEachIndexed { index, pair ->
            if(index!=0) content += "\n"
            content += pair.first + ": " + pair.second
        }
        textView.text = content
    }

    private fun loadParagraphs(){
        article.listOfParagraphs?.forEachIndexed { index, paragraph ->
            loadImageAsParagraph(listOfPhotos?.find { it.numberOfParagraph == index-1 })
            val subtitleIsAvailable = paragraph.subtitle!=null
            if(subtitleIsAvailable)
                articleView.addViewToArticleLinear(getParagraphTitleTextView(paragraph.subtitle!!))
            articleView.addViewToArticleLinear(getParagraphContentTextView(paragraph.content!!, subtitleIsAvailable))
        }
    }

    private fun getParagraphTitleTextView(title: String): TextView{
        val textView = TextView(context)
        TextViewCompat.setTextAppearance(textView, R.style.ArticleContentTitleTextviewStyle)
        textView.text = title
        textView.setPadding(articleMargin, articleBigUpMargin, articleMargin, 0)
        return textView
    }

    private fun getParagraphContentTextView(content: String, subtitleAvailable: Boolean): TextView{
        val textView = TextView(context)
        TextViewCompat.setTextAppearance(textView, R.style.ArticleContentContentTextviewStyle)
        textView.text = content
        textView.setLineSpacing(25f, 1f)
        if(!subtitleAvailable) textView.setPadding(articleMargin, articleBigUpMargin, articleMargin, 0)
        else textView.setPadding(articleMargin, articleSmallUpMargin, articleMargin, 0)
        return textView
    }

    private fun loadImageAsParagraph(photo: Photo?){
        if(photo!=null){
            val imageView = ImageView(context)
            imageView.adjustViewBounds = true
            imageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            imageView.setPadding(0, articleBigUpMargin, 0, 0)
            articleView.addViewToArticleLinear(imageView)
            imageDao.loadPhotoInBothQualities(numberOfPhoto, photo.objectId!!)
            numberOfPhoto++
        }
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        articleView.loadUriToImage(photoUri, pos)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        articleView.loadBitmapToImage(photoBitmap, pos)
    }
}