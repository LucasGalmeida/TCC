import React, { useEffect, useState } from 'react';
import { Layout, Menu, Button, theme, Modal } from 'antd';
import { PhoneOutlined, FileOutlined, PlusOutlined, MenuFoldOutlined, MenuUnfoldOutlined, LogoutOutlined } from '@ant-design/icons';
import { useAuthContext } from '../context/AuthContext';
import { useNavigate } from "react-router-dom";
import type { MenuProps } from 'antd';
import DocumentService from '../services/document.service';

const { Header, Content, Footer, Sider } = Layout;

const LayoutWithSider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { logout } = useAuthContext();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState<boolean>(false);
  const {token: { colorBgContainer, borderRadiusLG }} = theme.useToken();

  const [documents, setDocuments] = useState<any>([]);
  const [chats, setChats] = useState<any>([]);

  useEffect(() => {
    buscarMeusDocumentos();
  }, [])

  function buscarMeusDocumentos(){
    DocumentService.myDocuments()
    .then(response => {
      setDocuments(response);
    })
    .catch(error => {
      console.error("Erro ao buscar documentos: ", error.response.data);
    });
  }

  const [isModalVisible, setIsModalVisible] = useState(false);
  const showModal = () => setIsModalVisible(true);
  const handleOk = () => {
    setIsModalVisible(false);
  };
  const handleCancel = () => setIsModalVisible(false);

  type MenuItem = Required<MenuProps>['items'][number];
  const menuItems: MenuItem[] = [
    {
      label: "Meus documentos",
      key: "documents",
      icon: <FileOutlined />,
      children: [
        {
          label: (
            <Button 
              type="text" 
              icon={<PlusOutlined />} 
              onClick={showModal} 
              style={{ color: "white", width: "100%", textAlign: "left" }}
            >
              Adicionar Documento
            </Button>
          ),
          key: "addDocument",
        },
        ...documents.map((doc:any) => ({
          label: doc.name,
          key: doc.id,
          style: {whiteSpace: 'normal', height: 'auto'},
          onClick: () => navigate(`/document/${doc.id}`)
        })),
      ],
    },
    {
      label: "Chats",
      key: "chats",
      icon: <PhoneOutlined />,
      children: [
        {
          label: (
            <Button 
              type="text" 
              icon={<PlusOutlined />} 
              onClick={showModal} 
              style={{ color: "white", width: "100%", textAlign: "left" }}
            >
              Novo chat
            </Button>
          ),
          key: "addChat",
        },
        ...chats.map((doc:any) => ({
          label: doc.title,
          key: doc.id,
        })),
      ],
    }
  ];

  return (
    <Layout>
      <Sider width={250} trigger={null} collapsible collapsed={collapsed}>
        <div className="demo-logo-vertical">
          <Menu
            theme="dark"
            mode="inline" items={menuItems}>
          </Menu>
        </div>
      </Sider>
      <Layout>
        <Header style={{ padding: 0, display: 'flex', justifyContent: 'space-between' }}>
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              style={{ fontSize: '16px', width: 64, height: 64, color: "white" }}
            />
            <Button
              type="text"
              icon={<LogoutOutlined />}
              onClick={() => {
                logout();
                navigate('/');
              }}
              style={{ fontSize: '16px', width: 64, height: 64, color: "white" }}
            />
          
        </Header>
        <Content
          style={{
            margin: '24px 16px',
            padding: 24,
            minHeight: 280,
            background: colorBgContainer,
            borderRadius: borderRadiusLG,
          }}
        >
          {children}
        </Content>
        <Footer style={{ textAlign: 'center' }}>Ant Design ©2024 Created by Ant UED</Footer>
      </Layout>


      <Modal title="Adicionar Documento" open={isModalVisible} onOk={handleOk} onCancel={handleCancel}>
        {/* Conteúdo do modal para adicionar documentos */}
      </Modal>

    </Layout>
  );
};

export default LayoutWithSider;
