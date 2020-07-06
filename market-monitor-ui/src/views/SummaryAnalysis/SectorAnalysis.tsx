import React, {useState} from "react";
import {Card, Radio, Select, Space} from "antd";
import {CompanyAnalysisTable} from "./CompanyAnalysisTable";
import {RadioChangeEvent} from "antd/es/radio";
import {SelectValue} from "antd/es/select";

interface Props {
    summaryAnalysis?: SummaryAnalysis
}

type Type = 'most-overvalued' | 'most-undervalued';

export function SectorAnalysis(props: Props) {

    const sectorSummaries = props.summaryAnalysis?.sectorSummaries ?? [];
    const [sectorSummary, setSectorSummary] = useState<SectorSummary>();
    const [type, setType] = useState<Type>("most-overvalued");

    let widget = null;
    if (type === "most-overvalued") {
        widget = <CompanyAnalysisTable companyAnalyses={sectorSummary?.bottom20DiscountRate}/>
    } else if (type === "most-undervalued") {
        widget = <CompanyAnalysisTable companyAnalyses={sectorSummary?.top20DiscountRate}/>
    }

    function handleTypeSelect(e: RadioChangeEvent) {
        setType(e.target.value);
    }

    function handleSectorSelect(value: SelectValue) {
        const found = sectorSummaries.find(({name}) => name === value);
        setSectorSummary(found);
    }

    const sectorOptions = sectorSummaries.map(({name}) => ({value: name})) ?? [];

    const title = (
        <Space>
            <Select
                style={{width: '13rem'}}
                onChange={handleSectorSelect}
                placeholder="Choose sector"
                options={sectorOptions}
            />
            <Radio.Group
                value={type}
                options={[
                    {value: 'most-undervalued', label: 'Most Undervalued'},
                    {value: 'most-overvalued', label: 'Most Overvalued'}
                ]}
                onChange={handleTypeSelect}
                optionType="button"
            />
        </Space>
    );

    return (
        <React.Fragment>
            <Card title={title}>
                {widget}
            </Card>
        </React.Fragment>
    );

}