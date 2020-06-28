import {Card} from "antd";
import React from "react";
import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";
import {SeriesOptionsType} from "highcharts";
import moment from "moment";

interface Props {
    marketSummaries: MarketSummary[]
}

export function SectorContributionOvertime(props: Props) {
    const {marketSummaries} = props;

    // @ts-ignore
    const series = marketSummaries.reduce((allSeries: Array<SeriesOptionsType>, marketSummary) => {
        const {date, sectorSummaries} = marketSummary;
        if (sectorSummaries.length === 0) {
            return allSeries;
        }
        return sectorSummaries.map(subMarketSummary => {
            const series = allSeries
                .find(series => series.name === subMarketSummary.name);
            const y = subMarketSummary.contributionToTotalMarketPe;
            const x = moment(date).valueOf();

            if (series === undefined) {
                // create a new series if this is the 1st encounter with a subMarketSummary of a given name
                return {
                    name: subMarketSummary.name,
                    type: 'areaspline',
                    data: [{x, y}]
                };
            } else {
                // or else just append the datum point to an existing series
                return {
                    ...series,
                    data: [
                        // @ts-ignore
                        ...series.data,
                        {x, y}
                    ]
                };
            }
        });
    }, []);

    const options: Highcharts.Options = {
        title: {
            text: 'P/E Contribution from Sector Overtime'
        },
        subtitle: {
            text: 'Market Cap Weighted'
        },
        xAxis: {
            type: "datetime"
        },
        yAxis: {
            title: {
                text: 'P/E Multiple Contribution'
            }
        },
        plotOptions: {
            areaspline: {
                marker: {
                    enabled: false
                },
                stacking: "normal"
            }
        },
        series
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
