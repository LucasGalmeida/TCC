import React from 'react';
import { Carousel, Card, Button } from 'antd';
import { LeftOutlined, RightOutlined } from '@ant-design/icons';
import './Carousel.css'; 

const courses = [
  { id: '1', title: 'Curso de React', description: 'Aprenda React do básico ao avançado' },
  { id: '2', title: 'Curso de JavaScript', description: 'Domine JavaScript e conceitos modernos' },
  { id: '3', title: 'Curso de CSS Avançado', description: 'Estilize suas aplicações com técnicas avançadas de CSS' },
  { id: '4', title: 'Curso de Node.js', description: 'Construa servidores e APIs robustas' },
];

function CourseCarousel({ onSelectCourse }:any) {
  const carouselRef:any = React.createRef();

  const handlePrev = () => {
    carouselRef.current.prev();
  };

  const handleNext = () => {
    carouselRef.current.next();
  };

  return (
    <div className="carousel-container">
      <Button className="nav-button left" onClick={handlePrev} icon={<LeftOutlined />} />
      <Carousel ref={carouselRef} dotPosition="bottom" autoplay>
        {courses.map((course) => (
          <div key={course.id}>
            <Card
              title={course.title}
              bordered={false}
              style={{ width: 300, margin: '20px auto', textAlign: 'center', borderRadius: '8px' }}
              hoverable
              onClick={() => onSelectCourse(course.id)}
            >
              <p>{course.description}</p>
            </Card>
          </div>
        ))}
      </Carousel>
      <Button className="nav-button right" onClick={handleNext} icon={<RightOutlined />} />
    </div>
  );
}

export default CourseCarousel;
