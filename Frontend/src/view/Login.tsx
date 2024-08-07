import React from "react";
import { Form, Input, Button, Checkbox, FormProps } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import AuthService from "../services/auth.service";

type FieldType = {
  username?: string;
  password?: string;
  remember?: string;
};

const onFinish: FormProps<FieldType>["onFinish"] = (values) => {
  console.log("Success:", values);
};

const onFinishFailed: FormProps<FieldType>["onFinishFailed"] = (errorInfo) => {
  console.log("Failed:", errorInfo);
};

const Login: React.FC = () => {
  const onFinish = (values: any) => {
    const data = {
      login: values.username,
      password: values.password
    }
    AuthService.login(data)
    .then(response => {
      window.localStorage.setItem("token", response.token);
    })
    .catch(error => {
      console.error("Erro ao fazer login:", error.response.data);
  });
  };

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
          label="Username"
          name="username"
          rules={[{ required: true, message: "Please input your username!" }]}
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

        <Form.Item>
          <Form.Item<FieldType>
            name="remember"
            valuePropName="checked"
            wrapperCol={{ offset: 2, span: 16 }}
          >
            <Checkbox>Remember me</Checkbox>
          </Form.Item>
          
        </Form.Item>

        <Form.Item wrapperCol={{  span: 24 }}>
          <Button type="primary" htmlType="submit">
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
