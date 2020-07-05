import React, {useState} from 'react';
import {Col, Row} from "antd";
import {Inputs} from "./Inputs";
import axios from 'axios';
import {CompanyValueAttribution} from "./CompanyValueAttribution";
import {useParams} from "react-router";
import {CompanyDescription} from "./CompanyDescription";
import {CompanyAnalysisSummary} from "./CompanyAnalysisSummary";
import {FundamentalsOvertime} from "./FundamentalsOvertime";

export function CompanyAnalysisView() {

    const {ticker} = useParams<{ ticker?: string }>();

    const [
        companyReturnAnalysis,
        setCompanyAnalysis
    ] = useState<CompanyAnalysis | undefined>(undefined);

    const [
        fundamentals,
        setFundamentals
    ] = useState<Fundamental[]>([]);

    async function runCompanyAnalysis(request: CompanyAnalysisRequest) {
        const companyAnalysisApiResponse = await axios.post<CompanyAnalysis>('/api/company-analysis', request);
        const fundamentalsApiResponse = await axios.get<Fundamental[]>(`/api/fundamentals/${request.ticker}/MRT`);
        setCompanyAnalysis(companyAnalysisApiResponse.data);
        setFundamentals(fundamentalsApiResponse.data);
    }

    return (
        <Row gutter={[16, 16]}>
            <Col span={8}>
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <Inputs
                            ticker={ticker}
                            onChange={runCompanyAnalysis}
                        />
                    </Col>
                    <Col span={24}>
                        <CompanyDescription
                            companyAnalysis={companyReturnAnalysis}
                        />
                    </Col>
                </Row>
            </Col>
            <Col span={16}>
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <CompanyAnalysisSummary
                            companyAnalysis={companyReturnAnalysis}
                        />
                    </Col>
                    <Col span={24}>
                        <CompanyValueAttribution
                            companyAnalysis={companyReturnAnalysis}
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