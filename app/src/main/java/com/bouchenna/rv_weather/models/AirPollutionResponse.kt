package com.bouchenna.rv_weather.models


data class AirPollutionResponse(
    val coord: Coord?,
    val list: List<PollutionData>?
) {
    data class Coord(
        val lat: Double?,
        val lon: Double?
    )

    data class PollutionData(
        val dt: Long?,
        val main: PollutionMain?,
        val components: PollutionComponents?
    )

    data class PollutionMain(
        val aqi: Int?
    )

    data class PollutionComponents(
        val co: Double?,
        val no: Double?,
        val no2: Double?,
        val o3: Double?,
        val so2: Double?,
        val pm2_5: Double?,
        val pm10: Double?,
        val nh3: Double?
    )
}
