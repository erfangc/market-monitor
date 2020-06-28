import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";
import React from "react";
import moment from "moment";

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
                    text: "Positive / Negative Earning Ratio"
                },
                opposite: true
            }
        ],
        series: [
            {
                type: 'spline',
                name: 'Price to Earning',
                yAxis: 0,
                data: marketSummaries
                    .map(({date, pe}) => ({x: moment(date).valueOf(), y: pe}))
            },
            {
                type: 'spline',
                name: 'Positive / Negative Earning Ratio',
                yAxis: 1,
                data: marketSummaries
                    .map(({date, positiveNegativeRatio}) => ({x: moment(date).valueOf(), y: positiveNegativeRatio}))
            }
        ]
    };
    return (
        <HighchartsReact
            highcharts={highcharts}
            options={options}
        />
    )
}