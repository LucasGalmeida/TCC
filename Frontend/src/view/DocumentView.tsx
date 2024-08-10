import React, { useEffect, useState } from 'react';
import PdfPreview from '../components/PdfPreview';
import DocumentService from '../services/document.service';
import { useParams } from 'react-router-dom';


const DocumentView: React.FC = () => {

  const { documentId } = useParams<{ documentId: string }>();
  const [document, setDocument] = useState<any>(null);

  useEffect(() => {
    if(documentId){
      buscarDocumentoPorId();
    }
  }, [])

  function buscarDocumentoPorId(){
    DocumentService.getDocumentById(documentId!)
    .then(response => {
      setDocument(response);
    })
    .catch(error => {
      console.error("Erro ao buscar documento por id: ", error.response.data);
    });
  }

  return (
    document 
    ?
    <div>
      <h1>PDF Preview</h1>
      <PdfPreview pdfUrl={document.url} />
    </div>
    :
    <>
    <span>Carregando...</span>
    </>
  );
};

export default DocumentView;
