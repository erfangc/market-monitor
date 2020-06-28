import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";

import React from "react";
import {numberFormat} from "highcharts";
import {Card} from "antd";

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
        legend: {
            enabled: false
        },
        tooltip: {
            useHTML: true,
            pointFormatter: function () {
                // @ts-ignore
                const z = this.z;
                return `
                    <table>
                        <tbody>
                        <tr>
                            <td>Median P/E: </td>
                            <td>${numberFormat(this.x, 2)}</td>
                        </tr>
                        <tr>
                            <td>Market Cap Weighted P/E: </td>
                            <td>${numberFormat(this.y ?? 0.0, 2)}</td>
                        </tr>
                        <tr>
                            <td>Market Cap: </td>
                            <td>${numberFormat(z ?? 0.0, 0)}</td>
                        </tr>
                        </tbody>
                    </table>
                    `;
            }
        },
        series: [
            {
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
        <Card>
            <HighchartsReact
                highcharts={highcharts}
                options={options}
            />
        </Card>
    );
}

function getData(subMarketSummaries: SubMarketSummary[]) {
    return subMarketSummaries.map(
        ({medianPe, name, marketCapWeightedPe, marketCap}) => ({
            x: medianPe, y: marketCapWeightedPe, z: marketCap, name
        })
    );
}
