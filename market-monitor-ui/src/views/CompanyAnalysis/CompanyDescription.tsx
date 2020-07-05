import React from 'react';
import {Descriptions} from "antd";

interface Props {
    companyAnalysis?: CompanyAnalysis
}

export function CompanyDescription(props: Props) {
    const {companyAnalysis} = props;
    return (
        <Descriptions bordered>
            <Descriptions.Item label="Name" span={3}>
                {companyAnalysis?.meta?.name ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Category" span={2}>
                {companyAnalysis?.meta?.category ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="SEC Filings" span={1}>
                <a href={`${companyAnalysis?.meta?.secfilings}`}>Link to SEC</a>
            </Descriptions.Item>
            <Descriptions.Item label="Sector" span={2}>
                {companyAnalysis?.meta?.sector ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Industry" span={2}>
                {companyAnalysis?.meta?.industry ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="FAMA Industry" span={2}>
                {companyAnalysis?.meta?.famaindustry ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="FAMA Sector" span={2}>
                {companyAnalysis?.meta?.sector ?? '-'}
            </Descriptions.Item>
        </Descriptions>
    );
}