import React, {useState} from 'react';
import {Col, Row} from "antd";
import {Inputs} from "./Inputs";
import axios from 'axios';
import {CompanyValueAttribution} from "./CompanyValueAttribution";
import {useParams} from "react-router";
import {CompanyDescription} from "./CompanyDescription";
import {CompanyReturnAnalysisSummary} from "./CompanyReturnAnalysisSummary";
import {FundamentalsOvertime} from "./FundamentalsOvertime";

export function ExpectedReturn() {

    const {ticker} = useParams<{ ticker?: string }>();

    const [
        companyReturnAnalysis,
        setCompanyReturnAnalysis
    ] = useState<CompanyReturnAnalysis | undefined>(undefined);

    const [
        fundamentals,
        setFundamentals
    ] = useState<Fundamental[]>([]);

    async function runCompanyReturnAnalysis(request: CompanyReturnAnalysisRequest) {
        const companyReturnAnalysisApiResponse = await axios.post<CompanyReturnAnalysis>('/api/company-return-analysis', request);
        const fundamentalsApiResponse = await axios.get<Fundamental[]>(`/api/fundamentals/${request.ticker}/MRT`);
        setCompanyReturnAnalysis(companyReturnAnalysisApiResponse.data);
        setFundamentals(fundamentalsApiResponse.data);
    }

    return (
        <Row gutter={[16, 16]}>
            <Col span={8}>
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <Inputs
                            ticker={ticker}
                            onChange={runCompanyReturnAnalysis}
                        />
                    </Col>
                    <Col span={24}>
                        <CompanyDescription
                            companyReturnAnalysis={companyReturnAnalysis}
                        />
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