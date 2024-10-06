import React, { useEffect, useRef, useState } from 'react';
import { Layout, Menu, Button, theme, Modal, Upload, message, Popconfirm, Input, Tooltip } from 'antd';
import { DeleteOutlined,  UploadOutlined, PhoneOutlined, FileOutlined, PlusOutlined, MenuFoldOutlined, MenuUnfoldOutlined, LogoutOutlined, EditOutlined } from '@ant-design/icons';
import { useAuthContext } from '../context/AuthContext';
import { Link, useNavigate, useParams } from "react-router-dom";
import type { MenuProps } from 'antd';
import DocumentService from '../services/document.service';
import ChatService from '../services/chat.service';
import { Document } from '../types/Document';
import { Chat } from '../types/Chat';

const { Header, Content, Footer, Sider } = Layout;

const LayoutWithSider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { logout } = useAuthContext();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState<boolean>(false);
  const {token: { colorBgContainer, borderRadiusLG }} = theme.useToken();
  const [loading, setLoading] = useState(false);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [chats, setChats] = useState<Chat[]>([]);
  const [chatTitle, setChatTitle] = useState<string>('');
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [arquivosASeremSalvos, setArquivosASeremSalvos] = useState<any[]>([]);
  const [modalType, setModalType] = useState(1);
  const inputRef:any = useRef(null);

  const [isEditing, setIsEditing] = useState(false); // Estado para controle do modal de edição
  const [editingChat, setEditingChat] = useState<any>(null); // Estado para guardar o chat que está sendo editado
  const [newTitle, setNewTitle] = useState(''); // Estado para o novo título do chat

  // Função para abrir o modal de edição
  const openEditModal = (chat: any) => {
    setEditingChat(chat);  // Guardar o chat que está sendo editado
    setNewTitle(chat.title); // Preencher o input com o nome atual
    setIsEditing(true); // Mostrar o modal
  };

  // Função para confirmar a edição do chat
const handleEdit = () => {
  if (editingChat) {
    ChatService.updateTitle(editingChat.id, newTitle)
      .then(_ => {
        message.success('Título alterado com sucesso!');
        const updatedChats = chats.map((chat: Chat) => {
          if (chat.id === editingChat.id) {
            return { ...chat, title: newTitle };
          }
          return chat;
        });
        setChats(updatedChats);
      })
      .catch(error => {
        message.error("Erro ao editar título: " + error.response?.data || error.message);
      })
      .finally(() => setIsEditing(false));
  }
};

  
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
      message.error("Erro ao buscar documentos: " + error.response.data);
    });
  }

  function buscarMeusChats(){
    ChatService.myChats()
    .then(response => {
      setChats(response);
    })
    .catch(error => {
      message.error("Erro ao buscar chats: " + error.response.data);
    });
  }

  function processarDocumento(docId:number){
    setLoading(true);
    ChatService.processDocumentById(docId)
      .then(_ => {
        message.success('Documento processado com sucesso!');
        const updatedDocuments = documents.map((doc: Document) => 
          doc.id === docId ? { ...doc, processed: true } : doc
        );
        setDocuments(updatedDocuments);
      })
      .catch(error => {
        message.error("Erro ao processar documento: " + error.response.data);
      })
      .finally(() => 
        setLoading(false)
      )
  }

  const excluirDocumento = (docId: number) => {
    setLoading(true);
    DocumentService.deleteDocumentById(docId)
      .then(_ => {
        message.success('Documento excluído com sucesso!');
        setDocuments(documents.filter((doc: Document) => doc.id !== docId));
        const { documentId } = useParams<{ documentId: string }>();
        if(Number(documentId) == docId) navigate("/home");
      })
      .catch(error => {
        message.error("Erro ao excluir documento: " + error.response.data);
      })
      .finally(() => 
        setLoading(false)
      )
  };

  const excluirChat = (chatRemoverId: number) => {
    setLoading(true);
    ChatService.deleteChatById(chatRemoverId)
      .then(_ => {
        message.success('Chat excluído com sucesso!');
        setChats(chats.filter((chat: Chat) => chat.id !== chatRemoverId));
        navigate("/home");
      })
      .catch(_ => {
        message.error('Erro ao excluir o chat.');
      })
      .finally(() => 
        setLoading(false)
      )
  };

  const showModal = (tipo:number) => {
    setModalType(tipo);
    setIsModalVisible(true)
    setTimeout(() => {
      if(tipo == 2 && inputRef.current){
        inputRef.current.focus();
      }
    }, 350)
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
      setLoading(true);
      DocumentService.saveDocuments(formData)
      .then(response => {
        setDocuments([...documents, ...response]);
        setArquivosASeremSalvos([]);
      })
      .catch(error => {
        message.error("Erro ao salvar documentos: " + error.response.data);
      })
      .finally(() => 
        setLoading(false)
      )
    } else if(tipo == 2){
      setLoading(true);
      ChatService.newChat(chatTitle)
      .then(response => {
        message.success('Chat criado com sucesso!');
        const updatedChats = [...chats, response]
        setChats(updatedChats);
        navigate('/chat/'+response.id);
        setChatTitle("");
      })
      .catch(err => {
        message.error("Erro ao criar chat: " + err.response.data);
      })
      .finally(() => 
        setLoading(false)
      )
    }
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setArquivosASeremSalvos([]);
    setIsModalVisible(false)
  };

  const beforeUpload = (file: File) => {
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
        ...documents.map((doc:Document) => ({
          label: (
            <Tooltip 
            title={
              !doc.processed ? (
                <span>
                  Este documento ainda não foi processado.
                  <Button 
                    type="primary" 
                    size="small" 
                    onClick={(e) => {
                      e.stopPropagation();
                      processarDocumento(doc.id);
                    }} 
                    loading={loading} disabled={loading}
                    style={{ marginLeft: '8px', marginTop: '4px' }}
                  >
                    Processar
                  </Button>
                </span>
              ) : ""
            } 
            placement="top"
          >
            <span style={{ display: 'flex', justifyContent: 'space-around', color: 'black' }}>
              {doc.name}
              <Popconfirm
                title="Excluir documento"
                description="Tem certeza que você deseja excluir este documento?"
                onConfirm={(e) => {
                  e?.stopPropagation();
                  excluirDocumento(doc.id);
                }} 
                okText="EXCLUIR"
                cancelText="CANCELAR"
              >
                <Button type="text" icon={<DeleteOutlined />} style={{ marginLeft: '8px', marginTop: '4px', color: 'black' }} onClick={(e) => e.stopPropagation()} loading={loading} disabled={loading}/>
              </Popconfirm>
            </span>
          </Tooltip>
          ),
          key: doc.id,
          style: {whiteSpace: 'normal', height: 'auto', border: `1px solid ${doc.processed ? 'green' : 'red'}`, paddingLeft: '0px', paddingRight: '0px', backgroundColor: '#e6f3ff'},
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
        ...chats.map((chat:Chat) => ({
          label: (
            <span style={{ display: 'flex', justifyContent: 'space-around', color: 'black' }}>
              {chat.title}
              <div>
                <Button 
                  type="text" 
                  icon={<EditOutlined />} 
                  style={{ marginLeft: '8px', marginTop: '4px' }} 
                  onClick={() => openEditModal(chat)}
                  disabled={loading}
                />
                <Popconfirm
                  title="Excluir chat"
                  description="Tem certeza que você deseja excluir este chat?"
                  onConfirm={() => excluirChat(chat.id)} 
                  okText="EXCLUIR"
                  cancelText="CANCELAR"
                >
                  <Button type='text' icon={<DeleteOutlined />} style={{marginTop: '4px' }} loading={loading} disabled={loading}></Button>
                </Popconfirm>
              </div>
            </span>
          ),
          key: chat.id,
          style: {whiteSpace: 'normal', height: 'auto', border: `1px solid white`, paddingLeft: '0px', paddingRight: '0px', backgroundColor: '#e6f3ff'},
          onClick: () => navigate(`/chat/${chat.id}`)
        })),
      ],
    }
  ];

  return (
    <Layout>
      <Modal
        title="Editar nome do chat"
        visible={isEditing}
        onOk={handleEdit}
        onCancel={() => setIsEditing(false)}
        okText="Salvar"
        cancelText="Cancelar"
      >
        <Input 
          value={newTitle} 
          onChange={(e) => setNewTitle(e.target.value)} 
          placeholder="Novo nome do chat"
          onPressEnter={(e) => {
            e.preventDefault();
            handleEdit();
          }}
        />
      </Modal>
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
              Chat IA
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
           {React.Children.map(children, child => 
            React.isValidElement(child) ? React.cloneElement(child as React.ReactElement<any>, { documents }) : child
          )}
        </Content>
        <Footer style={{ textAlign: 'center' }}>ChatIA ©2024</Footer>
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
            onPressEnter={(e) => {
              e.preventDefault();
              handleOk(modalType);
            }}
            ref={inputRef}
            />
        </>
        }
        
      </Modal>

    </Layout>
  );
};

export default LayoutWithSider;
