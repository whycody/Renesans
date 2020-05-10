package pl.renesans.renesans.tour

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.tour_slide_layout.view.*
import pl.renesans.renesans.R
import pl.renesans.renesans.SuggestionBottomSheetDialog
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Paragraph
import pl.renesans.renesans.data.Tour
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.sources.SourcesActivity
import java.lang.StringBuilder

class TourAdapter(private val activity: TourActivity, private val tour: Tour): PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as ScrollView
    }

    override fun getCount(): Int = tour.photosArticlesList!!.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.tour_slide_layout, container, false)
        val photoArticle = tour.photosArticlesList!![position]
        view.findViewById<TextView>(R.id.photoDescription).text = photoArticle.title
        view.findViewById<TextView>(R.id.articleTitle).text = photoArticle.photo?.description
        view.findViewById<TextView>(R.id.articleParagraph).text = photoArticle.paragraph?.content
        view.articleParagraph.setOnLongClickListener(
            getOnTextViewLongClick(photoArticle.paragraph!!,
            view.articleParagraph,
            position))
        view.articleTitle.setOnLongClickListener(
            getOnTextViewLongClick(photoArticle.paragraph!!,
                view.articleParagraph,
                position))
        setupSourceBtn(view.findViewById(R.id.sourcesBtn), position)
        container.addView(view)
        return view
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
            popup.menu.getItem(0).setOnMenuItemClickListener { copyParagraph(paragraph)
                true
            }
            popup.menu.getItem(1).setOnMenuItemClickListener {
                val hereArticle = getArticleWithHereOnStart(article)
                SuggestionBottomSheetDialog(hereArticle, 0)
                    .show(activity.supportFragmentManager, "Suggest")
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

    private fun getArticleWithHereOnStart(article: Article): Article{
        if(article.listOfParagraphs!![0].content?.substring(0, 8) == "To tutaj") return article
        val copyArticle = article.copy()
        val stringBuilder = StringBuilder()
        stringBuilder.append("To tutaj ")
        stringBuilder.append(copyArticle.listOfParagraphs!![0].content)
        copyArticle.listOfParagraphs!![0].content = stringBuilder.toString()
        return copyArticle
    }

    private fun startSourceActivity(article: Article){
        val intent = Intent(activity, SourcesActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE, article)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ScrollView)
    }
}