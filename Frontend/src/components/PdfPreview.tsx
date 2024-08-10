import React from 'react';
import { Worker, Viewer } from '@react-pdf-viewer/core';
import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/default-layout/lib/styles/index.css';

interface PdfPreviewProps {
    pdfUrl: string;
}

const PdfPreview: React.FC<PdfPreviewProps> = ({ pdfUrl }) => {
    return (
        <div style={{ height: '100%' }}>
            <h1>{pdfUrl}</h1>
            {/* <Worker workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}>
                <Viewer fileUrl={pdfUrl} />
            </Worker> */}
        </div>
    );
};

export default PdfPreview;
