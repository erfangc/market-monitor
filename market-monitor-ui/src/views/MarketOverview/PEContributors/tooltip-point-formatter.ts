import {numberFormat} from "highcharts";

export function pointFormatter() {
    // @ts-ignore
    const contributor = this.options.contributor as PriceToEarningContributor;
    return `
        <table>
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
