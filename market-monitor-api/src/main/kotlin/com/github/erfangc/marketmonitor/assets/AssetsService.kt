package com.github.erfangc.marketmonitor.assets

import com.github.erfangc.marketmonitor.assets.models.Asset
import com.github.erfangc.marketmonitor.assets.models.AssetRow
import com.github.erfangc.marketmonitor.io.MongoDB
import com.github.erfangc.marketmonitor.quandl.QuandlService.exportQuandl
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import com.vhl.blackmo.grass.dsl.grass
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@ExperimentalStdlibApi
@Service
class AssetsService {

    companion object {
        val assetsCollection = MongoDB.database.getCollection<AssetRow>()
    }

    private val log = LoggerFactory.getLogger(AssetsService::class.java)

    fun bootstrap() {
        log.info("Bootstrapping assets data from Quandl and saving the results to MongoDB")
        assetsCollection.drop()
        assetsCollection.createIndex(Indexes.hashed((AssetRow::asset / Asset::ticker).name))
        assetsCollection.createIndex(Indexes.text((AssetRow::asset / Asset::ticker).name))
        exportQuandl(publisher = "SHARADAR", table = "TICKERS") { response ->
            val rows = grass<Asset>()
                    .harvest(response.asList())
                    .filter { it.table == "SF1" }
                    .map {
                        AssetRow(_id = "${it.table}:${it.permaticker}:${it.ticker}", asset = it)
                    }
            if (rows.isNotEmpty()) {
                assetsCollection.insertMany(rows)
                log.info("Inserted ${rows.size} rows into the ${Asset::class.simpleName} collection")
            }
        }
    }

    @Cacheable("getTickers")
    fun getTicker(ticker: String): Asset? {
        return assetsCollection.findOne(AssetRow::asset / Asset::ticker eq ticker).asset
    }

    @Cacheable("matchTicker")
    fun matchTicker(term: String): List<Asset> {
        return assetsCollection.find(Filters.text(term)).map { it.asset }.toList()
    }

}