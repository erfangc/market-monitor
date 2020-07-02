package com.github.erfangc.marketmonitor.yfinance

import org.jsoup.nodes.Element

object TableParser {
    fun parseRectangularTable(table: Element): List<Map<String, String>> {
        if (table.tagName() != "table") {
            throw RuntimeException("Element must be a table but was ${table.tagName()} instead")
        }
        val headers = table.select("thead > tr > th").map { it.text() }
        val rows = table.select("tbody > tr").map { element ->
            element
                    .select("td")
                    .mapIndexed { index, element -> headers[index] to element.text() }
                    .toMap()
        }.toList()
        return rows
    }

}
