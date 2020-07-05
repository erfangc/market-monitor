import React from 'react';
import {Card, Col, Row} from "antd";
import HighchartsReact from "highcharts-react-official";
import {colors, highcharts} from "../../highcharts";
import {numberFormat} from "highcharts";

interface Props {
    companyAnalysis?: CompanyAnalysis
}

export function CompanyValueAttribution(
    {companyAnalysis}: Props
) {
    const pricingFnOutputs = companyAnalysis?.pricingFunctionOutputs;

    const commonOptions: Highcharts.Options = {
        yAxis: {
            title: {
                text: null
            },
            gridLineWidth: 0
        },
        xAxis: {
            type: "category"
        },
        legend: {
            enabled: false
        },
        tooltip: {
            enabled: false
        },
        plotOptions: {
            waterfall: {
                lineWidth: 2,
                lineColor: '#ebebeb',
                dataLabels: {
                    useHTML: true,
                    enabled: true,
                    formatter: function () {
                        const monetary = this.point.y ?? 0;
                        const percentage = monetary * 100 / (pricingFnOutputs?.price ?? 0.0);
                        return `
                        ${numberFormat(percentage, 1)}%
                        </br>
                        $${numberFormat(monetary, 1)}
                        `;
                    }
                }
            }
        }
    };

    const shortLongTerm: Highcharts.Options = {
        title: {
            text: 'Value from Short vs. Long Term'
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
    const valueVsGrowth: Highcharts.Options = {
        title: {
            text: 'Value from Current Earnings vs. Value from Growth'
        },
        ...commonOptions,
        series: [
            {
                name: 'Value from Current Earnings vs. Value from Growth',
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
        <Card title="Company Analysis">
            <Row gutter={[16, 16]}>
                <Col span={12}>
                    <HighchartsReact highcharts={highcharts} options={shortLongTerm}/>
                </Col>
                <Col span={12}>
                    <HighchartsReact highcharts={highcharts} options={valueVsGrowth}/>
                </Col>
            </Row>
        </Card>
    );
}