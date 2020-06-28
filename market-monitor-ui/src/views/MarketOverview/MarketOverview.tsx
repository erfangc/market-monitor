import React, {useEffect, useState} from "react";
import axios from "axios";
import {Col, Row, Spin} from "antd";
import {PriceToEarningOvertime} from "./PriceToEarningOvertime";
import {SectorPriceToEarning} from "./SectorPriceToEarning";
import {PEBubbleChart} from "./PEBubbleChart";

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

    const marketSummary = marketSummaries.length > 0 ? marketSummaries[marketSummaries.length - 1] : undefined;
    return (
        <Spin spinning={loading}>
            <Row gutter={[16, 16]}>
                <Col span={12}>
                    <PriceToEarningOvertime marketSummaries={marketSummaries}/>
                </Col>
                <Col span={12}>
                    <SectorPriceToEarning marketSummary={marketSummary}/>
                </Col>
                <Col span={24}>
                    <PEBubbleChart marketSummary={marketSummary}/>
                </Col>
            </Row>
        </Spin>
    );
}