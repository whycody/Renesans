package pl.renesans.renesans.article

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_article.view.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.firebase.FirebaseContract
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment
import pl.renesans.renesans.photo.PhotoActivity


class ArticleFragment(var article: Article? = null,
                      private val articleActivityView: ArticleContract.ArticleActivityView? = null)
    : Fragment(), ArticleContract.ArticleFragmentView {

    private val imagesList = mutableListOf<ImageView>()
    private lateinit var presenter: ArticleContract.ArticlePresenter
    private lateinit var articleImage: ImageView
    private lateinit var articleLinear: LinearLayout
    private lateinit var headerLinear: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_article, container, false)
        articleImage = view.articleImage
        articleLinear = view.articleLinear
        headerLinear = view.headerLinear
        if(article == null) article = getArticleObject()
        imagesList.add(articleImage)
        val articleDao = ArticleDaoImpl()
        loadSizeOfImageView(articleDao.getObjectTypeFromObjectId(article?.objectId!!))
        presenter = ArticlePresenterImpl(activity!! as ArticleActivity, this, articleActivityView)
        presenter.loadContent()
        showPhotoViewActivityOnImageViewClick()
        return view
    }

    private fun loadSizeOfImageView(objectType: Int){
        setHeightOfArticleImage()
        val articleImageHeight = articleImage.layoutParams.height
        var checkingType = article?.typeOfScaling
        if(checkingType == null) checkingType = objectType
        when (checkingType){
            DiscoverRecyclerFragment.ARTS -> articleImage.layoutParams.height =
                (articleImageHeight * 1.5).toInt()
            DiscoverRecyclerFragment.OTHER_ERAS, DiscoverRecyclerFragment.PHOTOS,
            DiscoverRecyclerFragment.EVENTS -> articleImage.layoutParams.height =
                (articleImageHeight * 0.8).toInt()
        }
    }

    private fun setHeightOfArticleImage(){
        val display: Display = activity?.windowManager!!.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        articleImage.layoutParams.height = (width * 0.8).toInt()
    }

    private fun showPhotoViewActivityOnImageViewClick(){
        imagesList.forEachIndexed{ index, image ->
            image.setOnClickListener{
                startPhotoViewActivity(getArticleObject(), index)
            }
        }
    }

    private fun startPhotoViewActivity(article: Article, position: Int){
        val intent = Intent(context, PhotoActivity::class.java)
        intent.putExtra(PhotoActivity.ARTICLE, article)
        intent.putExtra(PhotoActivity.POSITION, position)
        startActivity(intent)
    }

    override fun getArticleObject(): Article {
        return activity?.intent?.getSerializableExtra(ArticleActivity.ARTICLE) as Article
    }

    override fun loadBitmapToImage(bitmap: Bitmap, pos: Int) {
        if(!bitmap.isRecycled && context!=null)
            Glide.with(context!!.applicationContext).load(bitmap).into(getImageAtPos(pos))
    }

    private fun getImageAtPos(pos: Int): ImageView{
        return imagesList[pos]
    }

    override fun loadUriToImage(uri: Uri, pos: Int) {
        if(context!=null)
            Glide.with(context!!.applicationContext).load(uri)
                .placeholder(getImageAtPos(pos).drawable).into(getImageAtPos(pos))
    }

    override fun addViewToArticleLinear(view: View) {
        articleLinear.addView(view)
        if(view is ImageView) imagesList.add(view)
    }

    override fun addViewToHeaderLinear(view: View) {
        headerLinear.addView(view)
    }
}