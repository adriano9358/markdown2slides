import * as React from 'react';
import { createRoot } from 'react-dom/client'
import * as bootstrap from 'bootstrap'
import './scss/styles.scss'
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Layout } from './Layout';
import { Login } from './Login';
import { Home } from './Home';
import MarkdownToSlides from './SlidePreview';

const router = createBrowserRouter([
    {
      path: '/',
      element: <MarkdownToSlides />,
      /*children: [
        {
          path: '/',
          element: <Home />,
        },
        {
          path: '/login',
          element: <Login />,
        },
      ],*/
    },
  ]);


export function App() {
    return (
        <RouterProvider router={router} />
    );
}
