import React, { useEffect, useState } from 'react';
import { Carousel, Card, Button, message } from 'antd';
import { LeftOutlined, RightOutlined } from '@ant-design/icons';
import './Carousel.css'; 
import { Document } from '../types/Document';
import DocumentService from '../services/document.service';

function CourseCarousel({ onSelectCourse }: any) {
  const carouselRef:any = React.createRef();
  const [documents, setDocuments] = useState<Document[]>([]);

  useEffect(() => {
    buscarMeusDocumentos();
  }, [])

  function buscarMeusDocumentos(){
    DocumentService.myDocuments()
    .then(response => {
      setDocuments(response.filter((document:any) => document.processed));
    })
    .catch(error => {
      message.error("Erro ao buscar cursos: " + error.response.data);
    });
  }

  const handlePrev = () => {
    carouselRef.current.prev();
  };

  const handleNext = () => {
    carouselRef.current.next();
  };

  return (
    <div className="carousel-container">
      {documents.length > 0 ? (
        <>
        <Button className="nav-button left" onClick={handlePrev} icon={<LeftOutlined />} />
        <Carousel ref={carouselRef} dotPosition="bottom" autoplay autoplaySpeed={4000}>
          {documents.map((course) => (
            <div key={course.id}>
              <Card
                title={course.name}
                bordered={true}
                style={{ width: 300, margin: '20px auto', textAlign: 'center', borderRadius: '8px', border: '1px solid black' }}
                hoverable
                onClick={() => onSelectCourse(course)}
              >
                <p>{course.description}</p>
              </Card>
            </div>
          ))}
        </Carousel>
        <Button className="nav-button right" onClick={handleNext} icon={<RightOutlined />} />
        </>
      ) : (
        <h3 style={{textAlign: 'center'}}>Nenhum curso cadastrado no momento</h3>
      )}
    </div>
  );
}

export default CourseCarousel;
