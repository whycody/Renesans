package pl.renesans.renesans.utility

import android.app.AlertDialog

interface AlertDialogUtility {

    fun getDownloadPhotosPermissionDialog(): AlertDialog

    fun setColorsOfButtonsOfDialog(dialog: AlertDialog)
}