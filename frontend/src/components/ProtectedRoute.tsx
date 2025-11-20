import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../stores/useUserStore';

interface ProtectedRouteProps {
    children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
    const { isAuthenticated } = useAuthStore();
    
    console.log('ProtectedRoute - isAuthenticated:', isAuthenticated);
    
    return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

export default ProtectedRoute;
