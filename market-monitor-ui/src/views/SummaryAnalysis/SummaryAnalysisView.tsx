import React, {useEffect, useState} from "react";
import axios from 'axios';
import {Card, Col, Row} from "antd";
import {MedianValueFromGrowth} from "./MedianValueFromGrowth";
import {CompanyAnalysisTable} from "./CompanyAnalysisTable";
import {SectorAnalysis} from "./SectorAnalysis";
import {MedianDiscountRate} from "./MedianDiscountRate";

export function SummaryAnalysisView() {

    const [
        summaryAnalysis,
        setSummaryAnalysis
    ] = useState<SummaryAnalysis | undefined>(undefined)

    useEffect(() => {
        (async function () {
            const {data} = await axios.get<SummaryAnalysis>('/api/summary-analysis');
            setSummaryAnalysis(data);
        }());
    }, []);

    return (
        <Row gutter={[16, 16]}>
            <Col span={12}>
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <MedianValueFromGrowth summaryAnalysis={summaryAnalysis}/>
                    </Col>
                    <Col span={24}>
                        <MedianDiscountRate summaryAnalysis={summaryAnalysis}/>
                    </Col>
                </Row>
            </Col>
            <Col span={12}>
                <SectorAnalysis summaryAnalysis={summaryAnalysis}/>
            </Col>
            <Col span={12}>
                <Card title="Bottom 20 Discounted">
                    <CompanyAnalysisTable companyAnalyses={summaryAnalysis?.snp500BottomDiscountRates}/>
                </Card>
            </Col>
            <Col span={12}>
                <Card title="Top 20 Discounted">
                    <CompanyAnalysisTable companyAnalyses={summaryAnalysis?.snp500TopDiscountRates}/>
                </Card>
            </Col>
        </Row>
    );
}