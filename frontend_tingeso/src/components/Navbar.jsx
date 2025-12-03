import React, { useState } from 'react';
import {
    Drawer,
    List,
    ListItem,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Collapse,
    Box,
    Typography
} from '@mui/material';
import {
    Build as BuildIcon,
    Assignment as AssignmentIcon,
    People as PeopleIcon,
    AttachMoney as MoneyIcon,
    ExpandLess,
    ExpandMore,
    AddBox as AddIcon,
    List as ListIcon,
    Inventory as InventoryIcon,
    PersonAdd as PersonAddIcon,
    AccountBalance as RatesIcon,
    History as HistoryIcon,
    Refresh as RefreshIcon,
    Payment as PaymentIcon,
    Assessment as AssessmentIcon,
    Leaderboard as LeaderboardIcon,
    HomeRepairService as RepairIcon
} from '@mui/icons-material';
import { useKeycloak } from "@react-keycloak/web";
import { useNavigate, useLocation } from "react-router-dom";

const DRAWER_WIDTH = 280;

const Navbar = ({ open, onClose }) => {
    const { keycloak } = useKeycloak();
    const navigate = useNavigate();
    const location = useLocation();
    const roles = keycloak?.tokenParsed?.realm_access?.roles || [];

    // Estados para controlar submenús
    const [openMenus, setOpenMenus] = useState({
        tools: false,
        rentals: false,
        customers: false,
        fines: false,
        rates: false,
        records: false,
        reportsAndConsultations: false
    });


    const toggleSubmenu = (menu) => {
        setOpenMenus(prev => ({
            ...prev,
            [menu]: !prev[menu]
        }));
    };

    const handleNavigation = (path) => {
        navigate(path);
        if (onClose) onClose(); // Cerrar en móvil
    };

    const isActive = (path) => location.pathname === path;
    const isParentActive = (parentPath) => location.pathname.startsWith(parentPath);

    const menuItems = [
        {
            id: 'tools',
            title: 'Herramientas',
            icon: <BuildIcon />,
            items: [
                { path: '/tools', title: 'Ver Herramientas', icon: <ListIcon />, roles: ['ADMIN', 'EMPLOYEE']  },
                { path: '/toolsInventory', title: 'Inventario de herramientas', icon: <InventoryIcon />, roles: ['ADMIN', 'EMPLOYEE'] },
                { path: '/addTool', title: 'Agregar Herramienta', icon: <AddIcon />, roles: ['ADMIN'] },
                { path: '/repairedTool', title: 'Herramienta Reparada', icon: <RepairIcon />, roles: ['ADMIN'] }
            ]
        },
        {
            id: 'loans',
            title: 'Préstamos',
            icon: <AssignmentIcon />,
            items: [
                { path: '/loans', title: 'Ver Préstamos', icon: <ListIcon />, roles: ['ADMIN', 'EMPLOYEE'] },
                { path: '/loans/add', title: 'Nuevo Préstamo', icon: <AddIcon />, roles: ['ADMIN', 'EMPLOYEE'] },
                { path: '/loans/returns', title: 'Devoluciones', icon: <AssignmentIcon />, roles: ['ADMIN', 'EMPLOYEE'] }
            ]
        },
        {
            id: 'customers',
            title: 'Clientes',
            icon: <PeopleIcon />,
            items: [
                { path: '/customers', title: 'Ver Clientes', icon: <ListIcon />, roles: ['ADMIN', 'EMPLOYEE'] },
                { path: '/customers/add', title: 'Agregar Cliente', icon: <PersonAddIcon />, roles: ['ADMIN'] },
                { path: '/customers/update', title: 'Actualizar Cliente', icon: <RefreshIcon />, roles: ['ADMIN'] }
            ]
        },
        {
            id: 'fines',
            title: 'Multas',
            icon: <MoneyIcon />,
            roles: ['ADMIN', 'EMPLOYEE'],
            items: [
                { path: '/fines', title: 'Ver Multas', icon: <ListIcon />, roles: ['ADMIN', 'EMPLOYEE'] },
                { path: '/fines/add', title: 'Agregar Multa', icon: <AddIcon />, roles: ['ADMIN'] },
                { path: '/fines/payFine', title: 'Pagar Multa', icon: <PaymentIcon />, roles: ['ADMIN']}
            ]
        },
        {
            id: 'rates',
            title: 'Tarifas',
            icon: <RatesIcon />,
            items: [
                { path: '/globalRates', title: 'Ver Tarifas', icon: <ListIcon />, roles: ['ADMIN', 'EMPLOYEE'] },
                { path: '/globalRates/add', title: 'Agregar Tarifa global', icon: <AddIcon />, roles: ['ADMIN'] },
                { path: '/rates/update', title: 'Actualizar tarifas', icon: <RefreshIcon />, roles: ['ADMIN'] }
            ]
        },
        {
            id: 'records',
            title: 'Registros',
            icon: <HistoryIcon />,
            roles: ['ADMIN', 'EMPLOYEE'],
            items: [
                { path: '/records', title: 'Historial de movimientos', icon: <AssignmentIcon /> }
            ]
        },

        {
            id: 'reportsAndConsultations',
            title: 'Reportes y consultas',
            icon: <AssessmentIcon  />,
            roles: ['ADMIN', 'EMPLOYEE'],
            items: [
                { path: '/reportsAndConsultations', title: 'Listar préstamos activos', icon: <ListIcon /> },
                { path: '/reportsAndConsultations/listCustomersWithOverdue', title: 'Listar clientes con  atraso', icon: <ListIcon /> },
                { path: '/reportsAndConsultations/toolsRanking', title: 'Ranking herramientas mas prestadas', icon: <LeaderboardIcon  /> }

            ]
        }

    ];

    // Filtrar items basado en roles
    const filteredMenuItems = menuItems.filter(menu => {
        if (!menu.roles) return true;
        return menu.roles.some(role => roles.includes(role));
    });

    const drawer = (
        <Box sx={{ height: '100%', backgroundColor: '#f8f9fa' }}>
            {/* Espaciado para el TopBar */}
            <Box sx={{ height: 64 }} />

            {/* Header del sidebar */}
            <Box sx={{ p: 2, borderBottom: '1px solid #e0e0e0' }}>
                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                    Menú Principal
                </Typography>
            </Box>

            {/* Lista de menús */}
            <List sx={{ pt: 1 }}>
                {filteredMenuItems.map((menu) => (
                    <React.Fragment key={menu.id}>
                        <ListItem disablePadding>
                            <ListItemButton
                                onClick={() => toggleSubmenu(menu.id)}
                                sx={{
                                    py: 1.5,
                                    backgroundColor: isParentActive(`/${menu.id}`) ? 'rgba(10, 20, 46, 0.05)' : 'transparent',
                                    borderRight: isParentActive(`/${menu.id}`) ? '3px solid #FACC15' : '3px solid transparent',
                                    '&:hover': {
                                        backgroundColor: 'rgba(10, 20, 46, 0.08)'
                                    }
                                }}
                            >
                                <ListItemIcon sx={{ color: isParentActive(`/${menu.id}`) ? '#0A142E' : '#666' }}>
                                    {menu.icon}
                                </ListItemIcon>
                                <ListItemText
                                    primary={menu.title}
                                    sx={{
                                        '& .MuiListItemText-primary': {
                                            color: isParentActive(`/${menu.id}`) ? '#0A142E' : '#333',
                                            fontWeight: isParentActive(`/${menu.id}`) ? 'bold' : 'normal'
                                        }
                                    }}
                                />
                                {openMenus[menu.id] ?
                                    <ExpandLess sx={{ color: '#666' }} /> :
                                    <ExpandMore sx={{ color: '#666' }} />
                                }
                            </ListItemButton>
                        </ListItem>

                        <Collapse in={openMenus[menu.id]} timeout="auto" unmountOnExit>
                            <List component="div" disablePadding>
                                {menu.items.map((item) => {
                                    // Verificar permisos para subitems
                                    if (item.roles && !item.roles.some(role => roles.includes(role))) {
                                        return null;
                                    }

                                    return (
                                        <ListItem key={item.path} disablePadding>
                                            <ListItemButton
                                                onClick={() => handleNavigation(item.path)}
                                                sx={{
                                                    pl: 4,
                                                    py: 1,
                                                    backgroundColor: isActive(item.path) ? 'rgba(250, 204, 21, 0.1)' : 'transparent',
                                                    borderRight: isActive(item.path) ? '3px solid #FACC15' : '3px solid transparent',
                                                    '&:hover': {
                                                        backgroundColor: 'rgba(250, 204, 21, 0.05)'
                                                    }
                                                }}
                                            >
                                                <ListItemIcon sx={{
                                                    minWidth: 36,
                                                    color: isActive(item.path) ? '#FACC15' : '#888'
                                                }}>
                                                    {item.icon}
                                                </ListItemIcon>
                                                <ListItemText
                                                    primary={item.title}
                                                    sx={{
                                                        '& .MuiListItemText-primary': {
                                                            fontSize: '0.875rem',
                                                            color: isActive(item.path) ? '#0A142E' : '#555',
                                                            fontWeight: isActive(item.path) ? 'bold' : 'normal'
                                                        }
                                                    }}
                                                />
                                            </ListItemButton>
                                        </ListItem>
                                    );
                                })}
                            </List>
                        </Collapse>
                    </React.Fragment>
                ))}

            </List>
        </Box>
    );

    return (
        <Drawer
            variant="temporary"
            anchor="left"
            open={open}
            onClose={onClose}
            sx={{
                width: DRAWER_WIDTH,
                flexShrink: 0,
                '& .MuiDrawer-paper': {
                    width: DRAWER_WIDTH,
                    boxSizing: 'border-box',
                    borderRight: '2px solid #FACC15'
                },
            }}
        >
            {drawer}
        </Drawer>
    );
};

export default Navbar;