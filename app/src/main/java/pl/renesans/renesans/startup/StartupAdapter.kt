package pl.renesans.renesans.startup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import pl.renesans.renesans.R

class StartupAdapter(private val context: Context): PagerAdapter() {

    private val images = listOf(context.getDrawable(R.drawable.img_cracow_castle))

    private val titles = listOf("Poznaj Leonarda da Vinci z innej strony", "Odkrywaj bogactwo sztuki",
        "Przenieś się do czasów Mikołaja Kopernika")

    private val descriptions = listOf("Czy wiedziałeś, że Leonardo był leworęczny? Ale to nie wszystko." +
            " Oprócz tego pisał od prawej do lewej strony.","Słyszysz sztuka - myślisz Mona Lisa? Z nami " +
            "znacznie rozszerzysz tą definicję. Poznasz literaturę, kulturę i muzykę minionych wieków."
            ,"Osobiście zobacz krakowski zamek oczami Kopernika, poczuj smak toruńskich pierników," +
                " czy wybierz się do Starego Rynku w Olsztynie.")

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as ConstraintLayout
    }

    override fun getCount(): Int = titles.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.startup_slide_layout, container, false)
        view.findViewById<ImageView>(R.id.startupImage).setImageDrawable(images[0])
        view.findViewById<TextView>(R.id.settingTitle).text = titles[position]
        view.findViewById<TextView>(R.id.descriptionView).text = descriptions[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}