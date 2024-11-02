import React, { useEffect, useState } from "react";
import { Form, Input, Button, Card, Row, Col, message } from "antd";
import { useNavigate } from "react-router-dom";
import AuthService from "../services/auth.service";
import { useAuthContext } from "../context/AuthContext";
import { LoginRequestDTO } from "../types/LoginRequestDTO";
import imgLogin from "../assets/login.jpg";

type FieldType = {
  login?: string;
  password?: string;
};

const onFinishFailed = (errorInfo: any) => {
  console.log("Failed:", errorInfo);
};

const Login: React.FC = () => {
  const { login, isAuthenticated } = useAuthContext();
  const [loading, setLoading] = useState(false);

  const onFinish = (values: LoginRequestDTO) => {
    const data = {
      login: values.login,
      password: values.password,
    };
    setLoading(true);
    AuthService.login(data)
      .then((response) => {
        login();
        window.localStorage.setItem("token", response.token);
        navigate("/home");
      })
      .catch((error) => {
        setLoading(false);
        message.error("Erro ao fazer login:" + error.response.data);
      });
  };

  useEffect(() => {
    if (isAuthenticated) navigate("/home");
  }, [isAuthenticated]);

  const navigate = useNavigate();

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
        backgroundColor: '#001529',
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      <Card
        style={{
          maxWidth: 600,
          width: "100%",
          padding: "40px",
          borderRadius: "8px",
          boxShadow: "0 4px 12px rgba(0, 0, 0, 0.1)",
        }}
      >
        <Row gutter={16}>
          <Col span={12} style={{ textAlign: "center" }}>
            <img
              src={imgLogin}
              alt="Login"
              style={{ maxWidth: "100%", height: "auto", borderRadius: "8px" }}
            />
          </Col>
          
          <Col span={12}>
            <Form
              name="login"
              layout="vertical"
              onFinish={onFinish}
              onFinishFailed={onFinishFailed}
              autoComplete="off"
            >
              <Form.Item<FieldType>
                label="Login"
                name="login"
                rules={[{ required: true, message: "Por favor insira seu login!" }]}
              >
                <Input placeholder="Digite seu login" />
              </Form.Item>

              <Form.Item<FieldType>
                label="Senha"
                name="password"
                rules={[{ required: true, message: "Por favor insira sua senha!" }]}
              >
                <Input.Password placeholder="Digite sua senha" />
              </Form.Item>

              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  disabled={loading}
                  style={{ width: "100%" }}
                >
                  Login
                </Button>
              </Form.Item>

              <Form.Item>
                <Button
                  type="link"
                  onClick={() => navigate("/register")}
                  style={{ width: "100%", textAlign: "center" }}
                >
                  Novo usuário? Clique aqui.
                </Button>
              </Form.Item>
            </Form>
          </Col>
        </Row>
      </Card>
    </div>
  );
};

export default Login;
