package com.miki.fitnesstracker.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.adapter.DailySummaryAdapter
import com.miki.fitnesstracker.room.FitnessViewModel

class HistoryFragment : Fragment(R.layout.history_fragment) {

    private val viewModel: FitnessViewModel by viewModels()

    private lateinit var chart: BarChart
    private lateinit var avgText: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: DailySummaryAdapter

    private var filter = Filter.WEEK

    enum class Filter { DAY, WEEK, MONTH }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        chart = view.findViewById(R.id.steps_chart_view)
        avgText = view.findViewById(R.id.chart_summary_text)
        recycler = view.findViewById(R.id.daily_summary_recycler_view)

        adapter = DailySummaryAdapter(mutableListOf())
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        view.findViewById<Button>(R.id.btn_select_day).setOnClickListener { filter = Filter.DAY }
        view.findViewById<Button>(R.id.btn_select_week).setOnClickListener { filter = Filter.WEEK }
        view.findViewById<Button>(R.id.btn_select_month).setOnClickListener { filter = Filter.MONTH }

        viewModel.allLogs.observe(viewLifecycleOwner) { logs ->
            update(logs)
        }
    }

    private fun update(logs: List<com.miki.fitnesstracker.room.FitnessLogEntity>) {

        val now = System.currentTimeMillis()

        val filtered = logs.filter {
            when (filter) {
                Filter.DAY -> it.date >= now - 24 * 60 * 60 * 1000L
                Filter.WEEK -> it.date >= now - 7 * 24 * 60 * 60 * 1000L
                Filter.MONTH -> it.date >= now - 30 * 24 * 60 * 60 * 1000L
            }
        }.sortedBy { it.date }

        val entries = filtered.mapIndexed { index, log ->
            BarEntry(index.toFloat(), log.steps.toFloat())
        }

        val dataSet = BarDataSet(entries, "Steps").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
        }

        chart.data = BarData(dataSet)
        chart.description.isEnabled = false
        chart.setFitBars(true)
        chart.invalidate()

        val avg = if (filtered.isNotEmpty()) filtered.map { it.steps }.average() else 0.0
        avgText.text = "Average steps: ${avg.toInt()}"

        adapter.updateLogs(filtered)
    }
}