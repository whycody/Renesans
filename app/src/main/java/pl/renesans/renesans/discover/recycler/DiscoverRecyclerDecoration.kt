package pl.renesans.renesans.discover.recycler

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class DiscoverRecyclerDecoration(val context: Context): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        val params = view.layoutParams as RecyclerView.LayoutParams
        if (parent.getChildAdapterPosition(view) == 0)
            params.marginStart = context.resources.getDimension(R.dimen.discoverMargin).toInt()
        else params.marginStart = 0
        super.getItemOffsets(outRect, view, parent, state)
    }
}