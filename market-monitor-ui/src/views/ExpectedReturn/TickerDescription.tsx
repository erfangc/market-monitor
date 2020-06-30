import React from 'react';
import {Descriptions} from "antd";

interface Props {
    ticker?: Ticker
}

export function TickerDescription(props: Props) {
    const {ticker} = props;
    return (
        <Descriptions bordered>
            <Descriptions.Item label="Name" span={3}>
                {ticker?.name ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Category" span={2}>
                {ticker?.category ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="SEC Filings" span={1}>
                <a href={`${ticker?.secfilings}`}>Link to SEC</a>
            </Descriptions.Item>
            <Descriptions.Item label="Sector" span={2}>
                {ticker?.sector ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Industry" span={2}>
                {ticker?.industry ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="FAMA Industry" span={2}>
                {ticker?.famaindustry ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="FAMA Sector" span={2}>
                {ticker?.sector ?? '-'}
            </Descriptions.Item>
        </Descriptions>
    );
}