import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../stores/useUserStore';

interface PublicRouteProps {
    children: React.ReactNode;
}

const PublicRoute: React.FC<PublicRouteProps> = ({ children }) => {
    const { isAuthenticated } = useAuthStore();
    
    return !isAuthenticated ? <>{children}</> : <Navigate to="/" replace />;
};

export default PublicRoute;
