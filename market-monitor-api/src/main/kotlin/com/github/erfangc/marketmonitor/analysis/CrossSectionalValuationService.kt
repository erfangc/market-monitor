package com.github.erfangc.marketmonitor.analysis

import com.ma.dailymetrics.models.DailyMetric
import com.ma.io.MongoDB
import org.litote.kmongo.eq
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.streams.toList

data class CrossSectional(
        val date: LocalDate,
        val positiveNegativeRatio: Double,
        val pe: Double,
        val peCount: Int,
        val totalMarketCap: Double
)

@Service
class CrossSectionalValuationService {

    private val log = LoggerFactory.getLogger(CrossSectionalValuationService::class.java)

    fun crossSectionals(): List<CrossSectional> {
        val ret = LocalDate
                .of(2019, 6, 27)
                .datesUntil(LocalDate.of(2020, 6, 28))
                .filter { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY }
                .map { date ->
                    val all = MongoDB.dailyMetrics
                            .find(DailyMetric::date eq date)
                            .toList()
                    val positivePe = all.filter { (it.pe ?: 0.0) > 0 }
                    val negativePe = all.filter { (it.pe ?: 0.0) <= 0.0 }
                    val positiveMc = positivePe.sumByDouble { it.marketcap ?: 0.0 }
                    val negativeMc = negativePe.sumByDouble { it.marketcap ?: 0.0 }
                    val pe = positivePe.sumByDouble { (it.pe ?: 0.0) * ((it.marketcap ?: 0.0) / positiveMc) }
                    CrossSectional(
                            date = date,
                            totalMarketCap = positiveMc + negativeMc,
                            pe = pe,
                            peCount = positivePe.size,
                            positiveNegativeRatio = positiveMc / negativeMc
                    )
                }
        log.info("Finished computing cross sectionals")
        return ret.toList()
    }

}