import React, {ChangeEvent, useEffect, useState} from 'react';
import {AutoComplete, Button, Card, DatePicker, Input, InputNumber, Space} from "antd";
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
            growthRate: null,
            date: now.add(1, 'year').format("YYYY-MM-DD")
        },
        {
            eps: 0.0,
            growthRate: null,
            date: now.add(2, 'year').format("YYYY-MM-DD")
        }
    ],
    ticker: '',
    price: 0.0
};

export function Inputs(props: Props) {

    const [request, setRequest] = useState<CompanyReturnAnalysisRequest>(initialState);
    const [tickerOptions, setTickerOptions] = useState<{ value: string }[]>([]);
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

    // eslint-disable-next-line
    useEffect(() => {
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

    function handleTickerChange(ticker: string) {
        history.push(`/expected-return/${ticker}`);
    }

    async function handleTickerSearch(term: string) {
        const {data} = await axios.get<Ticker[]>('/api/tickers/_match', {params: {term}});
        setTickerOptions(data.map(({ticker}) => ({value: ticker})));
    }

    function removeShortTermEpsGrowth(date: string) {
        setRequest({
            ...request,
            shortTermEpsGrowths: shortTermEpsGrowths.filter(epsGrowth => epsGrowth.date !== date)
        });
    }

    function addShortTermEpsGrowth() {
        const lastEpsDate = shortTermEpsGrowths.length !== 0
            ? shortTermEpsGrowths[shortTermEpsGrowths.length - 1].date
            : undefined;

        const shortTermEpsGrowth: ShortTermEpsGrowth = {
            date: plusOneYear(lastEpsDate),
            eps: 1,
            growthRate: null
        };

        setRequest({
            ...request,
            shortTermEpsGrowths: [
                ...shortTermEpsGrowths,
                shortTermEpsGrowth
            ]
        });
    }

    function handleLtGrowthChange({currentTarget: {value}}: ChangeEvent<HTMLInputElement>) {
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

    return (
        <Card title="Inputs">
            <DatePicker
                value={moment(date)}
                onChange={newValue => handleDateChange(newValue?.format("YYYY-MM-DD"))}
            />
            <br/><br/>
            <AutoComplete
                options={tickerOptions}
                style={{width: 200}}
                onSelect={handleTickerChange}
                onSearch={handleTickerSearch}
                placeholder="Search for a ticker"
            />
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
            <Button type="dashed" onClick={addShortTermEpsGrowth} block>
                <PlusOutlined/> Add EPS Estimate
            </Button>
            <br/><br/>
            <Input
                addonBefore="Long Term Growth"
                type="number"
                placeholder="Enter a LT Estimate"
                value={longTermGrowth}
                onChange={handleLtGrowthChange}
            />
            <br/><br/>
            <Button onClick={submit}>
                Submit
            </Button>
        </Card>
    );

}

function plusOneYear(date: string | undefined): string {
    return moment(date)
        .add(1, "year")
        .format("YYYY-MM-DD");
}
