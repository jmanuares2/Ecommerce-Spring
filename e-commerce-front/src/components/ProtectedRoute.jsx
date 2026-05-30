import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Este componente protege una pantalla.
// Si hay usuario logueado, muestra el contenido.
// Si no hay usuario, manda al login.
function ProtectedRoute({ children }) {
  const { user } = useAuth();

  // children representa el componente que queremos proteger,
  // por ejemplo <Favorite />.
  return user ? children : <Navigate to="/login" replace />;
}

export default ProtectedRoute;
