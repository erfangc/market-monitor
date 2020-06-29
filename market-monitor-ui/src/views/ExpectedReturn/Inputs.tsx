import React, {ChangeEvent, FocusEvent, useState} from 'react';
import {Button, Card, DatePicker, Input, InputNumber, Space} from "antd";
import {MinusCircleOutlined, PlusOutlined} from '@ant-design/icons';
import moment from "moment";

interface Props {
    onChange: (request: CompanyReturnAnalysisRequest) => void
}

const initialState: CompanyReturnAnalysisRequest = {
    date: moment().format("YYYY-MM-DD"),
    longTermGrowth: 0.03,
    shortTermEpsGrowths: [
        {
            eps: 27.18,
            growthRate: null,
            date: '2020-12-31'
        },
        {
            eps: 30.5,
            growthRate: null,
            date: '2021-12-31'
        }
    ],
    ticker: 'BLK',
    price: 532.66
};

export function Inputs(props: Props) {

    const [
        request,
        setRequest
    ] = useState<CompanyReturnAnalysisRequest>(initialState);

    const {
        longTermGrowth, ticker, shortTermEpsGrowths, date, price
    } = request;

    function handleDateChange(date?: string) {
        if (!date) {
            return;
        }
        setRequest({...request, date});
    }

    function handlePriceChange({currentTarget: {value}}: ChangeEvent<HTMLInputElement>) {
        setRequest({...request, price: parseFloat(value)})
    }

    function handleTickerChange({currentTarget: {value}}: FocusEvent<HTMLInputElement>) {
        setRequest({...request, ticker: value});
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
            <Input
                placeholder="Enter a price"
                type="number"
                value={price!!}
                addonBefore="Price"
                onChange={handlePriceChange}
            />
            <br/><br/>
            <Input
                placeholder="Enter a ticker"
                value={ticker}
                addonBefore="Ticker"
                onChange={handleTickerChange}
            />
            <br/>
            {shortTermEpsGrowths.map(({date, eps}) => (
                <>
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
                </>
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
