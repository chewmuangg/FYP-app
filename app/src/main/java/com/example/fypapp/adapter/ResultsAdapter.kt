package com.example.fypapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fypapp.R
import com.example.fypapp.data.ColResult
import com.example.fypapp.databinding.ResultListItemBinding

class ResultsAdapter (private val resultsList: ArrayList<ColResult>) : RecyclerView.Adapter<ResultsAdapter.ItemViewHolder>() {

    /* Represents a single item view in the RecyclerView
     * and holds the binding object for the corresponding layout
     */
    class ItemViewHolder(internal val binding: ResultListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Function to bind the ColResult data to the views in the ResultListItem Layout
        fun bind(colResult: ColResult) {
            // Bind the data from the ColResult object to the ResultListItem Layout
            binding.colResult = colResult

            // Execute any pending bindings and ensure immediate binding of the data to the views
            binding.executePendingBindings()

            // Update visibility of the expandable layout based on isExpanded
            binding.expandableLayout.visibility = if(colResult.isExpanded) View.VISIBLE else View.GONE
        }
    }

    // Function to create ItemViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Inflate the layout for ResultListItem using DataBindingUtil
        val binding = DataBindingUtil.inflate<ResultListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.result_list_item,
            parent,
            false
        )

        // Return a new ItemViewHolder with the inflated binding
        return ItemViewHolder(binding)
    }

    // Returns the number of items in the resultsList ArrayList
    override fun getItemCount(): Int = resultsList.size

    // Function to bind data to ItemViewHolder instances
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Bind the data from the corresponding position in resutlsList to the ItemViewHolder
        val currentItem = resultsList[position]
        holder.bind(currentItem)

        // Change background colour to red if avg value above threshold value
        if ( currentItem.avgValue.toFloat() > 223 ) {
            // Get red colour from color resource
            val redColour = ContextCompat.getColor(holder.itemView.context, R.color.red)

            // Set cardLayout Background Color to red
            holder.binding.cardLayout.setBackgroundColor(redColour)
        }

        // TODO: Fix this?
        // Determine concentration values
        // Hardcoded, excluded 0.2 mM
        val concText = holder.binding.concText
        when (currentItem.colName.toInt()) {
            1 ->  {
                // Concentration: 0.1 mM
                concText.text = "Conc. : 0.1 mM"
            }
            2 -> {

                // Concetration: 0.5 mM
                concText.text = "Conc. : 0.5 mM"
            }
            3 -> {
                // Concentration: 1 mM
                concText.text = "Conc. : 1 mM"
            }
            4 -> {
                // Concentration: 5 mM
                concText.text = "Conc. : 5 mM"
            }
            5 -> {
                // Concentration: 10 mM
                concText.text = "Conc. : 10 mM"
            }
        }

        /*if ( currentItem.avgValue.toFloat() < 130 ) {
            // Concentration: 0.1 mM
            concText.text = "Conc. : 0.1 mM"

        } else if (currentItem.avgValue.toFloat() >= 130 && currentItem.avgValue.toFloat() < 160) {
            // Concentration: 0.2 mM
            concText.text = "Conc. : 0.2 mM"

        } else if (currentItem.avgValue.toFloat() >= 160 && currentItem.avgValue.toFloat() < 195) {
            // Concetration: 0.5 mM
            concText.text = "Conc. : 0.5 mM"

        } else if (currentItem.avgValue.toFloat() >= 195 && currentItem.avgValue.toFloat() < 223) {
            // Concentration: 1 mM
            concText.text = "Conc. : 1 mM"

        } else if (currentItem.avgValue.toFloat() >= 223 && currentItem.avgValue.toFloat() < 235) {
            // Concentration: 5 mM
            concText.text = "Conc. : 5 mM"

        } else if (currentItem.avgValue.toFloat() >= 235) {
            // Concentration: 10 mM
            concText.text = "Conc. : 10 mM"

        }*/

        // Set an onClickListener for each ResultListItem view to toggle/collapse additional info
        holder.itemView.setOnClickListener {
            // Toggle the expanded/collapsed state of the view
            currentItem.isExpanded = !currentItem.isExpanded
            notifyDataSetChanged()      // Notify adapter to update UI
        }
    }
}