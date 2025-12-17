package com.miki.fitnesstracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.room.FitnessLogEntity
import java.text.SimpleDateFormat
import java.util.*

class DailySummaryAdapter(private val logs: MutableList<FitnessLogEntity>) :
    RecyclerView.Adapter<DailySummaryAdapter.DailySummaryViewHolder>() {

    class DailySummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvWater: TextView = itemView.findViewById(R.id.tv_water_summary)
        val tvMeal: TextView = itemView.findViewById(R.id.tv_calories_summary)
        val tvSteps: TextView = itemView.findViewById(R.id.tv_steps_summary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailySummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_summary, parent, false)
        return DailySummaryViewHolder(view)
    }

    override fun getItemCount(): Int = logs.size

    override fun onBindViewHolder(holder: DailySummaryViewHolder, position: Int) {
        val log = logs[position]

        // Format the Long date to a readable string
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateString = sdf.format(Date(log.date))

        holder.tvDate.text = dateString
        holder.tvWater.text = "Water: ${log.water} ml"
        holder.tvMeal.text = "Meal: ${log.calories} kcal"
        holder.tvSteps.text = "Steps: ${log.steps}"
    }

    // Helper function to update the list
    fun updateLogs(newLogs: List<FitnessLogEntity>) {
        logs.clear()
        logs.addAll(newLogs)
        notifyDataSetChanged()
    }
}
