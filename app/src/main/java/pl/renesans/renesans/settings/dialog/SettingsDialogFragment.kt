package pl.renesans.renesans.settings.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R
import pl.renesans.renesans.data.SettingListItem

class SettingsDialogFragment(val view: SettingsListContract.SettingsListView): DialogFragment(),
    SettingsListContract.SettingsListView {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val recyclerView = RecyclerView(activity!!.applicationContext)
        val presenter: SettingsListContract.SettingsListPresenter = SettingsListPresenterImpl(this)
        presenter.onCreate()
        val adapter = SettingsListRecyclerAdapter(activity!!.applicationContext, presenter)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        builder.setView(recyclerView)
        builder.setNegativeButton(getString(R.string.cancel), null)
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        (dialog as AlertDialog?)?.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.GRAY)
    }

    override fun getSettingItemsList(): List<SettingListItem> {
        return view.getSettingItemsList()
    }

    override fun getDefaultSettingsItemPos(): Int {
        return view.getDefaultSettingsItemPos()
    }

    override fun radioBtnChoosed(selectedSettingPos: Int) {
        dialog?.dismiss()
        view.radioBtnChoosed(selectedSettingPos)
    }
}