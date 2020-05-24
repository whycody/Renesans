package pl.renesans.renesans.discover

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager

import pl.renesans.renesans.R
import pl.renesans.renesans.article.ArticleFragment
import pl.renesans.renesans.data.Article

class DiscoverContentFragment : Fragment() {

    private var manager: FragmentManager? = null
    val discoverFragment = DiscoverFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover_content, container, false)
        if(manager == null) manager = childFragmentManager
        val transaction = manager?.beginTransaction()
        transaction?.replace(R.id.discoverContentFrame, discoverFragment)
        transaction?.commit()
        return view
    }

    fun showArticleInSecondPanel(article: Article) {
        val articleFragment = ArticleFragment()
        articleFragment.setArticle(article)
        val transaction = manager?.beginTransaction()
        transaction?.replace(R.id.discoverArticleFrame, articleFragment)
        transaction?.commit()
    }

}
