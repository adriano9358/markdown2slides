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
import ProjectTestPanel from './ProjectTestPanel';
import { Login } from './pages/Login';
import CollaboratorsPage from './components/converter/CollaboratorsPage';
import InvitationsPage from './components/invitations/InvitationsPage';

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      { index: true, element: <Home /> },
      {
        path: "projects/test",
        element: (
          <AuthRequire>
            <ProjectTestPanel />
          </AuthRequire>
        ),
      },
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
      {
        path: "projects/:projectId/collaborators",
        element: (
          <AuthRequire>
            <CollaboratorsPage />
          </AuthRequire>
        ),
      },
      {
        path: "invitations",
        element: (
          <AuthRequire>
            <InvitationsPage />
          </AuthRequire>
        ),
      },
      { path: "about", element: <About /> },
      { path: "contact", element: <Contact /> },
      {
        path: "login",
        element: <Login />,
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
