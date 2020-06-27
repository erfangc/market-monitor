import {MarketSummary} from "./Models";
import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";
import React from "react";

interface Props {
    marketSummaries: MarketSummary[]
}

export function PriceToEarningOvertime(props: Props) {
    const {marketSummaries} = props;
    const options = {
        title: {
            text: 'Price to Earning Overtime'
        },
        xAxis: {
            type: 'datetime'
        },
        yAxis: [
            {
                title: {
                    text: "Value"
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
                    .filter(({pe}) => !!pe)
                    .map(({date, pe}) => ({x: new Date(date), y: pe}))
            },
            {
                type: 'spline',
                name: 'Positive / Negative Earning Ratio',
                yAxis: 1,
                data: marketSummaries
                    .filter(({positiveNegativeRatio}) => !!positiveNegativeRatio)
                    .map(({date, positiveNegativeRatio}) => ({x: new Date(date), y: positiveNegativeRatio}))
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