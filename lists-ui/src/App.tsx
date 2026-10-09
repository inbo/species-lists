/// <reference types="vite-plugin-svgr/client" />

// Routing
import { NuqsAdapter } from 'nuqs/adapters/react-router/v7';
import { RouterProvider } from 'react-router/dom';
import routes from './Router';

// Authentication
import { useAuth } from 'react-oidc-context';

// Local components
import PageLoader from './components/PageLoader';

function App() {
  const auth = useAuth();

  // If the user hasn't been authenticated, show a page loader instead
  return auth.isLoading ? (
    <div style={{ width: '100vw', height: '100vh' }}>
      <PageLoader />
    </div>
  ) : (
    <NuqsAdapter>
      <RouterProvider router={routes} />
    </NuqsAdapter>
  );
}

export default App;
