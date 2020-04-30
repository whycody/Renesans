package pl.renesans.renesans.tour

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_tour.*
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Tour
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl

class TourActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    ImageDaoContract.ImageDaoInterractor, TourContract.TourView {

    private lateinit var tour: Tour
    private lateinit var presenter: TourContract.TourPresenter
    private var dots = mutableListOf<View>()
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour)
        setSupportActionBar(tourToolbar)
        tour = getTourObject()
        tourToolbar.title = tour.title
        presenter = TourPresenterImpl(applicationContext, this)
        val tourAdapter = TourAdapter(this, tour)
        tourPager.adapter = tourAdapter
        tourPager.addOnPageChangeListener(this)
        backBtn.setOnClickListener{ showPreviousPage() }
        nextBtn.setOnClickListener{ showNextPage() }
        addDotsIndicator()
        presenter.onCreate()
    }

    override fun getTourObject(): Tour {
        return intent.getSerializableExtra(TOUR) as Tour
    }

    private fun showNextPage(){
        tourPager.currentItem = currentPage + 1
    }

    private fun showPreviousPage(){
        tourPager.currentItem = currentPage - 1
    }

    private fun addDotsIndicator(){
        for(i in 1.. tour.photosArticlesList!!.size){
            val view = getDefaultView()
            dots.add(view)
            dotsLayout.addView(view)
        }
        dots[0].background = getDrawable(R.drawable.sh_circle_gray)
    }

    private fun getDefaultView(): View {
        val view = View(this)
        val params = LinearLayout.LayoutParams(18, 18, 0.0f)
        params.setMargins(6, 1, 6, 0)
        view.layoutParams = params
        view.background = getDrawable(R.drawable.sh_circle_transp_gray)
        return view
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        presenter.onPageSelected(position)
        dots[currentPage].background = getDrawable(R.drawable.sh_circle_transp_gray)
        dots[position].background = getDrawable(R.drawable.sh_circle_gray)
        currentPage = position
        setBackBtnProperties(position)
        setNextBtnProperties(position)
    }

    private fun setBackBtnProperties(position: Int){
        if(position==0) backBtn.visibility = View.INVISIBLE
        else backBtn.visibility = View.VISIBLE
    }

    private fun setNextBtnProperties(position: Int){
        if(position==dots.size-1){
            nextBtn.text = getString(R.string.end)
            nextBtn.setOnClickListener{ finish() }
        }else{
            nextBtn.text = getString(R.string.next)
            nextBtn.setOnClickListener{ showNextPage() }
        }
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        Glide.with(applicationContext).load(photoUri).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        Glide.with(applicationContext).load(photoBitmap).placeholder(articlePhoto.drawable).into(articlePhoto)
    }

    companion object {
        const val TOUR = "tour"
    }
}
