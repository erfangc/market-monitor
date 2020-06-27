package com.github.erfangc.marketmonitor.io

import com.mongodb.MongoClientSettings
import org.litote.kmongo.KMongo

object MongoDB {
    private val settings = MongoClientSettings
            .builder()
            .applyToConnectionPoolSettings {
                it.maxSize(50)
                it.minSize(5)
            }
            .build()
    private val mongoClient = KMongo.createClient(settings)
    val database = mongoClient.getDatabase("test")
}