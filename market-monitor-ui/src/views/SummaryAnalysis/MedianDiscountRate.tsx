import React from "react";
import {sectorHighchartsOptions} from "./sector-highcharts-options";
import {Card} from "antd";
import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";

interface Props {
    summaryAnalysis?: SummaryAnalysis
}


export function MedianDiscountRate(props: Props) {
    const summaryAnalysis = props.summaryAnalysis?.sectorSummaries ?? []
    const options: Highcharts.Options = {
        ...sectorHighchartsOptions,
        series: [
            {
                type: 'column',
                data: summaryAnalysis
                    .map(({name, discountRate: {median: y}}) => ({name, y: y * 100}))
                    .sort((a, b) => b.y - a.y)
            }
        ]
    };
    return (
        <Card title="Median Discount Rate">
            <HighchartsReact highcharts={highcharts} options={options}/>
        </Card>
    );

}