import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Login from './view/Login';
import Register from './view/Register';
import { Layout } from 'antd';
import { Content, Footer, Header } from 'antd/es/layout/layout';
import Chat from './view/Chat';

function App() {
  

  return (
    <>
      <Layout className="layout">
        <Header>
          <div className="logo" />
        </Header>
        <Content>
          <div className="site-layout-content">
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/chat" element={<Chat />} />
            </Routes>
          </BrowserRouter>
          </div>
        </Content>
        <Footer style={{ textAlign: 'center' }}>Ant Design ©2024 Created by Ant UED</Footer>
      </Layout>


      
       
    </>
  )
}

export default App
