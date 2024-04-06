package com.example.fypapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fypapp.R
import com.example.fypapp.data.GraphResult
import com.example.fypapp.databinding.HistoryListItemBinding
import com.example.fypapp.ui.history.HistoryFragmentDirections

class HistoryListAdapter (private var graphResultList: List<GraphResult>) : RecyclerView.Adapter<HistoryListAdapter.ItemViewHolder>() {

    /* Represents a single item view in the RecyclerView
     * and holds the binding object for the corresponding layout
     */
    class ItemViewHolder(private val binding: HistoryListItemBinding): RecyclerView.ViewHolder(binding.root) {

        // Function to bind the GraphResult data to the views in the HistoryListItem Layout
        fun bind(graphResult: GraphResult) {
            // Bind the data from the GraphResult object to the HistoryListItem Layout
            binding.graphResult = graphResult

            // Execute any pending bindings and ensure immediate binding of the data to the views
            binding.executePendingBindings()
        }
    }

    // Function to create ItemViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Inflate the layout for HistoryListItem using DataBindingUtil
        val binding = DataBindingUtil.inflate<HistoryListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.history_list_item,
            parent,
            false
        )

        return ItemViewHolder(binding)
    }

    // Returns the number of items in the graphResults List
    override fun getItemCount(): Int = graphResultList.size

    // Function to bind data to ItemViewHolder instances
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Bind the data from the corresponding position in graphResults to the ItemViewHolder
        val currentItem = graphResultList[position]
        holder.bind(currentItem)

        // Set an onClickListener for each HistoryList Item view to navigate to its
        // detailed information page
        holder.itemView.setOnClickListener {
            //Toast.makeText(holder.itemView.context, "Pressed", Toast.LENGTH_SHORT).show()

            // Navigate to selected result item page
            val action = HistoryFragmentDirections.actionNavigationHistoryToHistoryItemFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    // Update the data list when new graphResults Items are added
    // and notify the adapter that the data has changed
    fun setGraphResults(graphResult: List<GraphResult>) {
        this.graphResultList = graphResult
        notifyDataSetChanged()
    }
}