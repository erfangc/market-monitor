import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";
import React from "react";
import moment from "moment";
import {Card} from "antd";

interface Props {
    marketSummaries: MarketSummary[]
}

export function PriceToEarningOvertime(props: Props) {
    const marketSummaries = (props?.marketSummaries || [])
        .filter(({totalMarketCap}) => totalMarketCap !== 0);
    const options: Highcharts.Options = {
        title: {
            text: 'Price to Earning Overtime'
        },
        xAxis: {
            type: 'datetime'
        },
        yAxis: [
            {
                title: {
                    text: "Multiple"
                }
            },
            {
                title: {
                    text: "% Negative Earning (Market Cap Weighted)"
                },
                opposite: true
            },
            {
                title: {
                    text: "% Negative Earning (Unweighted)"
                },
                opposite: true
            }
        ],
        series: [
            {
                type: 'spline',
                name: 'Price to Earning (Market Cap Weighted)',
                yAxis: 0,
                data: marketSummaries
                    .map(({date, marketCapWeightedPe}) => ({x: moment(date).valueOf(), y: marketCapWeightedPe}))
            },
            {
                type: 'spline',
                name: 'Price to Earning (Median)',
                yAxis: 0,
                data: marketSummaries
                    .map(({date, medianPe}) => ({x: moment(date).valueOf(), y: medianPe}))
            },
            {
                type: 'spline',
                name: '% Negative Earning (Market Cap Weighted)',
                yAxis: 1,
                data: marketSummaries
                    .map(({date, percentNegativeEarningMarketCapWeighted}) => (
                        {x: moment(date).valueOf(), y: percentNegativeEarningMarketCapWeighted * 100}
                    ))
            },
            {
                type: 'spline',
                name: '% Negative Earning (Unweighted)',
                yAxis: 2,
                data: marketSummaries
                    .map(({date, percentNegativeEarningUnweighted}) => (
                        {x: moment(date).valueOf(), y: percentNegativeEarningUnweighted * 100}
                    ))
            }
        ]
    };
    return (
        <Card>
            <HighchartsReact
                highcharts={highcharts}
                options={options}
            />
        </Card>
    )
}