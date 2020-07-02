import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import 'antd/dist/antd.dark.css';
import * as serviceWorker from './serviceWorker';
import {BrowserRouter} from "react-router-dom";
import axios, {AxiosError} from 'axios';
import {notification} from "antd";

axios.interceptors.response.use(undefined, (error) => {
    const {response} = error as AxiosError<ApiError>;
    notification.error({message: 'Server Error', description: response?.data?.message});
    return Promise.reject(error);
});

ReactDOM.render(
    <React.StrictMode>
        <BrowserRouter>
            <App/>
        </BrowserRouter>
    </React.StrictMode>,
    document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
