import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";
import React from "react";
import {Card} from "antd";

interface Props {
    marketSummary?: MarketSummary
}

export function SectorPriceToEarning(props: Props) {
    const sectorSummaries = (
        props.marketSummary?.sectorSummaries || []
    ).sort((a, b) => a.medianPe - b.medianPe);

    const options: Highcharts.Options = {
        xAxis: {
            categories: sectorSummaries.map(({name}) => name)
        },
        title: {
            text: 'Sector Relative Valuation'
        },
        yAxis: {
            title: {
                text: 'Multiple'
            }
        },
        series: [
            {
                name: 'Median P/E',
                data: sectorSummaries.map(({name, medianPe}) => ({
                    name,
                    y: medianPe
                })),
                type: 'column'
            },
            {
                name: 'Market Cap Weighted P/E',
                data: sectorSummaries.map(({name, marketCapWeightedPe}) => ({
                    name,
                    y: marketCapWeightedPe
                })),
                type: 'column'
            }
        ]
    }

    return (
        <Card>
            <HighchartsReact
                highcharts={highcharts}
                options={options}
            />
        </Card>
    )
}