import React, { useEffect, useState } from 'react';
import { Layout, Menu, Button, theme, Modal, Upload, message, Popconfirm, Input } from 'antd';
import { DeleteOutlined, CheckOutlined, CloseOutlined, UploadOutlined, PhoneOutlined, FileOutlined, PlusOutlined, MenuFoldOutlined, MenuUnfoldOutlined, LogoutOutlined } from '@ant-design/icons';
import { useAuthContext } from '../context/AuthContext';
import { Link, useNavigate, useParams } from "react-router-dom";
import type { MenuProps } from 'antd';
import DocumentService from '../services/document.service';
import ChatService from '../services/chat.service';

const { Header, Content, Footer, Sider } = Layout;

const LayoutWithSider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { logout } = useAuthContext();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState<boolean>(false);
  const {token: { colorBgContainer, borderRadiusLG }} = theme.useToken();

  const [documents, setDocuments] = useState<any>([]);
  const [chats, setChats] = useState<any>([]);
  const [chatTitle, setChatTitle] = useState<string>('');

  useEffect(() => {
    buscarMeusDocumentos();
    buscarMeusChats();
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

  function buscarMeusChats(){
    ChatService.myChats()
    .then(response => {
      setChats(response);
    })
    .catch(error => {
      console.error("Erro ao buscar chats: ", error.response.data);
    });
  }

  function processarDocumento(docId:number){
    ChatService.processDocumentById(docId)
      .then(_ => {
        message.success('Documento processado com sucesso!');
        const updatedDocuments = documents.map((doc: any) => 
          doc.id === docId ? { ...doc, processed: true } : doc
        );
        setDocuments(updatedDocuments);
      })
      .catch(_ => {
        message.error('Erro ao processar o documento.');
      });
  }

  const excluirDocumento = (docId: string) => {
    DocumentService.deleteDocumentById(docId)
      .then(_ => {
        message.success('Documento excluído com sucesso!');
        setDocuments(documents.filter((doc: any) => doc.id !== docId));
        const { documentId } = useParams<{ documentId: string }>();
        if(documentId == docId) navigate("/home");
      })
      .catch(_ => {
        message.error('Erro ao excluir o documento.');
      });
  };

  const excluirChat = (chatId: string) => {
    ChatService.deleteChatById(chatId)
      .then(_ => {
        message.success('Chat excluído com sucesso!');
        setChats(chats.filter((chat: any) => chat.id !== chatId));
        const { chatId } = useParams<{ chatId: string }>();
        if(chatId == chatId) navigate("/home");
      })
      .catch(_ => {
        message.error('Erro ao excluir o chat.');
      });
  };

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [arquivosASeremSalvos, setArquivosASeremSalvos] = useState<any[]>([]);
  const [modalType, setModalType] = useState(1);
  const showModal = (tipo:number) => {
    setModalType(tipo);
    setIsModalVisible(true)
  }

  const handleOk = (tipo:number) => {
    if(tipo == 1){
      if (arquivosASeremSalvos.length === 0) {
        message.error('Nenhum arquivo selecionado!');
        return;
      }
      const formData = new FormData();
      arquivosASeremSalvos.forEach(file => {
        formData.append('files', file);
      });
      DocumentService.saveDocuments(formData)
      .then(response => {
        setDocuments([...documents, ...response]);
        setArquivosASeremSalvos([]);
      })
      .catch(error => {
        console.error("Erro ao salvar documentos: ", error.response.data);
      });
    } else if(tipo == 2){
      ChatService.newChat(chatTitle)
      .then(response => {
        message.success('Chat criado com sucesso!');
        const updatedChats = [...chats, response]
        setChats(updatedChats);
      })
      .catch(_ => {
        message.error('Erro ao criar chat.');
      });
    }
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setArquivosASeremSalvos([]);
    setIsModalVisible(false)
  };

  const beforeUpload = (file: any) => {
    const isPDF = file.type === 'application/pdf';
    if (!isPDF) {
      message.error('Você pode apenas fazer upload de arquivos PDF!');
    }
    return isPDF;
  };

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
              onClick={() => showModal(1)} 
              style={{ color: "white", width: "100%", textAlign: "left"}}
            >
              Adicionar Documento
            </Button>
          ),
          style: { paddingLeft: '0px'},
          key: "addDocument",
        },
        ...documents.map((doc:any) => ({
          label: (
            <span style={{ display: 'flex', alignItems: 'center' }}>
              {doc.processed ? (
                <CheckOutlined style={{ color: 'green', marginRight: 8, marginLeft: 8 }} />
              ) : (
                <Popconfirm
                  title="Processar documento"
                  description="Tem certeza que você deseja iniciar o processamento desse documento (esse processo pode levar vários minutos)"
                  onConfirm={() => processarDocumento(doc.id)}
                  okText="PROCESSAR"
                  cancelText="CANCELAR"
                >
                  <Button danger icon={<CloseOutlined style={{ color: 'red' }} />} style={{padding: '6px', marginRight: '8px', marginLeft: '8px'}}></Button>
                </Popconfirm>
              )}
              {doc.name}
              <Popconfirm
                title="Excluir documento"
                description="Tem certeza que você deseja excluir este documento?"
                onConfirm={() => excluirDocumento(doc.id)} 
                okText="EXCLUIR"
                cancelText="CANCELAR"
              >
                <Button danger icon={<DeleteOutlined />} style={{ marginLeft: '8px' }}></Button>
              </Popconfirm>
            </span>
          ),
          key: doc.id,
          style: {whiteSpace: 'normal', height: 'auto', border: '1px solid white', paddingLeft: '0px'},
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
              onClick={() => showModal(2)} 
              style={{ color: "white", width: "100%", textAlign: "left" }}
            >
              Novo chat
            </Button>
          ),
          style: { paddingLeft: '0px'},
          key: "addChat",
        },
        ...chats.map((chat:any) => ({
          label: (
            <span style={{ display: 'flex', alignItems: 'center' }}>
              {chat.title}
              <Popconfirm
                title="Excluir chat"
                description="Tem certeza que você deseja excluir este chat?"
                onConfirm={() => excluirChat(chat.id)} 
                okText="EXCLUIR"
                cancelText="CANCELAR"
              >
                <Button danger icon={<DeleteOutlined />} style={{ marginLeft: '8px' }}></Button>
              </Popconfirm>
            </span>
          ),
          key: chat.id,
          onClick: () => navigate(`/chat/${chat.id}`)
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
          <div>
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              style={{ fontSize: '16px', width: 64, height: 64, color: "white" }}
            />
            <Link to="/home" style={{ color: 'white', fontSize: '24px', fontWeight: 'bold', marginLeft: '16px' }}>
              Home
            </Link>
          </div>
          <Popconfirm
              title="Sair do sistema"
              description="Tem certeza que você deseja sair do sistema?"
              onConfirm={() => {
                logout();
                navigate('/');
              }} 
              okText="SAIR"
              cancelText="CANCELAR"
            >
              <Button type="text" icon={<LogoutOutlined />} style={{ fontSize: '16px', width: 64, height: 64, color: "white" }}
            />
            </Popconfirm>
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
        <Footer style={{ textAlign: 'center' }}>ChatAI ©2024</Footer>
      </Layout>


      <Modal title={modalType == 1 ? 'Adicionar Documento' : 'Iniciar novo chat'} open={isModalVisible} onOk={() => handleOk(modalType)} onCancel={handleCancel}>
        {
        modalType == 1 
        ?
        <Upload
          name="file"
          customRequest={({ file }) => {
            setArquivosASeremSalvos([file]);
          }}
          beforeUpload={beforeUpload}
          fileList={arquivosASeremSalvos}
          showUploadList={{
            showPreviewIcon: true,
            showRemoveIcon: true,
            showDownloadIcon: false
          }}
          accept=".pdf"
        >
          <Button icon={<UploadOutlined />}>Clique para anexar o PDF</Button>
        </Upload>
        :
        <>
          <Input 
            placeholder="Digite o título do chat (máx. 50 caracteres)" 
            maxLength={50}
            value={chatTitle}
            onChange={(e) => setChatTitle(e.target.value)}
            />
        </>
        }
        
      </Modal>

    </Layout>
  );
};

export default LayoutWithSider;
