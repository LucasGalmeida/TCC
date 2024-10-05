import React, { useState } from 'react';
import { Form, Input, Button } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/auth.service';
import { useAuthContext } from '../context/AuthContext';

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
        password: values.password
      }
      setLoading(true);
      AuthService.register(data)
      .then(response => {
        login();
        window.localStorage.setItem("token", response.token);
        navigate('/home');
      })
      .catch(error => {
        setLoading(false);
        console.error("Erro ao fazer login:", error.response.data);
    });
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
      }}
    >
<Form
      name="register"
      onFinish={onFinish}
      scrollToFirstError
      layout="vertical"
    >
      <Form.Item
        name="name"
        label="Name"
        rules={[
          {
            required: true,
            message: 'Please enter your name!',
          },
        ]}
      >
        <Input prefix={<UserOutlined />} placeholder="Name" />
      </Form.Item>

      <Form.Item
        name="login"
        label="Login"
        rules={[
          {
            required: true,
            message: 'Please enter your login!',
          },
        ]}
      >
        <Input prefix={<MailOutlined />} placeholder="Login" />
      </Form.Item>

      <Form.Item
        name="password"
        label="Password"
        rules={[
          {
            required: true,
            message: 'Please enter your password!',
          },
        ]}
        hasFeedback
      >
        <Input.Password prefix={<LockOutlined />} placeholder="Password" />
      </Form.Item>

      <Form.Item
        name="confirm"
        label="Confirm Password"
        dependencies={['password']}
        hasFeedback
        rules={[
          {
            required: true,
            message: 'Please confirm your password!',
          },
          ({ getFieldValue }) => ({
            validator(_, value) {
              if (!value || getFieldValue('password') === value) {
                return Promise.resolve();
              }
              return Promise.reject(new Error('The two passwords do not match!'));
            },
          }),
        ]}
      >
        <Input.Password prefix={<LockOutlined />} placeholder="Confirm Password" />
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit" loading={loading} disabled={loading}>
          Register
        </Button>
        <Button type="default" onClick={() => navigate('/')} style={{ marginLeft: '10px' }}>
          Back to Login
        </Button>
      </Form.Item>
    </Form>
    </div>
    
  );
};

export default RegistrationForm;
