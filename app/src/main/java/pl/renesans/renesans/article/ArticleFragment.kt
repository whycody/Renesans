package pl.renesans.renesans.article

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_article.view.*
import pl.renesans.renesans.R
import pl.renesans.renesans.article.recycler.RelatedAdapter
import pl.renesans.renesans.article.recycler.RelatedContract
import pl.renesans.renesans.article.recycler.RelatedPresenterImpl
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.Photo
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.photo.PhotoActivity
import pl.renesans.renesans.utility.article.views.ArticleViewsUtility
import pl.renesans.renesans.utility.article.views.ArticleViewsUtilityImpl
import pl.renesans.renesans.utility.bookmark.BookmarkUtilityImpl
import pl.renesans.renesans.utility.bookmark.BookmarkUtilityInterractor

class ArticleFragment(private var article: Article? = null,
                      private val articleActivityView: ArticleContract.ArticleActivityView? = null)
    : Fragment(), ImageDaoContract.ImageDaoInterractor, ArticleContract.ArticleFragmentView,
    BookmarkUtilityInterractor.BookmarkView {

    private val imagesList = mutableListOf<ImageView>()
    private lateinit var presenter: ArticleContract.ArticlePresenter
    private lateinit var articleImage: ImageView
    private lateinit var articleLinear: LinearLayout
    private lateinit var headerLinear: LinearLayout
    private lateinit var bookmarkView: ImageView
    private lateinit var imageDao: ImageDaoContract.ImageDao
    private lateinit var articleViewsUtility: ArticleViewsUtility
    private lateinit var bookmarkUtility: BookmarkUtilityInterractor.BookmarkUtility
    private val invisibleViews = hashMapOf<Int, View>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_article, container, false)
        articleImage = view.articleImage
        articleLinear = view.articleLinear
        headerLinear = view.headerLinear
        bookmarkView = view.articleBookmark
        imagesList.add(articleImage)
        article = getArticleObject()
        imageDao = ImageDaoImpl(context!!.applicationContext, this)
        articleViewsUtility = ArticleViewsUtilityImpl(activity!!.applicationContext)
        bookmarkUtility = BookmarkUtilityImpl(context!!, this, article?.objectId!!)
        bookmarkView.setOnClickListener { bookmarkUtility.handleBookmarkOnClick() }
        presenter = ArticlePresenterImpl(article!!, this)
        val objectType = ArticleDaoImpl().getObjectTypeFromObjectId(article?.objectId!!)
        presenter.setArticleImageHeight(articleImage.layoutParams.height, objectType)
        presenter.loadContent()
        showPhotoViewActivityOnImageViewClick()
        return view
    }

    override fun changeColorOfBookmark(active: Boolean) {
        val colorFilter = if(active) PorterDuffColorFilter(ContextCompat
            .getColor(context!!, R.color.colorTitleGray), PorterDuff.Mode.SRC_ATOP)
        else PorterDuffColorFilter(ContextCompat
            .getColor(context!!, R.color.colorImageGray), PorterDuff.Mode.SRC_ATOP)
        bookmarkView.drawable.colorFilter = colorFilter
    }

    override fun setDefaultHeightOfArticleImage() {
        val display: Display = activity?.windowManager!!.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        articleImage.layoutParams.height = (width * 0.8).toInt()
    }

    private fun showPhotoViewActivityOnImageViewClick() {
        imagesList.forEachIndexed { index, image ->
            image.setOnClickListener { startPhotoViewActivity(getArticleObject(), index) }
        }
    }

    private fun startPhotoViewActivity(article: Article, position: Int) {
        val intent = Intent(context, PhotoActivity::class.java)
        intent.putExtra(PhotoActivity.ARTICLE, article)
        intent.putExtra(PhotoActivity.POSITION, position)
        startActivity(intent)
    }

    private val articleConverter = ArticleConverterImpl()

    override fun getArticleObject(): Article {
        return if (article != null) article!!
        else if (activity?.intent?.getSerializableExtra(ArticleActivity.ARTICLE) != null)
            activity?.intent?.getSerializableExtra(ArticleActivity.ARTICLE) as Article
        else articleConverter.convertPhotoArticleToArticle(
            activity?.intent?.getSerializableExtra(ArticleActivity.PHOTO_ARTICLE) as PhotoArticle
        )
    }

    override fun setArticleImageHeight(height: Int) {
        articleImage.layoutParams.height = height
    }

    override fun setToolbarTitle(title: String) {
        articleActivityView?.setToolbarTitle(title)
    }

    override fun loadPhoto(pos: Int, id: String) = imageDao.loadPhoto(pos, id)

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        if(context == null) return
        Glide.with(context!!.applicationContext)
            .load(photoUri)
            .placeholder(getImageAtPos(pos).drawable)
            .into(getImageAtPos(pos))
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        if(context == null) return
        Glide.with(context!!.applicationContext).load(photoBitmap).into(getImageAtPos(pos))
    }

    private fun getImageAtPos(pos: Int) = imagesList[pos]

    override fun addHeaderTitleView(text: String?)
            = addViewToHeaderLinear(articleViewsUtility.getHeaderTitleView(text))

    override fun addHeaderContentView(text: String?)
            = addViewToHeaderLinear(articleViewsUtility.getHeaderContentView(text))

    override fun addRelatedRecyclerView() {
        val relatedPresenter = RelatedPresenterImpl(
            activity!!.applicationContext, activity!!.supportFragmentManager, article!!)
        if(relatedPresenter.getItemCount()!=0)
            addRelatedArticlesToLayout(relatedPresenter)
    }

    private fun addRelatedArticlesToLayout(relatedPresenter: RelatedContract.RelatedPresenter) {
        addParagraphTitleView(getString(R.string.relations))
        val relatedRecyclerView = articleViewsUtility.getRelatedRecyclerView()
        val relatedAdapter = RelatedAdapter(context!!.applicationContext, relatedPresenter)
        relatedRecyclerView.adapter = relatedAdapter
        addViewToArticleLayout(relatedRecyclerView)
    }

    private fun addViewToHeaderLinear(view: View) = headerLinear.addView(view)

    override fun addParagraphTitleView(text: String?, index: Int?) {
        val paragraphTitleView = articleViewsUtility.getParagraphTitleView(text)
        if(index != null) setOnLongClickListener(paragraphTitleView, index)
        addViewToArticleLayout(paragraphTitleView)
    }

    override fun addParagraphContentView(text: String?, index: Int?) {
        val paragraphContentView = articleViewsUtility.getParagraphContentView(text)
        if(index != null) setOnLongClickListener(paragraphContentView, index)
        addViewToArticleLayout(paragraphContentView)
    }

    private fun setOnLongClickListener(textView: TextView, index: Int) {
        val onLongClickListener = getOnTextViewLongClick(
            article!!.listOfParagraphs!![index], invisibleViews[index]!!, index)
        textView.setOnLongClickListener(onLongClickListener)
    }

    private fun getOnTextViewLongClick(paragraph: Paragraph,
                                       view: View, index: Int): View.OnLongClickListener {
        return View.OnLongClickListener {
            showPopupMenu(paragraph, view, index)
            true
        }
    }

    private fun showPopupMenu(paragraph: Paragraph, view: View, index: Int) {
        val popup = PopupMenu(activity, view)
        val menu = popup.menu
        popup.menuInflater.inflate(R.menu.article_paragraph_popup_menu, popup.menu)
        menu.getItem(0).setOnMenuItemClickListener { copyParagraph(paragraph) }
        menu.getItem(1).setOnMenuItemClickListener { showSuggestionBottomSheet(index) }
        popup.show()
    }

    private fun copyParagraph(paragraph: Paragraph): Boolean {
        presenter.copyParagraph(paragraph)
        return true
    }

    override fun showSuggestionBottomSheet(paragraph: Int?) =
        articleActivityView?.showSuggestionBottomSheet(paragraph)!!

    override fun copyTextToClipboard(text: String) {
        val clipboard: ClipboardManager? =
            activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText(getString(R.string.app_name), text)
        clipboard?.setPrimaryClip(clip)
    }

    override fun getInvisibleView(bigMargin: Boolean) =
        articleViewsUtility.getInvisibleView(bigMargin)

    override fun saveInvisibleView(index: Int, view: View) {
        invisibleViews[index] = view
    }

    override fun addParagraphImageView(photo: Photo) {
        val paragraphImageView = articleViewsUtility.getParagraphImageView(photo)
        addViewToArticleLayout(paragraphImageView)
        imagesList.add(paragraphImageView)
    }

    override fun addParagraphImageDescriptionView(text: String?) =
        addViewToArticleLayout(articleViewsUtility.getParagraphImageDescriptionView(text))

    override fun addViewToArticleLayout(view: View) = articleLinear.addView(view)
}