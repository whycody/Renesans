package pl.renesans.renesans.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import pl.renesans.renesans.R

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val presenter = SettingsPresenterImpl(context!!.applicationContext)
        val adapter = SettingsAdapter(context!!.applicationContext, presenter)
        val recyclerView = view.findViewById<RecyclerView>(R.id.settingsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(context!!.applicationContext)
        recyclerView.adapter = adapter
        return view
    }

}
