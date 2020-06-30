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
        val calendardate: LocalDate? = null,
        // Date Key
        val datekey: LocalDate,
        // Report Period
        val reportperiod: LocalDate,
        // Last Updated Date
        val lastupdated: LocalDate? = null,
        // Accumulated Other Comprehensive Income
        val accoci: Double? = null,
        // Total Assets
        val assets: Double? = null,
        // Average Assets
        val assetsavg: Double? = null,
        // Current Assets
        val assetsc: Double? = null,
        // Assets Non-Current
        val assetsnc: Double? = null,
        // Asset Turnover
        val assetturnover: Double? = null,
        // Book Value per Share
        val bvps: Double? = null,
        // Capital Expenditure
        val capex: Double? = null,
        // Cash and Equivalents
        val cashneq: Double? = null,
        // Cash and Equivalents (USD)
        val cashnequsd: Double? = null,
        // Cost of Revenue
        val cor: Double? = null,
        // Consolidated Income
        val consolinc: Double? = null,
        // Current Ratio
        val currentratio: Double? = null,
        // Debt to Equity Ratio
        val de: Double? = null,
        // Total Debt
        val debt: Double? = null,
        // Debt Current
        val debtc: Double? = null,
        // Debt Non-Current
        val debtnc: Double? = null,
        // Total Debt (USD)
        val debtusd: Double? = null,
        // Deferred Revenue
        val deferredrev: Double? = null,
        // Depreciation Amortization & Accretion
        val depamor: Double? = null,
        // Deposit Liabilities
        val deposits: Double? = null,
        // Dividend Yield
        val divyield: Double? = null,
        // Dividends per Basic Common Share
        val dps: Double? = null,
        // Earning Before Interest & Taxes (EBIT)
        val ebit: Double? = null,
        // Earnings Before Interest Taxes & Depreciation Amortization (EBITDA)
        val ebitda: Double? = null,
        // EBITDA Margin
        val ebitdamargin: Double? = null,
        // Earnings Before Interest Taxes & Depreciation Amortization (USD)
        val ebitdausd: Double? = null,
        // Earning Before Interest & Taxes (USD)
        val ebitusd: Double? = null,
        // Earnings before Tax
        val ebt: Double? = null,
        // Earnings per Basic Share
        val eps: Double? = null,
        // Earnings per Diluted Share
        val epsdil: Double? = null,
        // Earnings per Basic Share (USD)
        val epsusd: Double? = null,
        // Shareholders Equity
        val equity: Double? = null,
        // Average Equity
        val equityavg: Double? = null,
        // Shareholders Equity (USD)
        val equityusd: Double? = null,
        // Enterprise Value
        val ev: Double? = null,
        // Enterprise Value over EBIT
        val evebit: Double? = null,
        // Enterprise Value over EBITDA
        val evebitda: Double? = null,
        // Free Cash Flow
        val fcf: Double? = null,
        // Free Cash Flow per Share
        val fcfps: Double? = null,
        // Foreign Currency to USD Exchange Rate
        val fxusd: Double? = null,
        // Gross Profit
        val gp: Double? = null,
        // Gross Margin
        val grossmargin: Double? = null,
        // Goodwill and Intangible Assets
        val intangibles: Double? = null,
        // Interest Expense
        val intexp: Double? = null,
        // Invested Capital
        val invcap: Double? = null,
        // Invested Capital Average
        val invcapavg: Double? = null,
        // Inventory
        val inventory: Double? = null,
        // Investments
        val investments: Double? = null,
        // Investments Current
        val investmentsc: Double? = null,
        // Investments Non-Current
        val investmentsnc: Double? = null,
        // Total Liabilities
        val liabilities: Double? = null,
        // Current Liabilities
        val liabilitiesc: Double? = null,
        // Liabilities Non-Current
        val liabilitiesnc: Double? = null,
        // Market Capitalization
        val marketcap: Double? = null,
        // Net Cash Flow / Change in Cash & Cash Equivalents
        val ncf: Double? = null,
        // Net Cash Flow - Business Acquisitions and Disposals
        val ncfbus: Double? = null,
        // Issuance (Purchase) of Equity Shares
        val ncfcommon: Double? = null,
        // Issuance (Repayment) of Debt Securities
        val ncfdebt: Double? = null,
        // Payment of Dividends & Other Cash Distributions
        val ncfdiv: Double? = null,
        // Net Cash Flow from Financing
        val ncff: Double? = null,
        // Net Cash Flow from Investing
        val ncfi: Double? = null,
        // Net Cash Flow - Investment Acquisitions and Disposals
        val ncfinv: Double? = null,
        // Net Cash Flow from Operations
        val ncfo: Double? = null,
        // Effect of Exchange Rate Changes on Cash
        val ncfx: Double? = null,
        // Net Income
        val netinc: Double? = null,
        // Net Income Common Stock
        val netinccmn: Double? = null,
        // Net Income Common Stock (USD)
        val netinccmnusd: Double? = null,
        // Net Loss Income from Discontinued Operations
        val netincdis: Double? = null,
        // Net Income to Non-Controlling Interests
        val netincnci: Double? = null,
        // Profit Margin
        val netmargin: Double? = null,
        // Operating Expenses
        val opex: Double? = null,
        // Operating Income
        val opinc: Double? = null,
        // Trade and Non-Trade Payables
        val payables: Double? = null,
        // Payout Ratio
        val payoutratio: Double? = null,
        // Price to Book Value
        val pb: Double? = null,
        // Price Earnings (Damodaran Method)
        val pe: Double? = null,
        // Price to Earnings Ratio
        val pe1: Double? = null,
        // Property Plant & Equipment Net
        val ppnenet: Double? = null,
        // Preferred Dividends Income Statement Impact
        val prefdivis: Double? = null,
        // Share Price (Adjusted Close)
        val price: Double? = null,
        // Price Sales (Damodaran Method)
        val ps: Double? = null,
        // Price to Sales Ratio
        val ps1: Double? = null,
        // Trade and Non-Trade Receivables
        val receivables: Double? = null,
        // Accumulated Retained Earnings (Deficit)
        val retearn: Double? = null,
        // Revenues
        val revenue: Double? = null,
        // Revenues (USD)
        val revenueusd: Double? = null,
        // Research and Development Expense
        val rnd: Double? = null,
        // Return on Average Assets
        val roa: Double? = null,
        // Return on Average Equity
        val roe: Double? = null,
        // Return on Invested Capital
        val roic: Double? = null,
        // Return on Sales
        val ros: Double? = null,
        // Share Based Compensation
        val sbcomp: Double? = null,
        // Selling General and Administrative Expense
        val sgna: Double? = null,
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
        val tangibles: Double? = null,
        // Tax Assets
        val taxassets: Double? = null,
        // Income Tax Expense
        val taxexp: Double? = null,
        // Tax Liabilities
        val taxliabilities: Double? = null,
        // Tangible Assets Book Value per Share
        val tbvps: Double? = null,
        // Working Capital
        val workingcapital: Double? = null
)