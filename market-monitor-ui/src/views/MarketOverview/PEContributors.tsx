import React from "react";
import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../highcharts";
import {numberFormat} from "highcharts";

interface Props {
    contributors?: PriceToEarningContributor[]
}

export function PEContributors(props: Props) {
    const contributors = props.contributors ?? []
    const options: Highcharts.Options = {
        title: {
            text: 'Individual Contributor to P/E'
        },
        yAxis: {
            title: {
                text: 'PE Contribution'
            }
        },
        xAxis: {
            labels: {
                enabled: false
            }
        },
        legend: {
            enabled: false
        },
        tooltip: {
            // @ts-ignore
            useHTML: true,
            pointFormatter: function () {
                // @ts-ignore
                const contributor = this.options.contributor as PriceToEarningContributor;
                return `
                <table class="tooltip-table">
                <tbody>
                    <tr>
                        <td>Name</td>
                        <td>${contributor.name}</td>
                    </tr>
                    <tr>
                        <td>Industry</td>
                        <td>${contributor.industry}</td>
                    </tr>
                    <tr>
                        <td>Sector</td>
                        <td>${contributor.sector}</td>
                    </tr>
                    <tr>
                        <td>PE Contribution</td>
                        <td>${numberFormat(contributor.peContribution, 1)}</td>
                    </tr>
                    <tr>
                        <td>Market Cap</td>
                        <td>${numberFormat(contributor.marketCap, 1)}</td>
                    </tr>
                    <tr>
                        <td>P/E</td>
                        <td>${numberFormat(contributor.pe, 1)}</td>
                    </tr>
                </tbody>
                </table>
                        `;
            }
        },
        series: [
            {
                name: 'P/E Contributors',
                type: 'column',
                data: contributors.map(contributor => ({
                    name: contributor.name,
                    y: contributor.peContribution,
                    contributor
                }))
            }
        ]
    };
    return (
        <HighchartsReact highcharts={highcharts} options={options}/>
    );
}