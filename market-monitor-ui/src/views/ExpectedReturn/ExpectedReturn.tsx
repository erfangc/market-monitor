import React, {useState} from 'react';
import {Card, Col, Descriptions, Row, Space, Statistic} from "antd";
import {Inputs} from "./Inputs";
import axios from 'axios';
import {CompanyValueAttribution} from "./CompanyValueAttribution";
import {useParams} from "react-router";

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

    async function runCompanyAnalysis(request: CompanyReturnAnalysisRequest) {
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
                        <Inputs ticker={symbol} onChange={runCompanyAnalysis}/>
                    </Col>
                    <Col span={24}>
                        <Descriptions bordered>
                            <Descriptions.Item label="Name" span={3}>
                                {ticker?.name ?? '-'}
                            </Descriptions.Item>
                            <Descriptions.Item label="Category" span={2}>
                                {ticker?.category ?? '-'}
                            </Descriptions.Item>
                            <Descriptions.Item label="SEC Filings" span={1}>
                                <a href={`${ticker?.secfilings}`}>Link to SEC</a>
                            </Descriptions.Item>
                            <Descriptions.Item label="Sector" span={2}>
                                {ticker?.sector ?? '-'}
                            </Descriptions.Item>
                            <Descriptions.Item label="Industry" span={2}>
                                {ticker?.industry ?? '-'}
                            </Descriptions.Item>
                            <Descriptions.Item label="FAMA Industry" span={2}>
                                {ticker?.famaindustry ?? '-'}
                            </Descriptions.Item>
                            <Descriptions.Item label="FAMA Sector" span={2}>
                                {ticker?.sector ?? '-'}
                            </Descriptions.Item>
                        </Descriptions>
                    </Col>
                </Row>
            </Col>
            <Col span={16}>
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <Card>
                            <Col span={24}>
                                <Space>
                                    <Statistic
                                        title="Expected Return"
                                        value={((companyReturnAnalysis?.expectedReturn ?? 0) * 100).toFixed(1)}
                                        suffix="%"
                                    />
                                    <Statistic
                                        title="Price to Earning"
                                        value={companyReturnAnalysis?.priceToEarning.toFixed(1)}
                                    />
                                </Space>
                            </Col>
                        </Card>
                    </Col>
                    <Col span={24}>
                        <Card title="Company Return Analysis">
                            <CompanyValueAttribution companyReturnAnalysis={companyReturnAnalysis}/>
                        </Card>
                    </Col>
                </Row>
            </Col>
        </Row>
    );
}