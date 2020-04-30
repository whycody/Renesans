package pl.renesans.renesans.tour

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import pl.renesans.renesans.R
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.Tour
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.sources.SourcesActivity

class TourAdapter(private val context: Context, private val tour: Tour): PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as ScrollView
    }

    override fun getCount(): Int = tour.photosArticlesList!!.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.tour_slide_layout, container, false)
        val photoArticle = tour.photosArticlesList!![position]
        view.findViewById<TextView>(R.id.photoDescription).text = photoArticle.title
        view.findViewById<TextView>(R.id.articleTitle).text = photoArticle.photo?.description
        view.findViewById<TextView>(R.id.articleParagraph).text = photoArticle.paragraph?.content
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

    private fun startSourceActivity(article: Article){
        val intent = Intent(context, SourcesActivity::class.java)
        intent.putExtra(ArticleActivity.ARTICLE, article)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ScrollView)
    }
}