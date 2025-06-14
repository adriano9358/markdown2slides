import './scss/styles.scss'
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Layout } from './components/layout/Layout';
import { Home } from './pages/Home';
//import MarkdownToSlides from './MarkdownToSlides';
import ConvertWorkspace from './components/converter/ConvertWorkspace';
import { About } from './pages/About';
import { Contact } from './pages/Contact';
import { AuthProvider } from './providers/AuthProvider';
import { AuthRequire } from './auth/AuthRequire';
import MyProjects from './pages/MyProjects';
import PrintSlides from './pages/PrintsSlides';

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      { index: true, element: <Home /> },
      {
        path: "projects",
        element: (
          <AuthRequire>
            <MyProjects />
          </AuthRequire>
        ),
      },
      {
        path: "projects/:projectId",
        element: (
          <AuthRequire>
            <ConvertWorkspace />
          </AuthRequire>
        ),
      },
      {
        path: "projects/:projectId/print",
        element: (
          <AuthRequire>
            <PrintSlides />
          </AuthRequire>
        ),
      },
      { path: "about", element: <About /> },
      { path: "contact", element: <Contact /> },
      {
        path: "login",
        element: (
          <div>
            <h2>Login Required</h2>
            <a href="http://localhost:8080/oauth2/authorization/google" className="btn btn-primary">
              Login with Google
            </a>
          </div>
        ),
      }
    ],
  },
]);

export function App() {
  return (
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  );
}
