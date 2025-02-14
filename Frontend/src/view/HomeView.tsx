import { Button, Typography, Row, Col, Card } from 'antd';
import { FileAddOutlined, RobotOutlined } from '@ant-design/icons';
import conversarIa from '../assets/conversar-ia.jpg';
import interagirIa from '../assets/interagir-com-ia.jpg';
import upload from '../assets/upload.jpg';

const { Title, Paragraph } = Typography;

const HomeView = ({ showModal }:any) => {
  return (
    <div>
      <section style={{ backgroundColor: '#f0f2f5', textAlign: 'center' }}>
        <Title level={1}>Bem-vindo ao Meu Professor Responde!</Title>
        <Paragraph style={{ fontSize: '18px', maxWidth: '800px', margin: '0 auto' }}>
          Seu assistente de IA está aqui para ajudar você a responder as dúvidas de seus alunos.
        </Paragraph>
        <img 
          src={conversarIa}
          alt="Converse com a IA" 
          style={{ marginTop: '30px', maxWidth: '100%', height: '500px' }}
        />
      </section>

      <section style={{ padding: '50px', backgroundColor: '#ffffff' }}>
        <Row gutter={16} align="middle">
          <Col span={12}>
            <img 
              src={upload}
              alt="Faça upload de seus arquivos" 
              style={{ maxWidth: '100%', height: 'auto' }}
            />
          </Col>
          <Col span={12}>
            <Card
              title="Envie Documentos"
              bordered={false}
              style={{ backgroundColor: '#ffffff' }}
              actions={[
                <Button type="primary" icon={<FileAddOutlined />} onClick={() => showModal(1)}> 
                  Enviar
                </Button>
              ]}
            >
              <Paragraph>
                Faça upload dos seus cursos de maneira rápida e fácil! Com nossa plataforma, você pode enviar seus documentos diretamente para o sistema em poucos cliques. Basta arrastar e soltar seus arquivos ou selecionar a opção de upload para começar. A nossa ferramenta suporta diversos formatos de arquivo, garantindo que você possa carregar tudo o que precisa sem complicações. Após o upload, nossa inteligência artificial analisará seus documentos e fornecerá insights valiosos, ajudando você a extrair o máximo de informações e otimizar seus processos. Experimente agora e veja como é simples integrar seus arquivos ao nosso sistema!
              </Paragraph>
            </Card>
          </Col>
        </Row>
      </section>

      <section style={{ padding: '50px', backgroundColor: '#f0f2f5' }}>
        <Row gutter={16} align="middle">
          <Col span={12}>
            <Card
              title="Começar a conversa"
              bordered={false}
              style={{ backgroundColor: '#ffffff' }}
              actions={[
                <Button type="primary" icon={<RobotOutlined />} onClick={() => showModal(2)}>
                  Começar Conversa
                </Button>
              ]}
            >
              <Paragraph>
                Inicie a conversa agora e descubra como nossa IA pode transformar a maneira como você expoem informações sobre seus cursos! Ao começar uma conversa, você terá a oportunidade de explorar as funcionalidades do nosso sistema, fazer perguntas, obter respostas precisas e personalizadas, e até mesmo receber sugestões baseadas nos documentos que você enviou. Não perca a chance de ver como a nossa tecnologia pode ajudar a otimizar seus processos e fornecer insights valiosos. Clique no botão abaixo para iniciar uma conversa e começar a sua jornada com nossa inteligência artificial.
              </Paragraph>
            </Card>
          </Col>
          <Col span={12}>
            <img 
              src={interagirIa}
              alt="Interaja com a IA" 
              style={{ maxWidth: '100%', height: 'auto' }}
            />
          </Col>
        </Row>
      </section>
    </div>
  );
};

export default HomeView;
