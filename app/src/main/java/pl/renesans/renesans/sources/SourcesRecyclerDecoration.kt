package pl.renesans.renesans.sources

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class SourcesRecyclerDecoration(val context: Context, private var itemCount: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        val params = view.layoutParams as RecyclerView.LayoutParams
        if (parent.getChildAdapterPosition(view) == itemCount - 1)
            params.bottomMargin = context.resources.getDimension(R.dimen.sourcesItemRowMargin).toInt()
        else params.bottomMargin = 0
        super.getItemOffsets(outRect, view, parent, state)
    }

    fun setItemCount(itemCount: Int){
        this.itemCount = itemCount
    }
}