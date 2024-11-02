import React, { useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import ChatService from '../services/chat.service';
import TextArea from 'antd/es/input/TextArea';
import { Button, Checkbox, Modal, Spin, message } from 'antd';
import { ChatHistoryEnum } from '../types/ChatHistoryEnum';
import { ChatHistory } from '../types/ChatHistory';
import { Key } from 'antd/es/table/interface';
import { Document } from '../types/Document';

interface DocumentListProps {
  documents?: Document[];
}

const ChatView: React.FC<DocumentListProps> = ({ documents }) => {
  const { chatId } = useParams<{ chatId: string }>();
  const [chatHistory, setChatHistory] = useState<ChatHistory[]>([]);
  const [newMessage, setNewMessage] = useState<string>('');
  const [isResponding, setIsResponding] = useState<boolean>(false);
  const [loading, setLoading] = useState(false);
  const [showDocumentModal, setShowDocumentModal] = useState<boolean>(false);
  const [selectedDocuments, setSelectedDocuments] = useState<Set<number>>(new Set());
  const chatContainerRef = useRef<HTMLDivElement | null>(null);

  const textAreaRef:any = useRef(null);

  const handleFocusTextArea = () => {
    setTimeout(() => {
      if (textAreaRef.current) {
        textAreaRef.current.focus();
      }
    }, 350);
  };

  useEffect(() => {
    if (chatId) {
      buscarHistoricoPorChatId();
    }
  }, [chatId]);

  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }
  }, [chatHistory]);

  function buscarHistoricoPorChatId() {
    ChatService.getChatHistoryById(Number(chatId)!)
      .then(response => {
        setChatHistory(response);
      })
      .catch(error => {
        message.error("Erro ao buscar histórico por chat id: " + error.response.data);
      });
  }

  function handleSendMessage() {
    if ((selectedDocuments.size > 0 && newMessage.trim() !== '') || newMessage.trim() !== '') {
      const documentIds = selectedDocuments ? Array.from(selectedDocuments) : [];
      setIsResponding(true);
      setLoading(true);
      ChatService.chatIa(Number(chatId), newMessage, documentIds)
      .then(response => {
        const userMessage: ChatHistory = {
          type: ChatHistoryEnum.USER_REQUEST,
          date: new Date().toISOString(),
          message: newMessage,
        };
        setChatHistory([...chatHistory, userMessage]);
        setNewMessage('');
        setChatHistory(prevHistory => [...prevHistory, response]);
        handleFocusTextArea();
      })
      .catch(error => {
        message.error("Erro ao enviar request para o backend: " + error.response.data);
        deletarUltimaMensagem();
      })
      .finally(() => {
        setIsResponding(false);
        setLoading(false);
        setSelectedDocuments(new Set());
        setShowDocumentModal(false);
      });
    }
  }

  const deletarUltimaMensagem = () => {
    ChatService.deleteLastChatHistoryByChatId(Number(chatId)).then()
  }

  const handleDocumentSelection = (docId: number) => {
    setSelectedDocuments(prevSelection => {
      const newSelection = new Set(prevSelection);
      if (newSelection.has(docId)) {
        newSelection.delete(docId);
      } else {
        newSelection.add(docId);
      }
      return newSelection;
    });
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div style={{ flex: 1, overflowY: 'auto', padding: '16px' }} ref={chatContainerRef}>
        {chatHistory.map((chat: ChatHistory, index: Key) => (
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
            <Spin tip="A IA está digitando..." >
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
          disabled={loading}
          onPressEnter={(e) => {
            e.preventDefault();
            handleSendMessage();
          }}
          ref={textAreaRef}
        />
        <Button type="primary" onClick={handleSendMessage} style={{ marginTop: '10px', float: 'right' }} loading={loading} disabled={loading || newMessage.trim() == ''}>
          Enviar
        </Button>
        <Button type="default" onClick={() => setShowDocumentModal(true)} style={{ marginTop: '10px', float: 'right', marginRight: '10px' }} loading={loading} disabled={loading || newMessage.trim() == ''}>
          Enviar com documentos
        </Button>
      </div>

      <Modal
        title="Selecionar Documentos"
        open={showDocumentModal}
        onOk={handleSendMessage}
        onCancel={() => setShowDocumentModal(false)}
        okText="Enviar"
        cancelText="Cancelar"
        confirmLoading={loading}
      >
        {documents?.filter(doc => doc.processed)?.map((doc) => (
          <div key={doc.id} style={{ marginBottom: '8px' }}>
            <Checkbox checked={selectedDocuments.has(doc.id)} onChange={() => handleDocumentSelection(doc.id)}>
              {doc.name}
            </Checkbox>
          </div>
        ))}
      </Modal>
    </div>
  );
};

export default ChatView;
