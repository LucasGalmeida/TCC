import { BrowserRouter, Route, Routes } from 'react-router-dom';

import Login from './view/Login';
import ErrorPage from './view/NotFound';
import Register from './view/Register';
import { AuthContextProvider } from './context/AuthContext';
import LayoutWithSider from './components/LayoutWithSider';
import RequireAuth from './components/RequireAuth';
import DocumentView from './view/DocumentView';
import HomeView from './view/HomeView';
import ChatView from './view/ChatView';

function App() {

  return (
    <AuthContextProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/home" element={
            <RequireAuth>
              <LayoutWithSider>
                <HomeView />
              </LayoutWithSider>
            </RequireAuth>
          } />
          <Route path="/document/:documentId" element={
            <RequireAuth>
              <LayoutWithSider>
                <DocumentView /> 
              </LayoutWithSider>
            </RequireAuth>
          } />
          <Route path="/chat/:chatId" element={
            <RequireAuth>
              <LayoutWithSider>
                <ChatView /> 
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
