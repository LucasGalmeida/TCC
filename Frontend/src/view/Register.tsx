import React, { useState } from 'react';
import { Form, Input, Button, Card } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/auth.service';
import { useAuthContext } from '../context/AuthContext';
import registrar from '../assets/registrar.jpg';

interface RegistrationFormValues {
  name: string;
  login: string;
  password: string;
  confirm: string;
}

const RegistrationForm: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuthContext();
  const [loading, setLoading] = useState(false);

  const onFinish = (values: RegistrationFormValues) => {
    const data = {
      name: values.name,
      login: values.login,
      password: values.password,
    };
    setLoading(true);
    AuthService.register(data)
      .then((response) => {
        login();
        window.localStorage.setItem('token', response.token);
        navigate('/home');
      })
      .catch((error) => {
        setLoading(false);
        console.error('Erro ao fazer login:', error.response.data);
      });
  };

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: '#001529',
      }}
    >
      <Card
        title="Crie sua Conta"
        bordered={false}
        style={{
          width: 400,
          textAlign: 'center',
          boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
        }}
        cover={
          <img
            alt="Exemplo de imagem"
            src={registrar}
            style={{ objectFit: 'cover', height: 200 }}
          />
        }
      >
        <Form
          name="register"
          onFinish={onFinish}
          scrollToFirstError
          layout="vertical"
          style={{ maxWidth: '100%' }}
        >
          <Form.Item
            name="name"
            label="Nome"
            rules={[
              {
                required: true,
                message: 'Por favor, digite seu nome!',
              },
            ]}
          >
            <Input prefix={<UserOutlined />} placeholder="Nome" />
          </Form.Item>

          <Form.Item
            name="login"
            label="Login"
            rules={[
              {
                required: true,
                message: 'Por favor, digite seu login!',
              },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="Login" />
          </Form.Item>

          <Form.Item
            name="password"
            label="Senha"
            rules={[
              {
                required: true,
                message: 'Por favor, digite sua senha!',
              },
            ]}
            hasFeedback
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Senha" />
          </Form.Item>

          <Form.Item
            name="confirm"
            label="Confirme a Senha"
            dependencies={['password']}
            hasFeedback
            rules={[
              {
                required: true,
                message: 'Por favor, confirme sua senha!',
              },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(
                    new Error('As duas senhas não coincidem!')
                  );
                },
              }),
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Confirme a Senha"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              disabled={loading}
              style={{ width: '100%' }}
            >
              Cadastrar
            </Button>
            <Button
              type="default"
              onClick={() => navigate('/')}
              style={{ marginTop: '10px', width: '100%' }}
            >
              Voltar para a tela de login
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default RegistrationForm;
