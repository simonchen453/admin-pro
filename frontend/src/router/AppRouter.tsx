import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Suspense, lazy } from 'react';
import { Spin } from 'antd';
import Layout from '../pages/Layout';
import ProtectedRoute from '../components/ProtectedRoute';
import PublicRoute from '../components/PublicRoute';

// 懒加载页面组件
const Login = lazy(() => import('../pages/Login/Login.tsx'));
const Home = lazy(() => import('../pages/Home'));
const UserList = lazy(() => import('../pages/User/UserList'));
const RoleList = lazy(() => import('../pages/Role/RoleList'));
const MenuList = lazy(() => import('../pages/Menu/MenuList'));
const DomainList = lazy(() => import('../pages/Domain/DomainList'));
const DomainEnvList = lazy(() => import('../pages/DomainEnv'));
const DeptList = lazy(() => import('../pages/Dept/DeptList'));
const PostList = lazy(() => import('../pages/Post/PostList'));
const ConfigList = lazy(() => import('../pages/Config/ConfigList'));
const DictList = lazy(() => import('../pages/Dict/DictList'));
const JobList = lazy(() => import('../pages/Job/JobList'));
const JobLog = lazy(() => import('../pages/Job/JobLog'));
const SessionList = lazy(() => import('../pages/Session/SessionList'));
const ServerInfo = lazy(() => import('../pages/Server/ServerInfo'));
const SysLogList = lazy(() => import('../pages/SysLog/SysLogList'));
const SysLogView = lazy(() => import('../pages/SysLog/SysLogView'));
const AuditLogList = lazy(() => import('../pages/AuditLog/AuditLogList'));
const GeneratorList = lazy(() => import('../pages/Generator'));
const Swagger = lazy(() => import('../pages/Swagger'));
const ChangePassword = lazy(() => import('../pages/ChangePassword/ChangePassword'));
const NoPermission = lazy(() => import('../pages/NoPermission'));
const NotFound = lazy(() => import('../pages/NotFound'));

// 加载中组件
const LoadingSpinner = () => (
    <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '200px' 
    }}>
        <Spin size="large" />
    </div>
);

function AppRouter() {
    return (
        <BrowserRouter>
            <Routes>
                <Route 
                    path="/login" 
                    element={
                        <PublicRoute>
                            <Suspense fallback={<LoadingSpinner />}>
                                <Login />
                            </Suspense>
                        </PublicRoute>
                    } 
                />
                <Route path="/" element={
                    <ProtectedRoute>
                        <Layout />
                    </ProtectedRoute>
                }>
                    <Route 
                        index 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <Home />
                            </Suspense>
                        } 
                    />
                    <Route 
                        path="home" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <Home />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/user" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <UserList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/role" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <RoleList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/menu" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <MenuList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/domain" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <DomainList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/userDomainEnv" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <DomainEnvList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/dept" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <DeptList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/post" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <PostList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/config" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <ConfigList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/dict" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <DictList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/job" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <JobList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/job/log" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <JobLog />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/session" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <SessionList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/server" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <ServerInfo />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/syslog"
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <SysLogList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/syslog/view" 
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <SysLogView />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/audit"
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <AuditLogList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/generator"
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <GeneratorList />
                            </Suspense>
                        } 
                    />
                    <Route
                        path="admin/swagger"
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <Swagger />
                            </Suspense>
                        } 
                    />

                    <Route
                        path="changepwd"
                        element={
                            <Suspense fallback={<LoadingSpinner />}>
                                <ChangePassword />
                            </Suspense>
                        }
                    />
                </Route>

                <Route
                    path="/no-permission" 
                    element={
                        <Suspense fallback={<LoadingSpinner />}>
                            <NoPermission />
                        </Suspense>
                    } 
                />
                <Route 
                    path="*" 
                    element={
                        <Suspense fallback={<LoadingSpinner />}>
                            <NotFound />
                        </Suspense>
                    } 
                />
            </Routes>
        </BrowserRouter>
    );
}

export default AppRouter;