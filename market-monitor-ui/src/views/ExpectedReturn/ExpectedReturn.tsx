import React, {useState} from 'react';
import {Card, Col, Row, Space, Statistic} from "antd";
import {Inputs} from "./Inputs";
import axios from 'axios';
import {CompanyValueAttribution} from "./CompanyValueAttribution";

export function ExpectedReturn() {
    const [
        companyReturnAnalysis,
        setCompanyReturnAnalysis
    ] = useState<CompanyReturnAnalysis | undefined>(undefined);

    async function runCompanyAnalysis(request: CompanyReturnAnalysisRequest) {
        const apiResponse = await axios.post<CompanyReturnAnalysis>('/api/company-return-analysis', request);
        setCompanyReturnAnalysis(apiResponse.data);
    }

    return (
        <Row gutter={[16, 16]}>
            <Col span={8}>
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <Inputs onChange={runCompanyAnalysis}/>
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