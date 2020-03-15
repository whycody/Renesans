package pl.renesans.renesans.map.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class LocationAdapter(val presenter: LocationPresenter, val context: Context): RecyclerView.Adapter<LocationRowHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationRowHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_location_row, parent, false)
        return LocationRowHolder(view, presenter)
    }

    override fun getItemCount(): Int {
        return presenter.getItemCount()
    }

    override fun onBindViewHolder(holder: LocationRowHolder, position: Int) {
        presenter.onBindViewHolder(holder, position)
    }
}