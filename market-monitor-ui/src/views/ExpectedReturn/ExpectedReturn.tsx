import React, {useState} from 'react';
import {Col, Row} from "antd";
import {Inputs} from "./Inputs";
import axios from 'axios';
import {CompanyValueAttribution} from "./CompanyValueAttribution";
import {useParams} from "react-router";
import {TickerDescription} from "./TickerDescription";
import {CompanyReturnAnalysisSummary} from "./CompanyReturnAnalysisSummary";

export function ExpectedReturn() {

    const {ticker: symbol} = useParams<{ ticker?: string }>();

    const [
        companyReturnAnalysis,
        setCompanyReturnAnalysis
    ] = useState<CompanyReturnAnalysis | undefined>(undefined);

    const [
        ticker,
        setTicker
    ] = useState<Ticker | undefined>(undefined);

    async function runCompanyReturnAnalysis(request: CompanyReturnAnalysisRequest) {
        const companyReturnAnalysisApiResponse = await axios.post<CompanyReturnAnalysis>('/api/company-return-analysis', request);
        const tickersApiResponse = await axios.get<Ticker>(`/api/tickers/${request.ticker}`);
        setCompanyReturnAnalysis(companyReturnAnalysisApiResponse.data);
        setTicker(tickersApiResponse.data);
    }

    return (
        <Row gutter={[16, 16]}>
            <Col span={8}>
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <Inputs
                            ticker={symbol}
                            onChange={runCompanyReturnAnalysis}
                        />
                    </Col>
                    <Col span={24}>
                        <TickerDescription ticker={ticker}/>
                    </Col>
                </Row>
            </Col>
            <Col span={16}>
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <CompanyReturnAnalysisSummary
                            companyReturnAnalysis={companyReturnAnalysis}
                        />
                    </Col>
                    <Col span={24}>
                        <CompanyValueAttribution
                            companyReturnAnalysis={companyReturnAnalysis}
                        />
                    </Col>
                </Row>
            </Col>
        </Row>
    );
}