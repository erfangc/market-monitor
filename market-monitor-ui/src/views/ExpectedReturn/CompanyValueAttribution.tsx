import React from 'react';
import {Col, Row} from "antd";
import HighchartsReact from "highcharts-react-official";
import {colors, highcharts} from "../../highcharts";

interface Props {
    companyReturnAnalysis?: CompanyReturnAnalysis
}

export function CompanyValueAttribution(
    {companyReturnAnalysis}: Props
) {
    const pricingFnOutputs = companyReturnAnalysis?.pricingFunctionOutputs;

    const commonOptions: Highcharts.Options = {
        yAxis: {
            title: {
                text: 'Contribution'
            }
        },
        xAxis: {
            type: "category"
        },
        legend: {
            enabled: false
        }
    };

    const options: Highcharts.Options = {
        title: {
            text: 'Short vs. Long-Term Value'
        },
        ...commonOptions,
        series: [
            {
                name: 'Contributions',
                type: 'waterfall',
                data: [
                    {
                        name: 'BVPS',
                        y: pricingFnOutputs?.contributionFromBvps
                    },
                    {
                        name: 'Short-Term Profit',
                        color: colors[1],
                        y: pricingFnOutputs?.contributionFromShortTerm
                    },
                    {
                        name: 'Terminal Value',
                        color: colors[2],
                        y: pricingFnOutputs?.contributionFromTerminalValue
                    },
                    {
                        name: 'Total',
                        color: colors[3],
                        isSum: true
                    }
                ]
            }
        ]
    };
    const options2: Highcharts.Options = {
        title: {
            text: 'Existing Operation vs. Value from Growth'
        },
        ...commonOptions,
        series: [
            {
                name: 'Contributions',
                type: 'waterfall',
                data: [
                    {
                        name: 'BVPS',
                        y: pricingFnOutputs?.contributionFromBvps
                    },
                    {
                        name: 'Current Earning',
                        color: colors[1],
                        y: pricingFnOutputs?.contributionFromCurrentEarnings
                    },
                    {
                        name: 'Growth',
                        color: colors[2],
                        y: pricingFnOutputs?.contributionFromGrowth
                    },
                    {
                        name: 'Total',
                        color: colors[3],
                        isSum: true
                    }
                ]
            }
        ]
    };

    return (
        <Row gutter={[16, 16]}>
            <Col span={12}>
                <HighchartsReact highcharts={highcharts} options={options}/>
            </Col>
            <Col span={12}>
                <HighchartsReact highcharts={highcharts} options={options2}/>
            </Col>
        </Row>
    );
}