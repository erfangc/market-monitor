package com.github.erfangc.marketmonitor.yfinance

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import us.codecraft.xsoup.Xsoup

@Service
class YFinanceService {

    fun getEarningsEstimate(ticker: String): List<Map<String, String>> {
        val incomeStatementDocument = Jsoup.connect("https://finance.yahoo.com/quote/$ticker/analysis?p=$ticker").get()
        val table = Xsoup
                .compile("//*[@id=\"Col1-0-AnalystLeafPage-Proxy\"]/section/table[1]")
                .evaluate(incomeStatementDocument)
                .elements
                .first()
        return TableParser.parseRectangularTable(table)
    }

}