package pl.renesans.renesans.utility

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import androidx.core.app.ActivityCompat
import pl.renesans.renesans.R
import pl.renesans.renesans.settings.SettingsPresenterImpl

class AlertDialogUtilityImpl(val activity: Activity): AlertDialogUtility {

    override fun getDownloadPhotosPermissionDialog(): AlertDialog {
        val dialog = AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.warning))
            .setMessage(activity.getString(R.string.permission_needed))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    SettingsPresenterImpl.WRITE_EXTERNAL_STORAGE) }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        setColorsOfButtonsOfDialog(dialog)
        return dialog
    }

    override fun setColorsOfButtonsOfDialog(dialog: AlertDialog) {
        dialog.setOnShowListener{
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
        }
    }
}