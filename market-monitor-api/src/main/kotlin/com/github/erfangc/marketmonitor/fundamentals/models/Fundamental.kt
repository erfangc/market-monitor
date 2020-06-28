package com.github.erfangc.marketmonitor.fundamentals.models

import java.time.LocalDate

data class FundamentalRow(
        val _id: String,
        val fundamental: Fundamental
)

data class Fundamental(
        // com.github.erfangc.marketmonitor.tickers.Ticker Symbol
        val ticker: String,
        // Dimension
        val dimension: String,
        // Calendar Date
        val calendardate: LocalDate,
        // Date Key
        val datekey: LocalDate,
        // Report Period
        val reportperiod: LocalDate,
        // Last Updated Date
        val lastupdated: LocalDate,
        // Accumulated Other Comprehensive Income
        val accoci: String? = null,
        // Total Assets
        val assets: String? = null,
        // Average Assets
        val assetsavg: String? = null,
        // Current Assets
        val assetsc: String? = null,
        // Assets Non-Current
        val assetsnc: String? = null,
        // Asset Turnover
        val assetturnover: Double? = null,
        // Book Value per Share
        val bvps: Double? = null,
        // Capital Expenditure
        val capex: String? = null,
        // Cash and Equivalents
        val cashneq: String? = null,
        // Cash and Equivalents (USD)
        val cashnequsd: Double? = null,
        // Cost of Revenue
        val cor: String? = null,
        // Consolidated Income
        val consolinc: String? = null,
        // Current Ratio
        val currentratio: Double? = null,
        // Debt to Equity Ratio
        val de: Double? = null,
        // Total Debt
        val debt: String? = null,
        // Debt Current
        val debtc: String? = null,
        // Debt Non-Current
        val debtnc: String? = null,
        // Total Debt (USD)
        val debtusd: Double? = null,
        // Deferred Revenue
        val deferredrev: String? = null,
        // Depreciation Amortization & Accretion
        val depamor: String? = null,
        // Deposit Liabilities
        val deposits: String? = null,
        // Dividend Yield
        val divyield: Double? = null,
        // Dividends per Basic Common Share
        val dps: Double? = null,
        // Earning Before Interest & Taxes (EBIT)
        val ebit: String? = null,
        // Earnings Before Interest Taxes & Depreciation Amortization (EBITDA)
        val ebitda: String? = null,
        // EBITDA Margin
        val ebitdamargin: Double? = null,
        // Earnings Before Interest Taxes & Depreciation Amortization (USD)
        val ebitdausd: Double? = null,
        // Earning Before Interest & Taxes (USD)
        val ebitusd: Double? = null,
        // Earnings before Tax
        val ebt: String? = null,
        // Earnings per Basic Share
        val eps: Double? = null,
        // Earnings per Diluted Share
        val epsdil: Double? = null,
        // Earnings per Basic Share (USD)
        val epsusd: Double? = null,
        // Shareholders Equity
        val equity: String? = null,
        // Average Equity
        val equityavg: String? = null,
        // Shareholders Equity (USD)
        val equityusd: Double? = null,
        // Enterprise Value
        val ev: Double? = null,
        // Enterprise Value over EBIT
        val evebit: Double? = null,
        // Enterprise Value over EBITDA
        val evebitda: Double? = null,
        // Free Cash Flow
        val fcf: String? = null,
        // Free Cash Flow per Share
        val fcfps: Double? = null,
        // Foreign Currency to USD Exchange Rate
        val fxusd: Double? = null,
        // Gross Profit
        val gp: String? = null,
        // Gross Margin
        val grossmargin: Double? = null,
        // Goodwill and Intangible Assets
        val intangibles: String? = null,
        // Interest Expense
        val intexp: String? = null,
        // Invested Capital
        val invcap: String? = null,
        // Invested Capital Average
        val invcapavg: String? = null,
        // Inventory
        val inventory: String? = null,
        // Investments
        val investments: String? = null,
        // Investments Current
        val investmentsc: String? = null,
        // Investments Non-Current
        val investmentsnc: String? = null,
        // Total Liabilities
        val liabilities: String? = null,
        // Current Liabilities
        val liabilitiesc: String? = null,
        // Liabilities Non-Current
        val liabilitiesnc: String? = null,
        // Market Capitalization
        val marketcap: Double? = null,
        // Net Cash Flow / Change in Cash & Cash Equivalents
        val ncf: String? = null,
        // Net Cash Flow - Business Acquisitions and Disposals
        val ncfbus: String? = null,
        // Issuance (Purchase) of Equity Shares
        val ncfcommon: String? = null,
        // Issuance (Repayment) of Debt Securities
        val ncfdebt: String? = null,
        // Payment of Dividends & Other Cash Distributions
        val ncfdiv: String? = null,
        // Net Cash Flow from Financing
        val ncff: String? = null,
        // Net Cash Flow from Investing
        val ncfi: String? = null,
        // Net Cash Flow - Investment Acquisitions and Disposals
        val ncfinv: String? = null,
        // Net Cash Flow from Operations
        val ncfo: String? = null,
        // Effect of Exchange Rate Changes on Cash
        val ncfx: String? = null,
        // Net Income
        val netinc: String? = null,
        // Net Income Common Stock
        val netinccmn: String? = null,
        // Net Income Common Stock (USD)
        val netinccmnusd: Double? = null,
        // Net Loss Income from Discontinued Operations
        val netincdis: String? = null,
        // Net Income to Non-Controlling Interests
        val netincnci: String? = null,
        // Profit Margin
        val netmargin: Double? = null,
        // Operating Expenses
        val opex: String? = null,
        // Operating Income
        val opinc: String? = null,
        // Trade and Non-Trade Payables
        val payables: String? = null,
        // Payout Ratio
        val payoutratio: Double? = null,
        // Price to Book Value
        val pb: Double? = null,
        // Price Earnings (Damodaran Method)
        val pe: Double? = null,
        // Price to Earnings Ratio
        val pe1: Double? = null,
        // Property Plant & Equipment Net
        val ppnenet: String? = null,
        // Preferred Dividends Income Statement Impact
        val prefdivis: String? = null,
        // Share Price (Adjusted Close)
        val price: Double? = null,
        // Price Sales (Damodaran Method)
        val ps: Double? = null,
        // Price to Sales Ratio
        val ps1: Double? = null,
        // Trade and Non-Trade Receivables
        val receivables: String? = null,
        // Accumulated Retained Earnings (Deficit)
        val retearn: String? = null,
        // Revenues
        val revenue: Double? = null,
        // Revenues (USD)
        val revenueusd: Double? = null,
        // Research and Development Expense
        val rnd: String? = null,
        // Return on Average Assets
        val roa: Double? = null,
        // Return on Average Equity
        val roe: Double? = null,
        // Return on Invested Capital
        val roic: Double? = null,
        // Return on Sales
        val ros: Double? = null,
        // Share Based Compensation
        val sbcomp: String? = null,
        // Selling General and Administrative Expense
        val sgna: String? = null,
        // Share Factor
        val sharefactor: Double? = null,
        // Shares (Basic)
        val sharesbas: Double? = null,
        // Weighted Average Shares
        val shareswa: Double? = null,
        // Weighted Average Shares Diluted
        val shareswadil: Double? = null,
        // Sales per Share
        val sps: Double? = null,
        // Tangible Asset Value
        val tangibles: String? = null,
        // Tax Assets
        val taxassets: String? = null,
        // Income Tax Expense
        val taxexp: String? = null,
        // Tax Liabilities
        val taxliabilities: String? = null,
        // Tangible Assets Book Value per Share
        val tbvps: Double? = null,
        // Working Capital
        val workingcapital: String? = null
)