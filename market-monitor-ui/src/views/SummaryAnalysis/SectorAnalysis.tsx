import React from 'react';
import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";
import {numberFormat} from "highcharts";
import {Card} from "antd";

interface Props {
    summaryAnalysis?: SummaryAnalysis
}

export function SectorAnalysis(props: Props) {
    const summaryAnalysis = props.summaryAnalysis?.sectorSummaries ?? []
    const options: Highcharts.Options = {
        title: {
            text: undefined
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: '%'
            }
        },
        legend: {
            enabled: false,
        },
        plotOptions: {
            column: {
                dataLabels: {
                    enabled: true,
                    formatter: function () {
                        return `${numberFormat(this.y ?? 0, 2)}%`
                    }
                }
            }
        },
        series: [
            {
                type: 'column',
                data: summaryAnalysis
                    .map(({name, pctValueFromGrowth: {median: y}}) => ({name, y: y * 100}))
                    .sort((a, b) => b.y - a.y)
            }
        ]
    };

    return (
        <Card title="Median % of Value from Growth by Sector">
            <HighchartsReact highcharts={highcharts} options={options}/>
        </Card>
    );

}