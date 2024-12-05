// src/components/InitialPage.js
import { Button, Layout, Modal } from 'antd';
import { Content, Header } from 'antd/es/layout/layout';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import CourseCarousel from '../components/Carousel';
import TextArea from 'antd/es/input/TextArea';
import ChatService from '../services/chat.service';

function InitialPage() {
  const [selectedCourse, setSelectedCourse] = useState<any>(null);
  const [isModalVisible, setIsModalVisible] = useState<boolean>(false);
  const [chatHistory, setChatHistory] = useState<any>([]);
  const [newMessage, setNewMessage] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const chatContainerRef:any = React.createRef();
  const textAreaRef:any = React.createRef();
  const navigate = useNavigate();

  const handleLoginClick = () => {
    navigate('/login');
  };

  const handleSelectCourse = (course:any) => {
    setSelectedCourse(course);
    setIsModalVisible(true);
  };

  const handleSendMessage = () => {
    if (newMessage.trim() === '') return;
    if (selectedCourse == null) return;
    setLoading(true);
    const newChat = {
      message: newMessage,
      date: new Date(),
      type: 'USER_REQUEST',
    };
    setChatHistory([...chatHistory, newChat]);
    setNewMessage('');

    try {
      const eventSource = ChatService.chamadaStream(newMessage, selectedCourse.id)
  
      eventSource.onmessage = (event) => {
        setChatHistory((prevChatHistory: any) => {
          const updatedChatHistory = [...prevChatHistory];
          if (updatedChatHistory.length > 0 && updatedChatHistory[updatedChatHistory.length - 1].type === 'AI_RESPONSE') {
            updatedChatHistory[updatedChatHistory.length - 1].message += event.data;
          } else {
            updatedChatHistory.push({
              message: event.data,
              date: new Date(),
              type: 'AI_RESPONSE',
            });
          }
          return updatedChatHistory;
        });
      };
  
      eventSource.onerror = (error) => {
        console.error('Erro na conexão SSE:', error);
        eventSource.close();
        // message.error('Erro ao processar o stream de SSE');
        setLoading(false);
      };
  
      eventSource.onopen = () => {
        console.log('Conexão SSE aberta');
      };
    } catch (error) {
      console.error('Erro ao iniciar EventSource:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleModalClose = () => {
    setIsModalVisible(false);
    setLoading(false);
    setChatHistory([]);
  };

  return (
    <Layout>
        <Header style={{ backgroundColor: '#001529', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h1 style={{ color: '#fff', margin: 0 }}>Meu professor responde</h1>
            <Button type="primary" onClick={handleLoginClick}>
            Login
            </Button>
        </Header>
        <Content style={{ padding: '20px' }}>
          <div style={{ border: '1px solid black', borderRadius: '15px' }}>
            <h2 style={{textAlign: 'center'}}>Selecione um curso</h2>
            <CourseCarousel onSelectCourse={handleSelectCourse} />
          </div>
          <Modal
          title={`Chat do Curso: ${selectedCourse?.name}`}
          visible={isModalVisible}
          onCancel={handleModalClose}
          footer={null}
          width="80%"
        >
          <div style={{ display: 'flex', flexDirection: 'column', height: '500px' }}>
            <div style={{ flex: 1, overflowY: 'auto', padding: '16px' }} ref={chatContainerRef}>
              {chatHistory.map((chat:any, index:any) => (
                <div
                  key={index}
                  style={{
                    display: 'flex',
                    justifyContent: chat.type === 'USER_REQUEST' ? 'flex-end' : 'flex-start',
                    marginBottom: '10px',
                  }}
                >
                  <div
                    style={{
                      maxWidth: '60%',
                      backgroundColor: chat.type === 'USER_REQUEST' ? '#d1e7dd' : '#f8d7da',
                      padding: '10px',
                      borderRadius: '10px',
                      whiteSpace: 'pre-wrap',
                    }}
                  >
                    <p style={{ margin: 0 }}>{chat.message}</p>
                    <small
                      style={{
                        display: 'block',
                        textAlign: chat.type === 'USER_REQUEST' ? 'right' : 'left',
                        marginTop: '5px',
                        color: '#6c757d',
                      }}
                    >
                      {new Date(chat.date).toLocaleString()}
                    </small>
                  </div>
                </div>
              ))}
            </div>
            <div style={{ padding: '10px', borderTop: '1px solid #f0f0f0' }}>
              <TextArea
                rows={2}
                autoSize
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                placeholder="Digite sua mensagem..."
                disabled={loading}
                onPressEnter={(e) => {
                  e.preventDefault();
                  handleSendMessage();
                }}
                ref={textAreaRef}
              />
              <Button
                type="primary"
                onClick={handleSendMessage}
                style={{ marginTop: '10px', float: 'right' }}
                loading={loading}
                disabled={loading || newMessage.trim() === ''}
              >
                Enviar
              </Button>
            </div>
          </div>
        </Modal>
      </Content>
    </Layout>
  );
}

export default InitialPage;
