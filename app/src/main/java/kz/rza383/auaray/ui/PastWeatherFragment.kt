package kz.rza383.auaray.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.PagerSnapHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import kz.rza383.auaray.R
import kz.rza383.auaray.databinding.FragmentPastWeatherBinding
@AndroidEntryPoint
class PastWeatherFragment : Fragment() {

    private val sharedViewModel: CurrentWeatherViewModel by activityViewModels()
    private var _binding: FragmentPastWeatherBinding? = null
    private lateinit var forecastChart: LineChart
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPastWeatherBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = sharedViewModel
        sharedViewModel.getForecast()
        val recyclerView = binding.forecastLst
        recyclerView.apply {
            adapter = ForecastAdapter()
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL).apply {
                setDrawable(ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.divider, null)!!)
            })
        }
        return binding.root
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forecastChart = binding.forecastChart
        forecastChart.apply {
            setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.backgroundBottom))
            description.isEnabled = false
            xAxis.apply {
                setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE)
                setTextSize(10f)
                setDrawAxisLine(false)
                setDrawGridLines(false)
                granularity = 1f

            }
            axisLeft.apply {
                isEnabled = false
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            }
            axisRight.isEnabled = false
        }
        sharedViewModel.set.observe(viewLifecycleOwner) { set ->
            set.setColors(*ColorTemplate.MATERIAL_COLORS)
            set.valueTextSize = 10f
            forecastChart.data = LineData(set)
            set.label = getString(R.string.chart_description)
            forecastChart.notifyDataSetChanged()
            forecastChart.invalidate()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}