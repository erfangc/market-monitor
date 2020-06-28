import React, {useState} from "react";
import HighchartsReact from "highcharts-react-official";
import {highcharts} from "../../../highcharts";
import {Card, Select, Space} from "antd";
import {pointFormatter} from "./tooltip-point-formatter";
import {filterContributors} from "./filter-contributors";

interface Props {
    contributors?: PriceToEarningContributor[]
}

export function PEContributors(props: Props) {

    const contributors = props.contributors ?? []
    const [industries, setIndustries] = useState<string[]>([]);
    const [sectors, setSectors] = useState<string[]>([]);

    const data = filterContributors(contributors, industries, sectors)
        .map(contributor => ({
            name: contributor.name,
            y: contributor.peContribution,
            contributor
        }));

    const options: Highcharts.Options = {
        title: {
            text: undefined
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
            pointFormatter
        },
        series: [
            {
                name: 'P/E Contributors',
                type: 'column',
                data
            }
        ]
    };

    const industryOptions = [...new Set(contributors.map(({industry}) => industry))].map(industry => (
        <Select.Option value={industry} key={industry}>{industry}</Select.Option>
    ));

    const sectorOptions = [...new Set(contributors.map(({sector}) => sector))].map(sector => (
        <Select.Option value={sector} key={sector}>{sector}</Select.Option>
    ));

    function handleIndustryChange(value: string[]) {
        setIndustries(value);
    }

    function handleSectorChange(value: string[]) {
        setSectors(value);
    }

    return (
        <Card>
            <Space>
                <Select
                    style={{width: '24rem', marginBottom: '1rem'}}
                    mode="multiple"
                    placeholder="Select an industry"
                    onChange={handleIndustryChange}
                    allowClear
                >
                    {industryOptions}
                </Select>
                <Select
                    style={{width: '24rem', marginBottom: '1rem'}}
                    mode="multiple"
                    placeholder="Select an sector"
                    onChange={handleSectorChange}
                    allowClear
                >
                    {sectorOptions}
                </Select>
            </Space>
            <HighchartsReact
                highcharts={highcharts}
                options={options}
            />
        </Card>
    );
}

