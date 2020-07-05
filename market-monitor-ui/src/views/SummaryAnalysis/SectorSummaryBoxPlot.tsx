import React from 'react';
import {highcharts} from "../../highcharts";
import HighchartsReact from "highcharts-react-official";

interface Props {
    summaryAnalysis?: SummaryAnalysis
}

export function SectorSummaryBoxPlot(props: Props) {
    const {summaryAnalysis} = props;
    const data = (summaryAnalysis?.sectorSummaries ?? [])
        .map(({name, pctValueFromGrowth: {average, median, stddev}}) => {
            return {
                low: average - 2 * stddev,
                q1: average - stddev,
                median,
                q3: average + stddev,
                high: average + 2 * stddev,
                name
            };
        });

    const options: Highcharts.Options = {
        title: {
            text: '% of Price from Growth by Sector'
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            max: 1,
            min: 0
        },
        legend: {
            enabled: false
        },
        series: [

            {
                type: "boxplot",
                data: data
            }
        ]
    };
    return (
        <HighchartsReact
            highcharts={highcharts}
            options={options}
        />
    );
}