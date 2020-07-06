import {numberFormat} from "highcharts";

export const sectorHighchartsOptions: Highcharts.Options = {
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
};