package pl.renesans.renesans.map

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pl.renesans.renesans.R
import pl.renesans.renesans.data.PhotoArticle

class PhotoBottomSheetDialog(val photoArticle: PhotoArticle? = null): BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_bottom_sheet_photo, container, false)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        view?.findViewById<TextView>(R.id.articleTitle)?.text = photoArticle?.title
        view?.findViewById<TextView>(R.id.articleParagraph)?.text = photoArticle?.paragraph?.content
        view?.findViewById<TextView>(R.id.photoDescription)?.text = photoArticle?.photo?.describe
        view?.findViewById<ImageView>(R.id.articlePhoto)?.setBackgroundColor(Color.LTGRAY)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) setWhiteNavigationBar(dialog)
        return dialog
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setWhiteNavigationBar(dialog: Dialog) {
        val window: Window = dialog.window!!
        val metrics = DisplayMetrics()
        window.windowManager.defaultDisplay.getMetrics(metrics)
        val dimDrawable = GradientDrawable()
        val navigationBarDrawable = GradientDrawable()
        navigationBarDrawable.shape = GradientDrawable.RECTANGLE
        navigationBarDrawable.setColor(ContextCompat.getColor(activity!!, R.color.colorGray))
        val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
        val windowBackground = LayerDrawable(layers)
        windowBackground.setLayerInsetTop(1, metrics.heightPixels)
        window.setBackgroundDrawable(windowBackground)
    }
}