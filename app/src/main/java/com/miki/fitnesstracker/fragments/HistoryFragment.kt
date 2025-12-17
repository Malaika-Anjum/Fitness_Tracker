package com.miki.fitnesstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.adapter.DailySummaryAdapter
import com.miki.fitnesstracker.room.FitnessLogEntity
import com.miki.fitnesstracker.room.FitnessViewModel
import java.util.Calendar


class HistoryFragment : Fragment() {

    private val viewModel: FitnessViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailySummaryAdapter

    private lateinit var btnWeek: Button
    private lateinit var btnMonth: Button
    private lateinit var btnYear: Button

    private var currentFilter = FilterPeriod.WEEK

    enum class FilterPeriod { WEEK, MONTH, YEAR }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.history_fragment, container, false)

        recyclerView = view.findViewById(R.id.daily_summary_recycler_view)
        adapter = DailySummaryAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnWeek = view.findViewById(R.id.btn_select_week)
        btnMonth = view.findViewById(R.id.btn_select_month)
        btnYear = view.findViewById(R.id.btn_select_year)

        btnWeek.setOnClickListener { changeFilter(FilterPeriod.WEEK) }
        btnMonth.setOnClickListener { changeFilter(FilterPeriod.MONTH) }
        btnYear.setOnClickListener { changeFilter(FilterPeriod.YEAR) }

        observeLogs()

        return view
    }

    private fun changeFilter(filter: FilterPeriod) {
        currentFilter = filter
        adapter.updateLogs(filterLogs(viewModel.allLogs.value ?: emptyList(), currentFilter))
    }

    private fun observeLogs() {
        viewModel.allLogs.observe(viewLifecycleOwner) { logs ->
            val filteredLogs = filterLogs(logs, currentFilter)
            adapter.updateLogs(filteredLogs)
        }
    }

    private fun filterLogs(logs: List<FitnessLogEntity>, period: FilterPeriod): List<FitnessLogEntity> {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis

        return logs.filter { log ->
            when (period) {
                FilterPeriod.WEEK -> log.date >= today - 7 * 24 * 60 * 60 * 1000L
                FilterPeriod.MONTH -> log.date >= today - 30 * 24 * 60 * 60 * 1000L
                FilterPeriod.YEAR -> log.date >= today - 365 * 24 * 60 * 60 * 1000L
            }
        }.sortedByDescending { it.date }
    }
}
