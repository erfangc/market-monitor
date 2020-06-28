import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";

import React from "react";
import {numberFormat} from "highcharts";

interface Props {
    marketSummary?: MarketSummary
}

export function PEBubbleChart(props: Props) {
    const subMarketSummaries = props.marketSummary?.sectorSummaries || [];

    const options: Highcharts.Options = {
        title: {
            text: 'Price to Earning vs. Market Cap'
        },
        xAxis: {
            title: {
                text: 'Median P/E'
            }
        },
        yAxis: {
            title: {
                text: 'Market Cap Weighted P/E'
            }
        },
        series: [
            {
                tooltip: {
                    pointFormatter: function () {
                        // @ts-ignore
                        const z = this.z;
                        return `
                       <b>Median P/E: </b><span>${numberFormat(this.x, 2)}</span><br>
                       <b>Market Cap Weighted P/E: </b><span>${numberFormat(this.y ?? 0.0, 2)}</span><br>
                       <b>Market Cap: </b><span>${numberFormat(z ?? 0.0, 0)}</span>
                        `;
                    }
                },
                dataLabels: {
                    enabled: true,
                    formatter: function () {
                        return this.point.name
                    }
                },
                name: 'P/E - Sector',
                type: 'bubble',
                data: getData(subMarketSummaries)
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

function getData(subMarketSummaries: SubMarketSummary[]) {
    return subMarketSummaries.map(
        ({medianPe, name, marketCapWeightedPe, marketCap}) => ({
            x: medianPe, y: marketCapWeightedPe, z: marketCap, name
        })
    );
}
