package pl.renesans.renesans.utility.alert.dialog

import android.app.AlertDialog

interface AlertDialogUtility {

    fun getDownloadPhotosPermissionDialog(): AlertDialog

    fun setColorsOfButtonsOfDialog(dialog: AlertDialog)
}