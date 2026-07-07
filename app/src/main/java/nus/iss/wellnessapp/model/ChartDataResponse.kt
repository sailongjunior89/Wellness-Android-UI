package nus.iss.wellnessapp.model

data class ChartDataResponse(val chartData: MetricsContainer)
data class MetricsContainer(
    val steps: List<MetricEntry>?,
    val distance: List<MetricEntry>?,
    val sleep: List<MetricEntry>?,
    val water: List<MetricEntry>?,
    val exercise: List<MetricEntry>?
)
data class MetricEntry(val date: String, val value: Float)