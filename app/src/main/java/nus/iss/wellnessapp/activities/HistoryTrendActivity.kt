package nus.iss.wellnessapp.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import nus.iss.wellnessapp.R
import java.text.SimpleDateFormat
import java.util.*

import nus.iss.wellnessapp.model.ChartDataResponse
import nus.iss.wellnessapp.model.MetricEntry

import nus.iss.wellnessapp.api.WellnessApiService
import nus.iss.wellnessapp.storage.TokenManager

import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class HistoryTrendActivity : AppCompatActivity() {

    private enum class Timeframe { DAY, WEEK, MONTH, THREE_MONTHS }
    private var currentFilter = Timeframe.WEEK // Default matching reference

    private val currentPeriodStart: Calendar = Calendar.getInstance()
    private val dateDisplayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    // UI View References
    private lateinit var tvDateRange: TextView
    private lateinit var btnDay: TextView
    private lateinit var btnWeek: TextView
    private lateinit var btnMonth: TextView
    private lateinit var btn3Months: TextView

    // Chart View References
    private lateinit var chartSteps: BarChart
    private lateinit var chartDistance: BarChart
    private lateinit var chartSleep: BarChart
    private lateinit var chartWater: BarChart
    private lateinit var chartExercise: BarChart

    // Summary Value TextView References
    private lateinit var tvStepsValue: TextView
    private lateinit var tvDistanceValue: TextView
    private lateinit var tvSleepValue: TextView
    private lateinit var tvWaterValue: TextView
    private lateinit var tvExerciseValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_trend)

        initViews()
        adjustCalendarToPeriodStart()
        setupAllChartAppearances()
        setupClickListeners()
        updateDashboardView()
    }

    private fun initViews() {
        tvDateRange = findViewById(R.id.tvDateRange)
        btnDay = findViewById(R.id.btnDay)
        btnWeek = findViewById(R.id.btnWeek)
        btnMonth = findViewById(R.id.btnMonth)
        btn3Months = findViewById(R.id.btn3Months)

        chartSteps = findViewById(R.id.barChartSteps)
        chartDistance = findViewById(R.id.barChartDistance)
        chartSleep = findViewById(R.id.barChartSleep)
        chartWater = findViewById(R.id.barChartWater)
        chartExercise = findViewById(R.id.barChartExercise)

        // Match the IDs used in your XML layout cards
        tvStepsValue = findViewById(R.id.tvStepValue)
        tvDistanceValue = findViewById(R.id.tvDistanceValue)
        tvSleepValue = findViewById(R.id.tvSleepValue)
        tvWaterValue = findViewById(R.id.tvWaterValue)
        tvExerciseValue = findViewById(R.id.tvExerciseValue)

    }


    private fun setupAllChartAppearances() {

        val charts = listOf(chartSteps, chartDistance, chartSleep, chartWater, chartExercise)
        charts.forEach { chart ->
            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.setDrawGridBackground(false)
            chart.setDrawBorders(false)
            chart.setTouchEnabled(false)

            // Configure X-Axis Layout
            val xAxis: XAxis = chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.textColor = Color.parseColor("#9E9E9E")

            // --- ADD THIS LINE TO ENLARGE X-AXIS LABELS ---
            xAxis.textSize = 12f // Default is usually 10f. Adjust this value higher if needed!

            xAxis.granularity = 1f
            xAxis.valueFormatter = DynamicAxisFormatter()

            // RESTORED Y-AXIS: Make left scale visible so users can read baseline numbers
            chart.axisLeft.apply {
                isEnabled = true
                setDrawLabels(true) // Enable numeric metrics text display
                setDrawGridLines(true) // Draws horizontal grid lines for scaling
                setDrawAxisLine(false)
                textColor = Color.parseColor("#9E9E9E")

                // --- INCREASE THIS VALUE TO ENLARGE Y-AXIS LABELS ---
                textSize = 14f // Changed from 9f to 12f for better visibility

                gridColor = Color.parseColor("#E0E0E0") // Subtle background color line
                enableGridDashedLine(10f, 10f, 0f) // Soft clean dashed formatting style
                axisMinimum = 0f
            }
            // Adds extra padding at the bottom so larger X-Axis text doesn't cut off
            chart.extraBottomOffset = 5f
            chart.axisRight.isEnabled = false
        }

    }

    private fun setupClickListeners() {
        findViewById<ImageButton>(R.id.btnPrevious).setOnClickListener { shiftDateRange(forward = false) }
        findViewById<ImageButton>(R.id.btnNext).setOnClickListener { shiftDateRange(forward = true) }

        btnDay.setOnClickListener { changeTimeframeFilter(Timeframe.DAY) }
        btnWeek.setOnClickListener { changeTimeframeFilter(Timeframe.WEEK) }
        btnMonth.setOnClickListener { changeTimeframeFilter(Timeframe.MONTH) }
        btn3Months.setOnClickListener { changeTimeframeFilter(Timeframe.THREE_MONTHS) }
    }

    private fun shiftDateRange(forward: Boolean) {
        val direction = if (forward) 1 else -1
        when (currentFilter) {
            Timeframe.DAY -> currentPeriodStart.add(Calendar.DATE, direction)
            Timeframe.WEEK -> currentPeriodStart.add(Calendar.WEEK_OF_YEAR, direction)
            Timeframe.MONTH -> currentPeriodStart.add(Calendar.MONTH, direction)
            Timeframe.THREE_MONTHS -> currentPeriodStart.add(Calendar.MONTH, direction * 3)
        }
        updateDashboardView()
    }

    private fun changeTimeframeFilter(newFilter: Timeframe) {
        if (currentFilter == newFilter) return
        currentFilter = newFilter

        val toggles = mapOf(
            Timeframe.DAY to btnDay,
            Timeframe.WEEK to btnWeek,
            Timeframe.MONTH to btnMonth,
            Timeframe.THREE_MONTHS to btn3Months
        )

        toggles.forEach { (filter, view) ->
            if (filter == currentFilter) {
                view.setBackgroundResource(R.drawable.bg_toggle_active_light)
                view.setTextColor(Color.WHITE)
            } else {
                view.setBackgroundResource(R.drawable.bg_toggle_inactive_light)
                view.setTextColor(Color.parseColor("#757575"))
            }
        }

        adjustCalendarToPeriodStart()
        updateDashboardView()
    }

    private fun adjustCalendarToPeriodStart() {
        when (currentFilter) {
            Timeframe.WEEK -> currentPeriodStart.set(Calendar.DAY_OF_WEEK, currentPeriodStart.firstDayOfWeek)
            Timeframe.MONTH, Timeframe.THREE_MONTHS -> currentPeriodStart.set(Calendar.DAY_OF_MONTH, 1)
            else -> {} // For Single Day view, track today's explicit timestamp
        }
    }

    private fun updateDashboardView() {
        val startFormatted = dateDisplayFormat.format(currentPeriodStart.time)
        val endCalendar = (currentPeriodStart.clone() as Calendar)

        val dataCount = when (currentFilter) {
            Timeframe.DAY -> {
                tvDateRange.text = startFormatted
                1
            }
            Timeframe.WEEK -> {
                endCalendar.add(Calendar.DATE, 6)
                tvDateRange.text = "$startFormatted - ${dateDisplayFormat.format(endCalendar.time)}"
                7
            }
            Timeframe.MONTH -> {
                val maxDays = currentPeriodStart.getActualMaximum(Calendar.DAY_OF_MONTH)
                endCalendar.add(Calendar.DATE, maxDays - 1)
                tvDateRange.text = "$startFormatted - ${dateDisplayFormat.format(endCalendar.time)}"
                maxDays
            }
            Timeframe.THREE_MONTHS -> {
                endCalendar.add(Calendar.MONTH, 3)
                endCalendar.add(Calendar.DATE, -1)
                tvDateRange.text = "$startFormatted - ${dateDisplayFormat.format(endCalendar.time)}"
                90
            }
        }

        // Adjust X-Axis sizing bounds dynamically
        val charts = listOf(chartSteps, chartDistance, chartSleep, chartWater, chartExercise)
        charts.forEach { chart ->
            chart.xAxis.axisMinimum = -0.5f
            chart.xAxis.axisMaximum = (dataCount - 0.5).toFloat()
            chart.xAxis.labelCount = if (currentFilter == Timeframe.WEEK) 7 else 4
        }

        // Trigger your live async server execution safely
        fetchHistoricalDataFromServer()

    }

    private fun loadChartData(chart: BarChart, colorHex: String, dataPoints: List<Float>, filter: Timeframe) {
        val entries = ArrayList<BarEntry>()
        dataPoints.forEachIndexed { index, value ->
            entries.add(BarEntry(index.toFloat(), value))
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = Color.parseColor(colorHex)
            setDrawValues(false)
        }

        val barData = BarData(dataSet).apply {
            // Adjust bar thickness dynamically so it doesn't look too thick on wide views
            // Value between 0.0f and 1.0f. Lower values make the bars thinner, larger values make them thicker.
            barWidth = when (filter) {
                Timeframe.DAY -> 0.10f
                Timeframe.WEEK -> 0.45f
                else -> 0.70f
            }
        }

        chart.data = barData
        chart.invalidate()
    }

    private fun generateMockValues(count: Int, min: Float, max: Float): List<Float> {
        val random = Random()
        return List(count) { min + random.nextFloat() * (max - min) }
    }

    // Handles the dynamic text layout transformations for the bottom X-Axis line label scheme
    inner class DynamicAxisFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            if (index < 0) return ""

            return when (currentFilter) {
                Timeframe.DAY -> {
                    // Displays the active selected weekday letter label on single item day frame
                    val dayFormat = SimpleDateFormat("E", Locale.getDefault())
                    dayFormat.format(currentPeriodStart.time).substring(0, 1)
                }
                Timeframe.WEEK -> {
                    // Classic layout schema edge tags matching reference view requirements
                    if (index == 0 || index == 6) "S" else ""
                }
                Timeframe.MONTH -> {
                    // Period intervals grouping layout markers cleanly
                    if (index == 0) "1st" else if (index == 15) "15th" else ""
                }
                Timeframe.THREE_MONTHS -> {
                    // Highlights every 30-day block interval cleanly
                    if (index % 30 == 0) {
                        val tempCal = (currentPeriodStart.clone() as Calendar)
                        tempCal.add(Calendar.DATE, index)
                        SimpleDateFormat("MMM", Locale.getDefault()).format(tempCal.time)
                    } else ""
                }
            }
        }
    }


    private fun mapApiDataToTimeframeSlots(
        apiEntries: List<nus.iss.wellnessapp.model.MetricEntry>?,
        totalSlots: Int
    ): List<Float> {
        val slots = FloatArray(totalSlots) { 0f }
        if (apiEntries == null) return slots.toList()

        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val baseCal = (currentPeriodStart.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetCal = Calendar.getInstance()

        for (entry in apiEntries) {
            try {
                val entryDate = apiDateFormat.parse(entry.date) ?: continue
                targetCal.time = entryDate

                val diffMillis = targetCal.timeInMillis - baseCal.timeInMillis
                val dayIndex = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

                if (dayIndex in 0 until totalSlots) {
                    slots[dayIndex] = entry.value
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return slots.toList()
    }

/*  // For debugging and Testing Purpose
    private fun getSampleJsonResponse(): String {
        return """
    {
        "chartData": {
            "sleep": [
                {"date": "2026-06-29", "value": 7.5},
                {"date": "2026-06-30", "value": 6.0},
                {"date": "2026-07-01", "value": 8.0},
                {"date": "2026-07-02", "value": 5.5},
                {"date": "2026-07-03", "value": 7.0}
            ],
            "distance": [
                {"date": "2026-06-29", "value": 11.68},
                {"date": "2026-06-30", "value": 4.72},
                {"date": "2026-07-01", "value": 7.85},
                {"date": "2026-07-02", "value": 6.93},
                {"date": "2026-07-03", "value": 11.53}
            ],
            "exercise": [
                {"date": "2026-06-29", "value": 35.0},
                {"date": "2026-07-01", "value": 45.0},
                {"date": "2026-07-03", "value": 25.0}
            ],
            "steps": [
                {"date": "2026-06-29", "value": 8500.0},
                {"date": "2026-06-30", "value": 6200.0},
                {"date": "2026-07-01", "value": 10300.0},
                {"date": "2026-07-02", "value": 9100.0},
                {"date": "2026-07-03", "value": 11200.0}
            ],
            "water": [
                {"date": "2026-06-29", "value": 2.2},
                {"date": "2026-06-30", "value": 1.8},
                {"date": "2026-07-01", "value": 2.5},
                {"date": "2026-07-02", "value": 2.0},
                {"date": "2026-07-03", "value": 2.3}
            ]
        }
    }
    """.trimIndent()
    } */

    private fun fetchHistoricalDataFromServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(WellnessApiService::class.java)

        val requestDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dynamicStartDateString = requestDateFormat.format(currentPeriodStart.time)

        // Convert active enum filter (DAY, WEEK, MONTH, THREE_MONTHS) to a clean string format
        val timeframeString = currentFilter.name

        val currentUserId = TokenManager.getUserId().toInt()

        // Ensure the anonymous object block opens here with a curly brace '{'
        apiService.getHistoryTrends(userId = currentUserId, startDate = dynamicStartDateString, timeframe = timeframeString)
            .enqueue(object : Callback<ChartDataResponse> {

                override fun onResponse(call: Call<ChartDataResponse>, response: Response<ChartDataResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val container = response.body()!!.chartData
                        val dataCount = calculateDataCountForCurrentFilter()

                        // Process the raw backend array elements into sequential day slots
                        val stepsData = mapApiDataToTimeframeSlots(container.steps, dataCount)
                        val distanceData = mapApiDataToTimeframeSlots(container.distance, dataCount)
                        val sleepData = mapApiDataToTimeframeSlots(container.sleep, dataCount)
                        val waterData = mapApiDataToTimeframeSlots(container.water, dataCount)
                        val exerciseData = mapApiDataToTimeframeSlots(container.exercise, dataCount)

                        // --- AGGREGATE SUMMARY FIGURES ---
                        updateSummaryMetrics(stepsData, distanceData, sleepData, waterData, exerciseData)

                        // Clear out the warnings by passing those parsed data slots into the UI charts!
                        loadChartData(chartSteps, "#F05A28", stepsData, currentFilter)
                        loadChartData(chartDistance, "#1A73E8", distanceData, currentFilter)
                        loadChartData(chartSleep, "#9C27B0", sleepData, currentFilter)
                        loadChartData(chartWater, "#00B0FF", waterData, currentFilter)
                        loadChartData(chartExercise, "#4CAF50", exerciseData, currentFilter)
                    }
                }

                override fun onFailure(call: Call<ChartDataResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            }) // Closes the enqueue callback block cleanly
    }

    // Helper extraction function to isolate timeframe mathematical calculation
    private fun calculateDataCountForCurrentFilter(): Int {
        return when (currentFilter) {
            Timeframe.DAY -> 1
            Timeframe.WEEK -> 7
            Timeframe.MONTH -> currentPeriodStart.getActualMaximum(Calendar.DAY_OF_MONTH)
            Timeframe.THREE_MONTHS -> 90
        }
    }


    private fun updateSummaryMetrics(
        steps: List<Float>,
        distance: List<Float>,
        sleep: List<Float>,
        water: List<Float>,
        exercise: List<Float>
    ) {
        // 1. Sum up absolute totals
        val totalSteps = steps.sum().toInt()
        val totalDistance = distance.sum()
        val totalExercise = exercise.sum().toInt()

        // 2. Compute averages for standard daily tracking metrics (only dividing by days that have values to prevent flattening)
        val activeSleepDays = sleep.filter { it > 0f }
        val avgSleep = if (activeSleepDays.isNotEmpty()) activeSleepDays.average().toFloat() else 0f

        val activeWaterDays = water.filter { it > 0f }
        val avgWater = if (activeWaterDays.isNotEmpty()) activeWaterDays.average().toFloat() else 0f

        // 3. Bind formatted values safely to the text views
        tvStepsValue.text = String.format(Locale.getDefault(), "%,d", totalSteps) // Formats with commas like 45,300
        tvDistanceValue.text = String.format(Locale.getDefault(), "%.2f", totalDistance) // Formats to 2 decimal points like 42.71
        tvExerciseValue.text = totalExercise.toString()

        tvSleepValue.text = String.format(Locale.getDefault(), "%.1f", avgSleep) // e.g., 6.8
        tvWaterValue.text = String.format(Locale.getDefault(), "%.2f", avgWater) // e.g., 2.16
    }

}