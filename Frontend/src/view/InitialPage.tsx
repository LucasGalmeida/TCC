// src/components/InitialPage.js
import { Button, Layout } from 'antd';
import { Content, Header } from 'antd/es/layout/layout';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import CourseCarousel from '../components/Carousel';

function InitialPage() {
    const [selectedCourseId, setSelectedCourseId] = useState('');
  const [documentId, setDocumentId] = useState('');
  const [question, setQuestion] = useState('');
  const navigate = useNavigate();

  const handleLoginClick = () => {
    navigate('/login');
  };

  const handleSubmit = (e:any) => {
    e.preventDefault();
    console.log(e)
  };

  const handleSelectCourse = (courseId:any) => {
    setSelectedCourseId(courseId);
    alert(`Curso ${courseId} selecionado!`);
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
            <h2>Selecione um curso</h2>
            <CourseCarousel onSelectCourse={handleSelectCourse} />
            <br />
            <Button type="primary" onClick={handleSubmit}>
            Continuar
            </Button>
      </Content>
    </Layout>
  );
}

export default InitialPage;
