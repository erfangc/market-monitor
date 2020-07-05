import React from "react";
import {Card, Space, Statistic} from "antd";

interface Props {
    companyAnalysis?: CompanyAnalysis
}

export function CompanyAnalysisSummary(props: Props) {
    const {companyAnalysis} = props;
    return (
        <Card>
            <Space>
                <Statistic
                    title="Expected Return"
                    value={((companyAnalysis?.discountRate ?? 0) * 100).toFixed(1)}
                    suffix="%"
                />
                <Statistic
                    title="Price to Earning"
                    value={companyAnalysis?.priceToEarning.toFixed(1)}
                />
                <Statistic
                    title="Earnings per Share (TTM)"
                    value={companyAnalysis?.eps.toFixed(1)}
                />
            </Space>
        </Card>
    );
}