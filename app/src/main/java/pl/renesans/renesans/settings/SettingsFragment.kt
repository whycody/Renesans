package pl.renesans.renesans.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.MainActivity

import pl.renesans.renesans.R

class SettingsFragment : Fragment(), SettingsContract.SettingsView {

    private lateinit var adapter: SettingsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val presenter = SettingsPresenterImpl(activity as MainActivity, this)
        adapter = SettingsAdapter(context!!.applicationContext, presenter)
        val recyclerView = view.findViewById<RecyclerView>(R.id.settingsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(context!!.applicationContext)
        recyclerView.adapter = adapter
        return view
    }

    override fun refreshMapFragment() {
        (activity as MainActivity).refreshMapFragment()
    }

    override fun changedOptionOfMapLimit() {
        (activity as MainActivity).changedOptionOfMapLimit()
    }

    override fun notifyItemChangedAtPosition(pos: Int) {
        adapter.notifyItemChanged(pos)
    }

}
