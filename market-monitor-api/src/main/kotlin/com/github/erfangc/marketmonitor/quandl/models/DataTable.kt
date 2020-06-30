package com.github.erfangc.marketmonitor.quandl.models

data class DataTable(
        val data: Array<Array<String?>>,
        val columns: List<Column>
)