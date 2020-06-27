import React, {useEffect, useState} from "react";
import axios from "axios";
import {Col, Row, Spin} from "antd";
import {MarketSummary} from "./Models";
import {PriceToEarningOvertime} from "./PriceToEarningOvertime";

export function MarketOverview() {

    const [loading, setLoading] = useState<boolean>(false);
    const [marketSummaries, setMarketSummaries] = useState<MarketSummary[]>([])

    useEffect(() => {
        (async () => {
            setLoading(true);
            const {data} = await axios.get<MarketSummary[]>('/api/analysis/market-summaries');
            setMarketSummaries(data);
            setLoading(false);
        })()
    }, []);

    return (
        <Spin spinning={loading}>
            <Row gutter={[16, 16]}>
                <Col span={12}>
                    <PriceToEarningOvertime marketSummaries={marketSummaries}/>
                </Col>
            </Row>
        </Spin>
    );
}