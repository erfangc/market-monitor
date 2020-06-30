interface SubMarketSummary {
    contributionToTotalMarketPe: number;
    marketCap: number;
    marketCapWeightedPe: number;
    medianPe: number;
    name: string;
    percentNegativeEarningMarketCapWeighted: number;
    percentNegativeEarningUnweighted: number;
    tickerCount: number;
}

interface MarketSummary {
    _id: string;
    date: string;
    marketCapWeightedPe: number;
    medianPe: number;
    peCount: number;
    percentNegativeEarningMarketCapWeighted: number;
    percentNegativeEarningUnweighted: number;
    sectorSummaries: SubMarketSummary[];
    totalMarketCap: number;
}

interface TemporalAccessor {
}

interface Temporal extends TemporalAccessor {
}

interface TemporalAdjuster {
}

interface Instant extends Temporal, TemporalAdjuster {
}

interface ApiError {
    message: string;
    status: number;
    timestamp: Instant;
}

interface DailyMetric {
    date: string;
    ev: number | null;
    evebit: number | null;
    evebitda: number | null;
    lastupdated: string;
    marketcap: number | null;
    pb: number | null;
    pe: number | null;
    ps: number | null;
    ticker: string;
}

interface Fundamental {
    accoci: number | null;
    assets: number | null;
    assetsavg: number | null;
    assetsc: number | null;
    assetsnc: number | null;
    assetturnover: number | null;
    bvps: number | null;
    calendardate: string | null;
    capex: number | null;
    cashneq: number | null;
    cashnequsd: number | null;
    consolinc: number | null;
    cor: number | null;
    currentratio: number | null;
    datekey: string;
    de: number | null;
    debt: number | null;
    debtc: number | null;
    debtnc: number | null;
    debtusd: number | null;
    deferredrev: number | null;
    depamor: number | null;
    deposits: number | null;
    dimension: string;
    divyield: number | null;
    dps: number | null;
    ebit: number | null;
    ebitda: number | null;
    ebitdamargin: number | null;
    ebitdausd: number | null;
    ebitusd: number | null;
    ebt: number | null;
    eps: number | null;
    epsdil: number | null;
    epsusd: number | null;
    equity: number | null;
    equityavg: number | null;
    equityusd: number | null;
    ev: number | null;
    evebit: number | null;
    evebitda: number | null;
    fcf: number | null;
    fcfps: number | null;
    fxusd: number | null;
    gp: number | null;
    grossmargin: number | null;
    intangibles: number | null;
    intexp: number | null;
    invcap: number | null;
    invcapavg: number | null;
    inventory: number | null;
    investments: number | null;
    investmentsc: number | null;
    investmentsnc: number | null;
    lastupdated: string | null;
    liabilities: number | null;
    liabilitiesc: number | null;
    liabilitiesnc: number | null;
    marketcap: number | null;
    ncf: number | null;
    ncfbus: number | null;
    ncfcommon: number | null;
    ncfdebt: number | null;
    ncfdiv: number | null;
    ncff: number | null;
    ncfi: number | null;
    ncfinv: number | null;
    ncfo: number | null;
    ncfx: number | null;
    netinc: number | null;
    netinccmn: number | null;
    netinccmnusd: number | null;
    netincdis: number | null;
    netincnci: number | null;
    netmargin: number | null;
    opex: number | null;
    opinc: number | null;
    payables: number | null;
    payoutratio: number | null;
    pb: number | null;
    pe: number | null;
    pe1: number | null;
    ppnenet: number | null;
    prefdivis: number | null;
    price: number | null;
    ps: number | null;
    ps1: number | null;
    receivables: number | null;
    reportperiod: string;
    retearn: number | null;
    revenue: number | null;
    revenueusd: number | null;
    rnd: number | null;
    roa: number | null;
    roe: number | null;
    roic: number | null;
    ros: number | null;
    sbcomp: number | null;
    sgna: number | null;
    sharefactor: number | null;
    sharesbas: number | null;
    shareswa: number | null;
    shareswadil: number | null;
    sps: number | null;
    tangibles: number | null;
    taxassets: number | null;
    taxexp: number | null;
    taxliabilities: number | null;
    tbvps: number | null;
    ticker: string;
    workingcapital: number | null;
}

interface PriceToEarningContributor {
    industry: string;
    marketCap: number;
    name: string;
    pe: number;
    peContribution: number;
    sector: string;
    ticker: string;
}

interface PricingFunctionOutputs {
    contributionFromBvps: number;
    contributionFromCurrentEarnings: number;
    contributionFromGrowth: number;
    contributionFromShortTerm: number;
    contributionFromTerminalValue: number;
    price: number;
}

interface Scenario {
    expectedReturn: number;
    longTermGrowth: number;
    price: number;
}

interface ShortTermEpsGrowth {
    date: string;
    eps: number | null;
    growthRate: number | null;
}

interface CompanyReturnAnalysis {
    bvps: number;
    date: string;
    eps: number;
    expectedReturn: number;
    longTermGrowth: number;
    priceToEarning: number;
    pricingFunctionOutputs: PricingFunctionOutputs;
    scenarios: Scenario[];
    shortTermEpsGrowths: ShortTermEpsGrowth[];
    ticker: string;
}

interface CompanyReturnAnalysisRequest {
    date: string | null;
    longTermGrowth: number;
    price: number | null;
    shortTermEpsGrowths: ShortTermEpsGrowth[];
    ticker: string;
}

interface Ticker {
    category: string | null;
    companysite: string | null;
    currency: string | null;
    cusips: string | null;
    exchange: string | null;
    famaindustry: string | null;
    famasector: string | null;
    firstadded: string | null;
    firstpricedate: string | null;
    firstquarter: string | null;
    industry: string | null;
    isdelisted: string | null;
    lastpricedate: string | null;
    lastquarter: string | null;
    lastupdated: string | null;
    location: string | null;
    name: string | null;
    permaticker: string;
    relatedtickers: string | null;
    scalemarketcap: string | null;
    scalerevenue: string | null;
    secfilings: string | null;
    sector: string | null;
    siccode: string | null;
    sicindustry: string | null;
    sicsector: string | null;
    table: string;
    ticker: string;
}