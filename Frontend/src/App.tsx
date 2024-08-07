import { useState } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

import Chat from './view/Chat';
import Login from './view/Login';
import ErrorPage from './view/NotFound';
import Register from './view/Register';
import { AuthContextProvider } from './context/AuthContext';
import LayoutWithSider from './components/LayoutWithSider';
import RequireAuth from './components/RequireAuth';

function App() {

  return (
    <AuthContextProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/chat" element={
            <RequireAuth>
              <LayoutWithSider>
                <Chat />
              </LayoutWithSider>
            </RequireAuth>
          } />
          <Route path="*" element={<ErrorPage />} />
        </Routes>
      </BrowserRouter>
    </AuthContextProvider>
  )
}

export default App
