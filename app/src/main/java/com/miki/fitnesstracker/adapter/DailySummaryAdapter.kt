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
        val tvDayOfWeek: TextView = itemView.findViewById(R.id.tv_day_of_week)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvWater: TextView = itemView.findViewById(R.id.tv_water_summary)
        val tvCalories: TextView = itemView.findViewById(R.id.tv_calories_summary)
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
        val dateValue = Date(log.date)

        // 1. Format Day of Week (e.g., "Mon")
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        holder.tvDayOfWeek.text = dayFormat.format(dateValue)

        // 2. Format Date (e.g., "Nov 18")
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        holder.tvDate.text = dateFormat.format(dateValue)

        // 3. Set Metrics (Removing "Water:", "Meal:", and "Steps:")
        holder.tvWater.text = "${log.water} ml"
        holder.tvCalories.text = "${log.calories} kcal"
        holder.tvSteps.text = String.format("%, d", log.steps) // Adds commas like "10,000"
    }

    fun updateLogs(newLogs: List<FitnessLogEntity>) {
        logs.clear()
        logs.addAll(newLogs)
        notifyDataSetChanged()
    }
}