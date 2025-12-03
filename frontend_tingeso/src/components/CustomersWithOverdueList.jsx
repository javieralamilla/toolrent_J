import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Chip,
    TextField,
    Button,
    Alert,
    CircularProgress,
    Grid
} from '@mui/material';
import {
    Clear as ClearIcon,
    Warning as WarningIcon,
    Person as PersonIcon
} from '@mui/icons-material';
import fineService from '../services/fine.service';

const CustomersWithOverdueList = () => {
    const navigate = useNavigate();
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Filtros de fecha
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    // Función para cargar clientes con atrasos
    const fetchCustomersWithOverdue = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await fineService.getCustomersWithOverdue(
                startDate || null,
                endDate || null
            );

            setCustomers(response.data);
        } catch (err) {
            setError('Error al cargar los clientes con atrasos');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    // Cargar clientes al montar el componente
    useEffect(() => {
        fetchCustomersWithOverdue();
    }, []);

    // Limpiar filtros
    const handleClearFilters = () => {
        setStartDate('');
        setEndDate('');
        fetchCustomersWithOverdue(); // Recargar sin filtros de fecha
    };

    // Buscar con filtros de fecha del backend
    const handleSearchWithDateFilter = () => {
        fetchCustomersWithOverdue();
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '400px' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 4 }}>
                Clientes con Atrasos
            </Typography>

            {/* Sección de Filtros */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 'bold', mb: 2 }}>
                    Filtros
                </Typography>

                <Grid container spacing={2} alignItems="center">
                    {/* Filtro fecha inicio */}
                    <Grid size={{ xs: 12, md: 4 }}>
                        <Box
                            onClick={() => document.getElementById('start-date-picker-overdue').showPicker?.()}
                            sx={{ cursor: 'pointer' }}
                        >
                            <TextField
                                id="start-date-picker-overdue"
                                fullWidth
                                label="Fecha Inicio"
                                type="date"
                                value={startDate}
                                onChange={(e) => setStartDate(e.target.value)}
                                InputLabelProps={{ shrink: true }}
                                sx={{
                                    '& input': {
                                        cursor: 'pointer'
                                    }
                                }}
                            />
                        </Box>
                    </Grid>
                    {/* Filtro fecha fin */}
                    <Grid size={{ xs: 12, md: 4 }}>
                        <Box
                            onClick={() => document.getElementById('end-date-picker-overdue').showPicker?.()}
                            sx={{ cursor: 'pointer' }}
                        >
                            <TextField
                                id="end-date-picker-overdue"
                                fullWidth
                                label="Fecha Fin"
                                type="date"
                                value={endDate}
                                onChange={(e) => setEndDate(e.target.value)}
                                InputLabelProps={{ shrink: true }}
                                sx={{
                                    '& input': {
                                        cursor: 'pointer'
                                    }
                                }}
                            />
                        </Box>
                    </Grid>

                    <Grid size={{ xs: 12, md: 2 }}>
                        <Button
                            fullWidth
                            variant="contained"
                            onClick={handleSearchWithDateFilter}
                            sx={{
                                height: '56px',
                                backgroundColor: '#0A142E',
                                '&:hover': {
                                    backgroundColor: '#1a2847'
                                }
                            }}
                        >
                            Buscar
                        </Button>
                    </Grid>

                    <Grid size={{ xs: 12, md: 2 }}>
                        <Button
                            fullWidth
                            variant="outlined"
                            startIcon={<ClearIcon />}
                            onClick={handleClearFilters}
                            sx={{ height: '56px' }}
                        >
                            Limpiar
                        </Button>
                    </Grid>
                </Grid>

                {/* Estadísticas rápidas */}
                <Box sx={{ mt: 3, display: 'flex', gap: 3, flexWrap: 'wrap' }}>
                    <Box>
                        <Typography variant="body2" color="textSecondary">
                            Total Clientes con Atrasos
                        </Typography>
                        <Typography variant="h6" sx={{ color: '#f44336', fontWeight: 'bold' }}>
                            {customers.length}
                        </Typography>
                    </Box>
                </Box>
            </Paper>

            {/* Mensaje de error */}
            {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            {/* Tabla de clientes */}
            <TableContainer component={Paper}>
                <Table>
                    <TableHead sx={{ backgroundColor: '#0A142E' }}>
                        <TableRow>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>ID</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>RUT</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Nombre</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Email</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Teléfono</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Estado</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {customers.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={7} align="center">
                                    <Typography variant="body1" sx={{ py: 3, color: '#666' }}>
                                        No se encontraron clientes con atrasos en el período seleccionado
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            customers.map((customer) => (
                                <TableRow
                                    key={customer.id}
                                    sx={{
                                        '&:hover': { backgroundColor: '#f5f5f5' }
                                    }}
                                >
                                    <TableCell>{customer.id}</TableCell>
                                    <TableCell>{customer.rut}</TableCell>
                                    <TableCell>{customer.name}</TableCell>
                                    <TableCell>{customer.email}</TableCell>
                                    <TableCell>{customer.phoneNumber || 'N/A'}</TableCell>
                                    <TableCell>
                                        <Chip
                                            icon={<WarningIcon />}
                                            label="Con Atrasos"
                                            color="error"
                                            size="small"
                                        />
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default CustomersWithOverdueList;