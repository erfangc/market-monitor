import React from 'react';
import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";
import {Card} from "antd";
import {sectorHighchartsOptions} from "./sector-highcharts-options";

interface Props {
    summaryAnalysis?: SummaryAnalysis
}

export function MedianValueFromGrowth(props: Props) {
    const summaryAnalysis = props.summaryAnalysis?.sectorSummaries ?? []
    const options: Highcharts.Options = {
        ...sectorHighchartsOptions,
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