package pl.renesans.renesans.article

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_article.*
import pl.renesans.renesans.R
import pl.renesans.renesans.SuggestionBottomSheetDialog
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment
import pl.renesans.renesans.photo.PhotoActivity

class ArticleActivity : AppCompatActivity(), ArticleContract.ArticleView {

    private val imagesList = mutableListOf<ImageView>()
    private lateinit var presenter: ArticleContract.ArticlePresenter
    private lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        article = getArticleObject()
        setSupportActionBar(articleToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        articleToolbar.navigationIcon?.setColorFilter(ContextCompat.getColor(this,
            android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        imagesList.add(articleImage)
        val articleDao = ArticleDaoImpl()
        loadSizeOfImageView(articleDao.getObjectTypeFromObjectId(article.objectId!!))
        presenter = ArticlePresenterImpl(this, this)
        presenter.loadContent()
        showPhotoViewActivityOnImageViewClick()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val popup = PopupMenu(this, articleToolbar)
        popup.menuInflater.inflate(R.menu.article_popup_menu, menu)
        val subMenu = menu!!.getItem(0).subMenu
        subMenu.add(getString(R.string.new_paragraph))
            .setOnMenuItemClickListener(getOnMenuItemClickListener())
        return true
    }

   private fun getOnMenuItemClickListener(index: Int? = null): MenuItem.OnMenuItemClickListener {
       return MenuItem.OnMenuItemClickListener {
           SuggestionBottomSheetDialog(article, index).show(supportFragmentManager, "Suggest")
           true
       }
   }

    private fun loadSizeOfImageView(objectType: Int){
        val articleImageHeight = articleImage.layoutParams.height
        when (objectType){
            DiscoverRecyclerFragment.ARTS -> articleImage.layoutParams.height =
                (articleImageHeight * 1.5).toInt()
            DiscoverRecyclerFragment.OTHER_ERAS, DiscoverRecyclerFragment.PHOTOS,
            DiscoverRecyclerFragment.EVENTS -> articleImage.layoutParams.height =
                (articleImageHeight * 0.8).toInt()
        }
    }

    private fun showPhotoViewActivityOnImageViewClick(){
        imagesList.forEachIndexed{ index, image ->
            image.setOnClickListener{
                startPhotoViewActivity(getArticleObject().listOfPhotos!![index].objectId!!)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun getArticleObject(): Article {
        return intent.getSerializableExtra(ARTICLE) as Article
    }

    override fun setTitle(title: String) {
        articleToolbar.title = title
    }

    override fun loadBitmapToImage(bitmap: Bitmap, pos: Int) {
        Glide.with(applicationContext).load(bitmap).into(getImageAtPos(pos))
    }

    override fun loadUriToImage(uri: Uri, pos: Int) {
        Glide.with(applicationContext).load(uri).placeholder(getImageAtPos(pos).drawable).into(getImageAtPos(pos))
    }

    private fun getImageAtPos(pos: Int): ImageView{
        return imagesList[pos]
    }

    override fun addViewToArticleLinear(view: View) {
        articleLinear.addView(view)
        if(view is ImageView) imagesList.add(view)
    }

    private fun startPhotoViewActivity(photoId: String){
        val intent = Intent(this, PhotoActivity::class.java)
        intent.putExtra(PhotoActivity.ARTICLE_ID, photoId)
        startActivity(intent)
    }

    override fun addViewToHeaderLinear(view: View) {
        headerLinear.addView(view)
    }

    companion object {
        const val ARTICLE = "Article"
    }
}
