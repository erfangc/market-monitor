import React from 'react';
import {Descriptions} from "antd";

interface Props {
    companyReturnAnalysis?: CompanyReturnAnalysis
}

export function CompanyDescription(props: Props) {
    const {companyReturnAnalysis} = props;
    return (
        <Descriptions bordered>
            <Descriptions.Item label="Name" span={3}>
                {companyReturnAnalysis?.meta?.name ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Category" span={2}>
                {companyReturnAnalysis?.meta?.category ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="SEC Filings" span={1}>
                <a href={`${companyReturnAnalysis?.meta?.secfilings}`}>Link to SEC</a>
            </Descriptions.Item>
            <Descriptions.Item label="Sector" span={2}>
                {companyReturnAnalysis?.meta?.sector ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Industry" span={2}>
                {companyReturnAnalysis?.meta?.industry ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="FAMA Industry" span={2}>
                {companyReturnAnalysis?.meta?.famaindustry ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="FAMA Sector" span={2}>
                {companyReturnAnalysis?.meta?.sector ?? '-'}
            </Descriptions.Item>
        </Descriptions>
    );
}