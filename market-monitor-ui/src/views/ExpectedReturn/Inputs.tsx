import React, {ChangeEvent, useEffect, useState} from 'react';
import {AutoComplete, Button, Card, Col, DatePicker, Input, InputNumber, Row, Space} from "antd";
import {MinusCircleOutlined, PlusOutlined} from '@ant-design/icons';
import moment from "moment";
import axios from 'axios';
import {useHistory} from "react-router";

interface Props {
    ticker?: string
    onChange: (request: CompanyReturnAnalysisRequest) => void
}

const now = moment();
const date = now.format("YYYY-MM-DD");

const initialState: CompanyReturnAnalysisRequest = {
    date,
    longTermGrowth: 0.0,
    shortTermEpsGrowths: [
        {
            eps: 0.0,
            date: now.add(1, 'year').format("YYYY-MM-DD")
        },
        {
            eps: 0.0,
            date: now.add(2, 'year').format("YYYY-MM-DD")
        }
    ],
    ticker: '',
    price: 0.0
};

export function Inputs(props: Props) {

    const [request, setRequest] = useState<CompanyReturnAnalysisRequest>(initialState);
    const [tickerOptions, setTickerOptions] = useState<Ticker[]>([]);
    const [value, setValue] = useState<string>('');
    const history = useHistory();

    /**
     * Automatically pre-populated
     * the request fields based on ticker
     */
    async function autoPopulateRequest(ticker?: string) {
        const apiResponse = await axios
            .get<CompanyReturnAnalysisRequest>(
                `/api/company-return-analysis/request/${ticker}`
            );
        setRequest(apiResponse.data);
        props.onChange(apiResponse.data);
    }

    useEffect(() => {
        // eslint-disable-next-line
        autoPopulateRequest(props.ticker)
    }, [props.ticker]);

    const {date, price, longTermGrowth, shortTermEpsGrowths} = request;

    function handleDateChange(date?: string) {
        if (!date) {
            return;
        }
        setRequest({...request, date});
    }

    function handlePriceChange({currentTarget: {value}}: ChangeEvent<HTMLInputElement>) {
        setRequest({...request, price: parseFloat(value)})
    }

    function handleTickerSelect(ticker: string) {
        history.push(`/expected-return/${ticker}`);
    }

    async function handleTickerSearch(term: string) {
        const {data} = await axios.get<Ticker[]>('/api/tickers/_match', {params: {term}});
        setTickerOptions(data);
    }

    function removeShortTermEpsGrowth(date: string) {
        setRequest({
            ...request,
            shortTermEpsGrowths: shortTermEpsGrowths.filter(epsGrowth => epsGrowth.date !== date)
        });
    }

    function addShortTermEpsGrowth() {
        // determine the last date for which there is an EPS estimate
        // by default the next EPS estimate date is 1 year from that date
        const lastEpsDate = shortTermEpsGrowths.length !== 0
            ? shortTermEpsGrowths[shortTermEpsGrowths.length - 1].date
            : undefined;
        const shortTermEpsGrowth: ShortTermEpsGrowth = {
            date: plusOneYear(lastEpsDate),
            eps: 1
        };
        setRequest({
            ...request,
            shortTermEpsGrowths: [
                ...shortTermEpsGrowths,
                shortTermEpsGrowth
            ]
        });
    }

    function handleLongTermGrowthUpdate(
        {currentTarget: {value}}: ChangeEvent<HTMLInputElement>
    ) {
        setRequest({...request, longTermGrowth: parseFloat(value)});
    }

    function handleEpsDateUpdate(oldDate: string, newDate?: string) {
        if (!newDate) {
            return;
        }
        const updated = shortTermEpsGrowths.map(epsGrowth => {
            if (epsGrowth.date === oldDate) {
                return {...epsGrowth, date: newDate};
            } else {
                return epsGrowth;
            }
        });
        setRequest({...request, shortTermEpsGrowths: updated});
    }

    function handleEpsUpdate(date: string, newValue: any) {
        const updated = shortTermEpsGrowths.map(epsGrowth => {
            if (epsGrowth.date === date) {
                return {...epsGrowth, eps: newValue};
            } else {
                return epsGrowth;
            }
        });
        setRequest({...request, shortTermEpsGrowths: updated});
    }

    function submit() {
        props.onChange(request);
    }

    function handleTickerChange(newValue: string) {
        setValue(newValue);
    }

    return (
        <Card title="Inputs">
            <DatePicker
                value={moment(date)}
                onChange={newValue => handleDateChange(newValue?.format("YYYY-MM-DD"))}
            />
            <br/><br/>
            <AutoComplete
                style={{width: '100%'}}
                onChange={handleTickerChange}
                onSearch={handleTickerSearch}
                onSelect={handleTickerSelect}
                placeholder="Search ticker"
                value={value}
            >
                {tickerOptions.map(({ticker, name}, idx) => (
                    <AutoComplete.Option key={idx} value={ticker}>
                        <Row>
                            <Col span={12}>{ticker}</Col>
                            <Col span={12}>{name}</Col>
                        </Row>
                    </AutoComplete.Option>
                ))}
            </AutoComplete>
            <br/><br/>
            <Input
                placeholder="Enter a price"
                type="number"
                value={price!!}
                addonBefore="Price"
                onChange={handlePriceChange}
            />
            <br/>
            {shortTermEpsGrowths.map(({date, eps}) => (
                <React.Fragment key={date}>
                    <br/>
                    <Space>
                        <DatePicker
                            value={moment(date)}
                            onChange={newValue => handleEpsDateUpdate(date, newValue?.format("YYYY-MM-DD"))}
                        />
                        <InputNumber
                            value={eps!!}
                            onChange={newValue => handleEpsUpdate(date, newValue)}
                        />
                    </Space>
                    <MinusCircleOutlined
                        style={{margin: '0 8px'}}
                        onClick={() => removeShortTermEpsGrowth(date)}
                    />
                    <br/>
                </React.Fragment>
            ))}
            <br/>
            <Button block type="dashed" onClick={addShortTermEpsGrowth}>
                <PlusOutlined/> Add EPS Estimate
            </Button>
            <br/><br/>
            <Input
                addonBefore="Long Term Growth"
                type="number"
                placeholder="Enter a LT Estimate"
                value={longTermGrowth}
                onChange={handleLongTermGrowthUpdate}
            />
            <br/><br/>
            <Button onClick={submit}>Submit</Button>
        </Card>
    );

}

function plusOneYear(date: string | undefined): string {
    return moment(date)
        .add(1, "year")
        .format("YYYY-MM-DD");
}
