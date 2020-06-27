package com.ma.io

import com.ma.dailymetrics.models.DailyMetric
import com.ma.fundamentals.models.Fundamental
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

object MongoDB {

    private val mongoClient = KMongo.createClient()

    private val database = mongoClient.getDatabase("test")

    val fundamentals = database.getCollection<Fundamental>()

    val dailyMetrics = database.getCollection<DailyMetric>()

}