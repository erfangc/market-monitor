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

    const grossProfit: Highcharts.SeriesOptionsType = {
        type: 'column',
        name: 'Gross Profit',
        data: fundamentals.map(({reportperiod, cor, revenue}) =>
            ({y: (revenue ?? 0) - (cor ?? 0), x: moment(reportperiod).valueOf()}))
    }

    const netIncome: Highcharts.SeriesOptionsType = {
        type: 'column',
        name: 'Net Income',
        data: fundamentals.map((
            {reportperiod, netinc}
        ) => ({y: netinc, x: moment(reportperiod).valueOf()}))
    };

    const netMargin: Highcharts.SeriesOptionsType = {
        yAxis: 1,
        type: 'spline',
        name: 'Net Margin',
        data: fundamentals.map((
            {reportperiod, netinc, revenue}
        ) => {
            const netMargin = (netinc ?? 0.0) / (revenue ?? 0.0);
            return ({y: netMargin * 100, x: moment(reportperiod).valueOf()});
        })
    };

    const grossMargin: Highcharts.SeriesOptionsType = {
        yAxis: 1,
        marker: {
            enabled: false,
        },
        dashStyle: "Dash",
        type: 'spline',
        name: 'Gross Margin',
        data: fundamentals.map((
            {reportperiod, grossmargin}
        ) => {
            return ({y: (grossmargin ?? 0) * 100, x: moment(reportperiod).valueOf()});
        })
    };

    const sgna: Highcharts.SeriesOptionsType = {
        type: 'spline',
        name: 'SG&A',
        yAxis: 1,
        data: fundamentals.map((
            {
                reportperiod,
                sgna
            }
        ) => {
            const y = sgna
            return {x: moment(reportperiod).valueOf(), y};
        })
    };

    const incomeAnalysis: Highcharts.Options = {
        title: {
            text: 'Revenue vs. Net Income Overtime'
        },
        yAxis: [
            {
                title: {
                    text: 'Monetary Amount'
                },
                gridLineWidth: 0
            },
            {
                title: {
                    text: '%'
                },
                opposite: true,
                gridLineWidth: 0
            }
        ],
        xAxis: {
            type: "datetime"
        },
        series: [revenue, grossProfit, netIncome, netMargin, grossMargin]
    };

    const fixedCostVsRevenue: Highcharts.Options = {
        title: {
            text: 'Revenue vs. SG&A'
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
                    text: 'SG&A'
                },
                gridLineWidth: 0,
                opposite: true,
            }
        ],
        series: [revenue, sgna]
    };

    return (
        <Card title="Fundamental Recap">
            <Row gutter={[16, 16]}>
                <Col span={12}>
                    <HighchartsReact highcharts={highcharts} options={incomeAnalysis}/>
                </Col>
                <Col span={12}>
                    <HighchartsReact highcharts={highcharts} options={fixedCostVsRevenue}/>
                </Col>
            </Row>
        </Card>
    );
}