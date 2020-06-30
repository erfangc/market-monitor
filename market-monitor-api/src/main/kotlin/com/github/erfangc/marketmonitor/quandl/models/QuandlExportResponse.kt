package com.github.erfangc.marketmonitor.quandl.models

data class QuandlExportResponse(
        val datatable: DataTable,
        val meta: Meta
) {
    fun asList(): List<Map<String, String>> {
        val datatable = this.datatable
        return datatable.data.map { row ->
            datatable.columns.map { it.name }.zip(row.map { it ?: "" }).toMap()
        }
    }
}