import React from 'react';
import { Button, Typography, Row, Col, Card } from 'antd';
import { FileAddOutlined, RobotOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;

const HomeView: React.FC = () => {
  return (
    <div style={{ padding: '50px', backgroundColor: '#f0f2f5' }}>
      <Row gutter={16}>
        <Col span={24} style={{ textAlign: 'center', marginBottom: '30px' }}>
          <Title level={1}>Bem-vindo ao ChatAI</Title>
          <Paragraph>
            Seu assistente de IA está aqui para ajudar você a analisar e aprender com seus documentos.
          </Paragraph>
        </Col>
        <Col span={12}>
          <Card
            title="Envie Documentos"
            bordered={false}
            style={{ textAlign: 'center', backgroundColor: '#ffffff' }}
            actions={[
              <Button type="primary" icon={<FileAddOutlined />}>
                Enviar
              </Button>
            ]}
          >
            <Paragraph>
            Faça upload dos seus arquivos de maneira rápida e fácil! Com nossa plataforma, você pode enviar seus documentos diretamente para o sistema em poucos cliques. Basta arrastar e soltar seus arquivos ou selecionar a opção de upload para começar. A nossa ferramenta suporta diversos formatos de arquivo, garantindo que você possa carregar tudo o que precisa sem complicações. Após o upload, nossa inteligência artificial analisará seus documentos e fornecerá insights valiosos, ajudando você a extrair o máximo de informações e otimizar seus processos. Experimente agora e veja como é simples integrar seus arquivos ao nosso sistema!
            </Paragraph>
          </Card>
        </Col>
        <Col span={12}>
          <Card
            title="Começar a conversa"
            bordered={false}
            style={{ textAlign: 'center', backgroundColor: '#ffffff' }}
            actions={[
              <Button type="primary" icon={<RobotOutlined />}>
                Começar Conversa
              </Button>
            ]}
          >
            <Paragraph>
            Inicie a conversa agora e descubra como nossa IA pode transformar a maneira como você interage com seus documentos! Ao começar uma conversa, você terá a oportunidade de explorar as funcionalidades do nosso sistema, fazer perguntas, obter respostas precisas e personalizadas, e até mesmo receber sugestões baseadas nos documentos que você enviou. Não perca a chance de ver como a nossa tecnologia pode ajudar a otimizar seus processos e fornecer insights valiosos. Clique no botão abaixo para iniciar uma conversa e começar a sua jornada com nossa inteligência artificial.
            </Paragraph>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default HomeView;
