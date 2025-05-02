import './scss/styles.scss'
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Layout } from './Layout';
import { Home } from './Home';
import MarkdownToSlides from './MarkdownToSlides';
import { About } from './About';
import { Contact } from './Contact';
import { AuthProvider } from './AuthProvider';
import { AuthRequire } from './AuthRequire';

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      { index: true, element: <Home /> },
      { 
        path: "convert", 
        element: (
          <AuthRequire>
            <MarkdownToSlides />
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
