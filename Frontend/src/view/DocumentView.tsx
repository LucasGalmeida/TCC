import React, { useEffect, useState } from 'react';
import PdfPreview from '../components/PdfPreview';
import DocumentService from '../services/document.service';
import { useParams } from 'react-router-dom';


const DocumentView: React.FC = () => {

  const { documentId } = useParams<{ documentId: string }>();
  const [pdfUrl, setPdfUrl] = useState<any>(null);

  useEffect(() => {
    if(documentId){
      buscarDocumentoPorId();
    }
  }, [documentId])

  function buscarDocumentoPorId(){
    DocumentService.getResourceById(documentId!)
    .then(response => {
      const blob = new Blob([response], { type: 'application/pdf' });
      const url = URL.createObjectURL(blob);
      setPdfUrl(url);
    })
    .catch(error => {
      console.error("Erro ao buscar documento por id: ", error.response.data);
    });
  }

  return (
    pdfUrl 
    ?
    <div>
      <h1>PDF Preview</h1>
      <PdfPreview pdfUrl={pdfUrl} />
      {/* <img src={pdfUrl} /> */}
    </div>
    :
    <>
    <span>Carregando...</span>
    </>
  );
};

export default DocumentView;
