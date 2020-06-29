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
    accoci: string | null;
    assets: string | null;
    assetsavg: string | null;
    assetsc: string | null;
    assetsnc: string | null;
    assetturnover: number | null;
    bvps: number | null;
    calendardate: string;
    capex: string | null;
    cashneq: string | null;
    cashnequsd: number | null;
    consolinc: string | null;
    cor: string | null;
    currentratio: number | null;
    datekey: string;
    de: number | null;
    debt: string | null;
    debtc: string | null;
    debtnc: string | null;
    debtusd: number | null;
    deferredrev: string | null;
    depamor: string | null;
    deposits: string | null;
    dimension: string;
    divyield: number | null;
    dps: number | null;
    ebit: string | null;
    ebitda: string | null;
    ebitdamargin: number | null;
    ebitdausd: number | null;
    ebitusd: number | null;
    ebt: string | null;
    eps: number | null;
    epsdil: number | null;
    epsusd: number | null;
    equity: string | null;
    equityavg: string | null;
    equityusd: number | null;
    ev: number | null;
    evebit: number | null;
    evebitda: number | null;
    fcf: string | null;
    fcfps: number | null;
    fxusd: number | null;
    gp: string | null;
    grossmargin: number | null;
    intangibles: string | null;
    intexp: string | null;
    invcap: string | null;
    invcapavg: string | null;
    inventory: string | null;
    investments: string | null;
    investmentsc: string | null;
    investmentsnc: string | null;
    lastupdated: string;
    liabilities: string | null;
    liabilitiesc: string | null;
    liabilitiesnc: string | null;
    marketcap: number | null;
    ncf: string | null;
    ncfbus: string | null;
    ncfcommon: string | null;
    ncfdebt: string | null;
    ncfdiv: string | null;
    ncff: string | null;
    ncfi: string | null;
    ncfinv: string | null;
    ncfo: string | null;
    ncfx: string | null;
    netinc: string | null;
    netinccmn: string | null;
    netinccmnusd: number | null;
    netincdis: string | null;
    netincnci: string | null;
    netmargin: number | null;
    opex: string | null;
    opinc: string | null;
    payables: string | null;
    payoutratio: number | null;
    pb: number | null;
    pe: number | null;
    pe1: number | null;
    ppnenet: string | null;
    prefdivis: string | null;
    price: number | null;
    ps: number | null;
    ps1: number | null;
    receivables: string | null;
    reportperiod: string;
    retearn: string | null;
    revenue: number | null;
    revenueusd: number | null;
    rnd: string | null;
    roa: number | null;
    roe: number | null;
    roic: number | null;
    ros: number | null;
    sbcomp: string | null;
    sgna: string | null;
    sharefactor: number | null;
    sharesbas: number | null;
    shareswa: number | null;
    shareswadil: number | null;
    sps: number | null;
    tangibles: string | null;
    taxassets: string | null;
    taxexp: string | null;
    taxliabilities: string | null;
    tbvps: number | null;
    ticker: string;
    workingcapital: string | null;
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