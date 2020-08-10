package pl.renesans.renesans.tour

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.tour_slide_layout.view.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.PhotoArticle
import pl.renesans.renesans.data.Tour
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.firebase.FirebaseContract
import pl.renesans.renesans.sources.SourcesBottomSheetDialog
import pl.renesans.renesans.toast.ToastHelperImpl
import java.lang.StringBuilder

class TourAdapter(private val activity: TourActivity, private val tour: Tour): PagerAdapter(),
    FirebaseContract.FirebaseInterractor {

    override fun isViewFromObject(view: View, `object`: Any) = view == `object` as ScrollView

    override fun getCount(): Int = tour.photosArticlesList!!.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.tour_slide_layout, container, false)
        val photoArticle = tour.photosArticlesList!![position]
        view.findViewById<TextView>(R.id.photoDescription).text = getPhotoDescription(photoArticle)
        view.findViewById<TextView>(R.id.articleTitle).text = photoArticle.photo?.description
        view.findViewById<TextView>(R.id.articleParagraph).text = photoArticle.paragraph?.content
        val onTextLongClickListener =
            getOnTextViewLongClick(photoArticle.paragraph!!, view.articleParagraph, position)
        view.articleParagraph.setOnLongClickListener(onTextLongClickListener)
        view.articleTitle.setOnLongClickListener(onTextLongClickListener)
        setupSourceBtn(view.findViewById(R.id.sourcesBtn), position)
        container.addView(view)
        return view
    }

    private fun getPhotoDescription(photoArticle: PhotoArticle): String{
        return if(photoArticle.position == null) activity.getString(R.string.no_place)
        else photoArticle.title!!
    }

    private fun setupSourceBtn(sourcesBtn: Button, pos: Int){
        val articleDao = ArticleDaoImpl()
        val articleConverter = ArticleConverterImpl()
        val article = articleConverter.convertPhotoArticleToArticle(tour.photosArticlesList!![pos])
        if(!articleDao.articleHasSources(article)) sourcesBtn.visibility = View.GONE
        else sourcesBtn.setOnClickListener{ startSourceActivity(article) }
    }

    private fun getOnTextViewLongClick(paragraph: Paragraph, view: View, pos: Int): View.OnLongClickListener{
        val converter = ArticleConverterImpl()
        val article = converter.convertPhotoArticleToArticle(tour.photosArticlesList!![pos])
        return View.OnLongClickListener {
            val popup = PopupMenu(activity, view)
            popup.menuInflater.inflate(R.menu.article_paragraph_popup_menu, popup.menu)
            popup.menu.getItem(0).setOnMenuItemClickListener {
                copyParagraph(paragraph)
                true
            }
            popup.menu.getItem(1).setOnMenuItemClickListener {
                activity.showSuggestionBottomSheet(article)
                true
            }
            popup.show()
            true
        }
    }

    private fun copyParagraph(paragraph: Paragraph){
        val stringBuilder = StringBuilder()
        if(paragraph.subtitle != null)
            stringBuilder.append("${paragraph.subtitle} - ${activity.getString(R.string.thats_here)}. ")
        stringBuilder.append(paragraph.content)
        val clipboard: ClipboardManager? =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText(
            activity.getString(R.string.app_name), stringBuilder.toString())
        clipboard?.primaryClip = clip
    }

    private fun startSourceActivity(article: Article){
        val articleWithTitle = article.copy()
        if(article.listOfPhotos != null && article.listOfPhotos!![0].description != null)
            articleWithTitle.title = article.listOfPhotos!![0].description
        SourcesBottomSheetDialog().newInstance(articleWithTitle)
            .show(activity.supportFragmentManager, "Sources")
    }

    private val toastHelper = ToastHelperImpl(activity)

    override fun onSuccess() = toastHelper.showToast(activity.getString(R.string.suggestions_sent))

    override fun onFail() = toastHelper.showToast(activity.getString(R.string.suggestions_fail))

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) =
        container.removeView(`object` as ScrollView)
}