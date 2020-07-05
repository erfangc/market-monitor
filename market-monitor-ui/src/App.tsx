import React from 'react';
import {Layout, Menu} from "antd";
import {Route, useLocation} from "react-router";
import {Link} from "react-router-dom";
import {CompanyAnalysisView} from "./views/CompanyAnalysis/CompanyAnalysisView";
import {SummaryAnalysisView} from "./views/SummaryAnalysis/SummaryAnalysisView";


function App() {
    const {pathname} = useLocation();

    const menuItems = [
        {
            key: '/summary-analysis',
            text: 'Summary Analysis'
        },
        {
            key: '/company-analysis',
            text: 'Company Analysis'
        }
    ];
    const selectedKeys = menuItems.filter(({key}) => pathname.startsWith(key)).map(({key}) => key);
    return (
        <Layout>
            <Layout.Header>
                <Menu theme="dark" mode="horizontal" selectedKeys={selectedKeys}>
                    {menuItems.map(({key, text}) => (
                        <Menu.Item key={key}>
                            <Link to={key}>{text}</Link>
                        </Menu.Item>
                    ))}
                </Menu>
            </Layout.Header>
            <Layout.Content style={{padding: '24px'}}>
                <Route path="/summary-analysis" component={SummaryAnalysisView} exact/>
                <Route path="/company-analysis" component={CompanyAnalysisView} exact/>
                <Route path="/company-analysis/:ticker" component={CompanyAnalysisView}/>
            </Layout.Content>
        </Layout>
    );
}

export default App;
