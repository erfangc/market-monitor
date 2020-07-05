import React from "react";
import {Table} from "antd";
import {Link} from "react-router-dom";
import {numberFormat} from "highcharts";

interface Props {
    companyAnalyses?: CompanyAnalysis[]
}

export function CompanyAnalysisTable(props: Props) {
    const companyAnalyses = props.companyAnalyses ?? [];
    return (
        <Table
            columns={[
                {
                    title: 'Ticker',
                    key: 'ticker',
                    dataIndex: 'ticker',
                    render: ticker => (
                        <Link
                            style={{color: 'aqua'}}
                            to={`/company-analysis/${ticker}`}>
                            {ticker}
                        </Link>
                    )
                },
                {
                    title: 'Sector',
                    dataIndex: ['meta', 'sector']
                },
                {
                    title: 'Industry',
                    dataIndex: ['meta', 'industry']
                },
                {
                    title: 'Name',
                    dataIndex: ['meta', 'name']
                },
                {
                    title: 'Discount Rate',
                    dataIndex: 'discountRate',
                    render: discountRate => <span>{numberFormat(discountRate * 100, 2)}%</span>
                }
            ]}
            dataSource={companyAnalyses}
        />
    );
}