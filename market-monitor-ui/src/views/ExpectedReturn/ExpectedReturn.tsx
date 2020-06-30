import React, {useState} from 'react';
import {Col, notification, Row} from "antd";
import {Inputs} from "./Inputs";
import axios, {AxiosError} from 'axios';
import {CompanyValueAttribution} from "./CompanyValueAttribution";
import {useParams} from "react-router";
import {TickerDescription} from "./TickerDescription";
import {CompanyReturnAnalysisSummary} from "./CompanyReturnAnalysisSummary";
import {FundamentalsOvertime} from "./FundamentalsOvertime";

export function ExpectedReturn() {

    const {ticker: symbol} = useParams<{ ticker?: string }>();

    const [
        companyReturnAnalysis,
        setCompanyReturnAnalysis
    ] = useState<CompanyReturnAnalysis | undefined>(undefined);

    const [
        fundamentals,
        setFundamentals
    ] = useState<Fundamental[]>([]);

    const [
        ticker,
        setTicker
    ] = useState<Ticker | undefined>(undefined);

    async function runCompanyReturnAnalysis(request: CompanyReturnAnalysisRequest) {
        try {
            const companyReturnAnalysisApiResponse = await axios.post<CompanyReturnAnalysis>('/api/company-return-analysis', request);
            const tickersApiResponse = await axios.get<Ticker>(`/api/tickers/${request.ticker}`);
            const fundamentalsApiResponse = await axios.get<Fundamental[]>(`/api/fundamentals/${request.ticker}/MRT`);
            setCompanyReturnAnalysis(companyReturnAnalysisApiResponse.data);
            setTicker(tickersApiResponse.data);
            setFundamentals(fundamentalsApiResponse.data);
        } catch (e) {
            const {response} = e as AxiosError<ApiError>;
            notification.error({message: 'Server Error', description: response?.data?.message});
        }
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
                    <Col span={24}>
                        <FundamentalsOvertime fundamentals={fundamentals}/>
                    </Col>
                </Row>
            </Col>
        </Row>
    );
}