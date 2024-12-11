import React, { useEffect, useState } from 'react';
import PdfPreview from '../components/PdfPreview';
import DocumentService from '../services/document.service';
import { useLocation, useParams } from 'react-router-dom';
import { Button, Form, Input, message } from 'antd';

const DocumentView: React.FC = () => {

  const { documentId } = useParams<{ documentId: string }>();
  const location = useLocation();
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [documentName, setDocumentName] = useState<string>('');
  const [documentDescription, setDocumentDescription] = useState<string>('');

  const document = location.state?.document;

  useEffect(() => {
    if(documentId){
      buscarDocumentoPorId();
    }
    if (document){
      setDocumentName(document.name);
      setDocumentDescription(document.description || '');
    }
  }, [documentId, document])

  function buscarDocumentoPorId(){
    DocumentService.getResourceById(documentId!)
    .then(response => {
      const blob = new Blob([response], { type: 'application/pdf' });
      const url = URL.createObjectURL(blob);
      setPdfUrl(url);
    })
    .catch(error => {
      message.error("Erro ao buscar documento por id: " + error.response.data);
    });
  }

  const handleSave = () => {
    if(documentName.trim() == "") return;
    if(!documentId) return;
    DocumentService.updateDocumentById(parseInt(documentId), documentName, documentDescription).then((_:any) => {
      message.success(`Documento "${documentName}" salvo com sucesso!`);
    })
    .catch(error => {
        message.error("Erro ao salvar documento: " + error.message);
      });
  }

  return (
    <div style={{ padding: '20px' }}>
      <h1 style={{ textAlign: 'center', marginBottom: '32px' }}>Dados documento</h1>
      <Form layout="vertical" style={{ marginTop: '32px' }}>
        <Form.Item label="Nome do Documento">
          <Input
            value={documentName}
            onChange={(e) => setDocumentName(e.target.value)}
            placeholder="Insira o nome do documento"
          />
        </Form.Item>
        <Form.Item label="Descrição do Documento">
          <Input
            value={documentDescription}
            onChange={(e) => setDocumentDescription(e.target.value)}
            placeholder="Insira a descrição do documento"
          />
        </Form.Item>
        <Form.Item>
          <div style={{ textAlign: 'right' }}>
            <Button type="primary" onClick={handleSave}>
              Salvar
            </Button>
          </div>
        </Form.Item>
      </Form>

      <h1 style={{ textAlign: 'center', marginBottom: '32px' }}>Pré visualização do documento</h1>
      {pdfUrl ? (
        <div>
          <PdfPreview pdfUrl={pdfUrl} />
        </div>
      ) : (
        <span>Carregando...</span>
      )}
    </div>
  );
};

export default DocumentView;
