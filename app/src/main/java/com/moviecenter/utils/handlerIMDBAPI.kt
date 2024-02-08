package com.app.moviecenter.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
suspend fun getMovieId(movieName: String): String {
    try {
        val encodedName = java.net.URLEncoder.encode(movieName, "utf-8")
        val apiUrl = "https://search.imdbot.workers.dev/?q=$encodedName"

        val url: URL = URI.create(apiUrl).toURL()

        val res = withContext(Dispatchers.IO) {
            (url.openConnection() as HttpURLConnection).inputStream.bufferedReader().readText()
        }

        val movieJSONObj = JSONObject(res).getJSONArray("description").getJSONObject(0)

        return movieJSONObj.getString("#IMDB_ID")
    } catch (e: Exception) {
        return ""
    }
}

suspend fun getMovieRank(movieId: String): Double {
    try {
        val apiUrl = "https://search.imdbot.workers.dev/?tt=$movieId"

        val url: URL = URI.create(apiUrl).toURL()
        val res = withContext(Dispatchers.IO) {
            (url.openConnection() as HttpURLConnection).inputStream.bufferedReader().readText()
        }

        return JSONObject(res).getJSONObject("short")
            .getJSONObject("aggregateRating").getDouble("ratingValue")
    } catch (e: Exception) {
        return 0.0
    }
}


