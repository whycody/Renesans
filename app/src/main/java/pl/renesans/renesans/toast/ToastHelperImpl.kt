package pl.renesans.renesans.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.android.synthetic.main.toast_suggestion.view.*
import pl.renesans.renesans.R

class ToastHelperImpl(private val context: Context): ToastHelper {

    override fun showToast(message: String) {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.toast_suggestion, null)
        view.toastText.text = message
        with(Toast(context)) {
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            this.view = view
            show()
        }
    }
}