import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import ChatService from '../services/chat.service';
import TextArea from 'antd/es/input/TextArea';
import { Button, Spin } from 'antd';
import { ChatHistoryEnum } from '../types/ChatHistoryEnum';

const ChatView: React.FC = () => {
  const { chatId } = useParams<{ chatId: string }>();
  const [chatHistory, setChatHistory] = useState<any[]>([]);
  const [newMessage, setNewMessage] = useState<string>('');
  const [isResponding, setIsResponding] = useState<boolean>(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (chatId) {
      buscarHistoricoPorChatId();
    }
  }, [chatId]);

  function buscarHistoricoPorChatId() {
    ChatService.getChatHistoryById(chatId!)
      .then(response => {
        setChatHistory(response);
      })
      .catch(error => {
        console.error("Erro ao buscar histórico por chat id: ", error.response.data);
      });
  }

  function handleSendMessage() {
    if (newMessage.trim() !== '') {
      const userMessage = {
        type: ChatHistoryEnum.USER_REQUEST,
        date: new Date().toISOString(),
        message: newMessage,
      };

      setChatHistory([...chatHistory, userMessage]);
      setNewMessage('');
      setIsResponding(true);
      setLoading(true);
      ChatService.chatEmbedding(chatId, userMessage.message)
        .then(response => {
          setChatHistory(prevHistory => [...prevHistory, response]);
        })
        .catch(error => {
          console.error("Erro ao enviar request para o backend: ", error.response.data);
        })
        .finally(() => {
          setIsResponding(false);
          setLoading(false);
        });
    }
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div style={{ flex: 1, overflowY: 'auto', padding: '16px' }}>
        {chatHistory.map((chat:any, index:any) => (
          <div 
            key={index} 
            style={{
              display: 'flex',
              justifyContent: chat.type === ChatHistoryEnum.USER_REQUEST ? 'flex-end' : 'flex-start',
              marginBottom: '10px'
            }}
          >
            <div 
              style={{
                maxWidth: '60%',
                backgroundColor: chat.type === ChatHistoryEnum.USER_REQUEST ? '#d1e7dd' : '#f8d7da',
                padding: '10px',
                borderRadius: '10px',
                whiteSpace: 'pre-wrap',
              }}
            >
              <p style={{ margin: 0 }}>{chat.message}</p>
              <small style={{ display: 'block', textAlign: chat.type === ChatHistoryEnum.USER_REQUEST ? 'right' : 'left', marginTop: '5px', color: '#6c757d' }}>
                {new Date(chat.date).toLocaleString()}
              </small>
            </div>
          </div>
        ))}
        {isResponding && (
          <div style={{ display: 'flex', justifyContent: 'flex-start', marginBottom: '10px' }}>
            <Spin tip="Chat PDF está digitando..." >
              <div></div>
            </Spin>
          </div>
        )}
      </div>
      <div style={{ padding: '10px', borderTop: '1px solid #f0f0f0' }}>
        <TextArea
          rows={2}
          autoSize 
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Digite sua mensagem..."
          onPressEnter={(e) => {
            e.preventDefault();
            handleSendMessage();
          }}
        />
        <Button type="primary" onClick={handleSendMessage} style={{ marginTop: '10px', float: 'right' }} loading={loading} disabled={loading}>
          Enviar
        </Button>
      </div>
    </div>
  );
};

export default ChatView;
