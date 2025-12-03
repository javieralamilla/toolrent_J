import React from 'react';
import {
    AppBar,
    Toolbar,
    Typography,
    Box,
    IconButton,
    Breadcrumbs,
    Link
} from '@mui/material';
import {
    Construction as ConstructionIcon,
    Person as PersonIcon,
    ExitToApp as ExitIcon,
    Home as HomeIcon,
    Menu as MenuIcon
} from '@mui/icons-material';
import { useKeycloak } from "@react-keycloak/web";
import { useNavigate, useLocation } from "react-router-dom";

const TopBar = ({ onMenuToggle }) => {
    const { keycloak } = useKeycloak();
    const navigate = useNavigate();
    const location = useLocation();
    const roles = keycloak?.tokenParsed?.realm_access?.roles || [];

    const isAdmin = roles.includes('ADMIN');
    const isEmployee = roles.includes('EMPLOYEE');

    // Generar breadcrumbs basado en la ruta actual
    const generateBreadcrumbs = () => {
        const pathnames = location.pathname.split('/').filter((x) => x);

        const breadcrumbNameMap = {
            'tools': 'Herramientas',
            'rentals': 'Préstamos',
            'customers': 'Clientes',
            'fines': 'Multas',
            'rates': 'Tarifas',
            'records': 'Registros',
            'add': 'Agregar',
            'edit': 'Editar',
            'new': 'Nuevo',
            'categories': 'Categorías'
        };

        return pathnames.map((value, index) => {
            const to = `/${pathnames.slice(0, index + 1).join('/')}`;
            const displayName = breadcrumbNameMap[value] || value;

            return { to, displayName };
        });
    };

    const breadcrumbs = generateBreadcrumbs();

    return (
        <AppBar
            position="fixed"
            sx={{
                backgroundColor: '#0A142E',
                zIndex: (theme) => theme.zIndex.drawer + 1,
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}
        >
            <Toolbar sx={{ justifyContent: 'space-between', px: 3 }}>
                {/* Lado izquierdo: Toggle menu + Logo */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <IconButton
                        color="inherit"
                        onClick={onMenuToggle}
                        sx={{
                            color: '#FACC15',
                            '&:hover': {
                                backgroundColor: 'rgba(250, 204, 21, 0.1)'
                            }
                        }}
                    >
                        <MenuIcon />
                    </IconButton>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <ConstructionIcon
                            sx={{
                                fontSize: 28,
                                color: '#FACC15',
                                transform: 'rotate(-15deg)'
                            }}
                        />
                        <Typography
                            variant="h6"
                            component="div"
                            sx={{
                                fontWeight: 'bold',
                                color: 'white',
                                letterSpacing: '1px',
                                cursor: 'pointer'
                            }}
                            onClick={() => navigate('/')}
                        >
                            ToolRent
                        </Typography>
                    </Box>
                </Box>

                {/* Centro: Breadcrumbs */}
                <Box sx={{ display: { xs: 'none', md: 'flex' }, alignItems: 'center' }}>
                    <Breadcrumbs
                        separator="›"
                        sx={{
                            color: 'white',
                            '& .MuiBreadcrumbs-separator': {
                                color: '#FACC15'
                            }
                        }}
                    >
                        <Link
                            color="inherit"
                            onClick={() => navigate('/')}
                            sx={{
                                cursor: 'pointer',
                                textDecoration: 'none',
                                display: 'flex',
                                alignItems: 'center',
                                gap: 0.5,
                                '&:hover': { color: '#FACC15' }
                            }}
                        >
                            <HomeIcon fontSize="small" />
                            Inicio
                        </Link>
                        {breadcrumbs.map((breadcrumb, index) => (
                            <Link
                                key={breadcrumb.to}
                                color={index === breadcrumbs.length - 1 ? '#FACC15' : 'inherit'}
                                onClick={() => navigate(breadcrumb.to)}
                                sx={{
                                    cursor: 'pointer',
                                    textDecoration: 'none',
                                    '&:hover': { color: '#FACC15' }
                                }}
                            >
                                {breadcrumb.displayName}
                            </Link>
                        ))}
                    </Breadcrumbs>
                </Box>

                {/* Lado derecho: Usuario info + logout */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Box sx={{
                        display: { xs: 'none', sm: 'flex' },
                        alignItems: 'center',
                        gap: 1,
                        backgroundColor: 'rgba(250, 204, 21, 0.1)',
                        padding: '8px 16px',
                        borderRadius: '20px',
                        border: '1px solid rgba(250, 204, 21, 0.3)'
                    }}>
                        <PersonIcon sx={{ color: '#FACC15', fontSize: 20 }} />
                        <Typography variant="body2" sx={{ color: 'white', fontSize: '0.875rem' }}>
                            {keycloak?.tokenParsed?.preferred_username}
                        </Typography>
                        {isAdmin && (
                            <Box sx={{
                                backgroundColor: '#FACC15',
                                color: '#0A142E',
                                padding: '2px 8px',
                                borderRadius: '10px',
                                fontSize: '0.75rem',
                                fontWeight: 'bold',
                                marginLeft: 1
                            }}>
                                ADMIN
                            </Box>
                        )}
                        {isEmployee && !isAdmin && (
                            <Box sx={{
                                backgroundColor: 'rgba(250, 204, 21, 0.3)',
                                color: 'white',
                                padding: '2px 8px',
                                borderRadius: '10px',
                                fontSize: '0.75rem',
                                fontWeight: 'bold',
                                marginLeft: 1
                            }}>
                                EMPLEADO
                            </Box>
                        )}
                    </Box>

                    <IconButton
                        onClick={() => keycloak?.logout({ redirectUri: window.location.origin + '/' })}
                        sx={{
                            color: '#FACC15',
                            backgroundColor: 'rgba(250, 204, 21, 0.1)',
                            '&:hover': {
                                backgroundColor: 'rgba(250, 204, 21, 0.2)',
                                color: 'white'
                            }
                        }}
                    >
                        <ExitIcon />
                    </IconButton>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default TopBar;