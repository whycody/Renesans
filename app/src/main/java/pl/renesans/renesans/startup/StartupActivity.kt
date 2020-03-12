package pl.renesans.renesans.startup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_startup.*
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R

class StartupActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    private var dots = mutableListOf<View>()
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
        changeStatusBarColor()
        val startupAdapter = StartupAdapter(this)
        startupPager.adapter = startupAdapter
        startupPager.addOnPageChangeListener(this)
        backBtn.setOnClickListener{ showPreviousPage() }
        nextBtn.setOnClickListener{ showNextPage() }
        addDotsIndicator()
    }

    private fun showNextPage(){
        startupPager.currentItem = currentPage + 1
    }

    private fun showPreviousPage(){
        startupPager.currentItem = currentPage - 1
    }

    private fun changeStatusBarColor(){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
    }

    private fun addDotsIndicator(){
        for(i in 0.. 2){
            val view = getDefaultView()
            dots.add(view)
            dotsLayout.addView(view)
        }
        dots[0].background = getDrawable(R.drawable.sh_circle_primary)
    }

    private fun getDefaultView(): View {
        val view = View(this)
        val params = LinearLayout.LayoutParams(18, 18, 0.0f)
        params.setMargins(6, 1, 6, 0)
        view.layoutParams = params
        view.background = getDrawable(R.drawable.sh_circle_transp_primary)
        return view
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        dots[currentPage].background = getDrawable(R.drawable.sh_circle_transp_primary)
        dots[position].background = getDrawable(R.drawable.sh_circle_primary)
        currentPage = position
        setBackBtnProperties(position)
        setNextBtnProperties(position)
    }

    private fun setBackBtnProperties(position: Int){
        if(position==0) backBtn.visibility = View.GONE
        else backBtn.visibility = View.VISIBLE
    }

    private fun setNextBtnProperties(position: Int){
        if(position==dots.size-1){
            nextBtn.text = getString(R.string.end)
            nextBtn.setOnClickListener{
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }else{
            nextBtn.text = getString(R.string.next)
            nextBtn.setOnClickListener{ showNextPage() }
        }
    }
}
