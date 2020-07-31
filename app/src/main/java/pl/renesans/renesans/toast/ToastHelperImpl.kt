package pl.renesans.renesans.toast

import android.app.Activity
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import pl.renesans.renesans.R

class ToastHelperImpl(val activity: Activity): ToastHelper {

    override fun showToast(message: String) {
        val view = activity.layoutInflater.inflate(R.layout.toast_suggestion, activity.findViewById(R.id.toastView))
        view.findViewById<TextView>(R.id.toastText).text = message
        val toast = Toast(activity.applicationContext)
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.view = view
        toast.show()
    }
}