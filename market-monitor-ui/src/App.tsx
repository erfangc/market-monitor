import React from 'react';
import {Layout, Menu} from "antd";
import {Route, useLocation, Redirect} from "react-router";
import {Link} from "react-router-dom";
import {MarketOverview} from "./views/MarketOverview/MarketOverview";


function App() {
    const {pathname} = useLocation();
    return (
        <Layout>
            <Layout.Header>
                <Menu theme="dark" mode="horizontal" selectedKeys={[pathname]}>
                    <Menu.Item key="/market-overview">
                        <Link to="/market-overview">Market Overview</Link>
                    </Menu.Item>
                    <Menu.Item key="/expected-return">
                        <Link to="/expected-return">Expected Return</Link>
                    </Menu.Item>
                    <Menu.Item key="/relative-value">
                        <Link to="/relative-value">Relative Value</Link>
                    </Menu.Item>
                </Menu>
            </Layout.Header>
            <Layout.Content style={{ padding: '24px' }}>
                <Route path="/market-overview" component={MarketOverview}/>
                <Redirect path="*" to="/market-overview"/>
            </Layout.Content>
        </Layout>
    );
}

export default App;
