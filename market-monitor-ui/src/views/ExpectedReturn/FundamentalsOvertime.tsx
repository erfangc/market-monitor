import React from "react";
import {Card, Col, Row} from "antd";
import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";
import moment from "moment";

interface Props {
    fundamentals: Fundamental[]
}

export function FundamentalsOvertime(props: Props) {
    const {fundamentals} = props;

    const revenue: Highcharts.SeriesOptionsType = {
        type: 'column',
        name: 'Revenue',
        data: fundamentals.map((
            {reportperiod, revenue}
        ) => ({y: revenue, x: moment(reportperiod).valueOf()}))
    };

    const netIncome: Highcharts.SeriesOptionsType = {
        type: 'column',
        name: 'Net Income',
        data: fundamentals.map((
            {reportperiod, netinc}
        ) => ({y: netinc, x: moment(reportperiod).valueOf()}))
    };

    const operatingLeverage: Highcharts.SeriesOptionsType = {
        type: 'column',
        name: 'Margin Analysis',
        yAxis: 1,
        data: fundamentals.map((
            {
                reportperiod,
                sgna,
                revenue,
                cor
            }
        ) => {
            const grossProfit = (revenue ?? 0) - (cor ?? 0)
            const fixedCost = sgna ?? 0
            const y = grossProfit / (fixedCost || 0.0)
            return {x: moment(reportperiod).valueOf(), y};
        })
    };

    const revenueAnalysis: Highcharts.Options = {
        title: {
            text: 'Revenue vs. Net Income Overtime'
        },
        yAxis: {
            title: {
                text: 'Monetary Amount'
            },
            gridLineWidth: 0
        },
        xAxis: {
            type: "datetime"
        },
        series: [revenue, netIncome]
    };

    const marginAnalysis: Highcharts.Options = {
        title: {
            text: 'Operating Leverage'
        },
        xAxis: {
            type: "datetime"
        },
        yAxis: [
            {
                title: {
                    text: 'Revenue'
                },
                gridLineWidth: 0
            },
            {
                title: {
                    text: 'Net Margin %'
                },
                opposite: true,
                gridLineWidth: 0
            }
        ],
        series: [operatingLeverage]
    };

    return (
        <Card title="Fundamental Recap">
            <Row gutter={[16, 16]}>
                <Col span={12}>
                    <HighchartsReact highcharts={highcharts} options={revenueAnalysis}/>
                </Col>
                <Col span={12}>
                    <HighchartsReact highcharts={highcharts} options={marginAnalysis}/>
                </Col>
            </Row>
        </Card>
    );
}