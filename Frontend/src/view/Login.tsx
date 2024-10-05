import React, { useEffect, useState } from "react";
import { Form, Input, Button, FormProps } from "antd";
import { useNavigate } from "react-router-dom";
import AuthService from "../services/auth.service";
import { useAuthContext } from "../context/AuthContext";
import { LoginRequestDTO } from "../types/LoginRequestDTO";

type FieldType = {
  login?: string;
  password?: string;
};

const onFinishFailed: FormProps<FieldType>["onFinishFailed"] = (errorInfo) => {
  console.log("Failed:", errorInfo);
};

const Login: React.FC = () => {
  const { login, isAuthenticated } = useAuthContext();
  const [loading, setLoading] = useState(false);

  const onFinish = (values: LoginRequestDTO) => {
    const data = {
      login: values.login,
      password: values.password
    }
    setLoading(true);
    AuthService.login(data)
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

  useEffect(() => {
    if(isAuthenticated) navigate('/home');
  }, []);


  const navigate = useNavigate();

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
        name="login"
        labelCol={{ span: 8 }}
        wrapperCol={{ span: 16 }}
        style={{ maxWidth: 600 }}
        initialValues={{ remember: true }}
        onFinish={onFinish}
        onFinishFailed={onFinishFailed}
        autoComplete="off"
      >
        <Form.Item<FieldType>
          label="Login"
          name="login"
          rules={[{ required: true, message: "Please input your login!" }]}
        >
          <Input />
        </Form.Item>

        <Form.Item<FieldType>
          label="Password"
          name="password"
          rules={[{ required: true, message: "Please input your password!" }]}
        >
          <Input.Password />
        </Form.Item>

        {/* <Form.Item>
          <Form.Item<FieldType>
            name="remember"
            valuePropName="checked"
            wrapperCol={{ offset: 2, span: 16 }}
          >
            <Checkbox>Remember me</Checkbox>
          </Form.Item>
          
        </Form.Item> */}

        <Form.Item wrapperCol={{  span: 24 }}>
          <Button type="primary" htmlType="submit" loading={loading} disabled={loading}>
            Login
          </Button>
          <Button type="default" onClick={() => navigate('/register')} style={{ marginLeft: '10px' }}>
            New user? Click here.
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default Login;
