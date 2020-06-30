import React from "react";
import {Card, Space, Statistic} from "antd";

interface Props {
    companyReturnAnalysis?: CompanyReturnAnalysis
}

export function CompanyReturnAnalysisSummary(props: Props) {
    const {companyReturnAnalysis} = props;
    return (
        <Card>
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
                <Statistic
                    title="Earnings per Share (TTM)"
                    value={companyReturnAnalysis?.eps.toFixed(1)}
                />
            </Space>
        </Card>
    );
}