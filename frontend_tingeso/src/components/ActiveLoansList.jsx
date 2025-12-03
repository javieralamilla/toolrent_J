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
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Alert,
    CircularProgress,
    Grid
} from '@mui/material';
import {
    Clear as ClearIcon,
    CheckCircle as CheckCircleIcon,
    Warning as WarningIcon
} from '@mui/icons-material';
import loanService from '../services/loan.service';

const ActiveLoansList = () => {
    const navigate = useNavigate();
    const [loans, setLoans] = useState([]);
    const [filteredLoans, setFilteredLoans] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Filtros
    const [statusFilter, setStatusFilter] = useState('all');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    // Estados posibles del sistema
    const STATUS_OPTIONS = [
        { value: 'all', label: 'Todos' },
        { value: 'activo', label: 'Activos' },
        { value: 'vencido', label: 'Vencidos' },
        { value: 'evaluación pendiente', label: 'Evaluación Pendiente' },
        { value: 'multa pendiente', label: 'Multa Pendiente' }
    ];

    // Función para formatear fechas sin problemas de zona horaria
    const formatDate = (dateString) => {
        const [year, month, day] = dateString.split('-');
        return `${day}-${month}-${year}`;
    };

    // Función para cargar préstamos activos
    const fetchActiveLoans = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await loanService.getActiveLoans(
                startDate || null,
                endDate || null
            );

            setLoans(response.data);
            setFilteredLoans(response.data);
        } catch (err) {
            setError('Error al cargar los préstamos activos');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    // Cargar préstamos al montar el componente
    useEffect(() => {
        fetchActiveLoans();
    }, []);

    // Aplicar filtros
    useEffect(() => {
        let filtered = [...loans];

        // Filtrar por estado (solo filtro local, el de fechas ya viene del backend)
        if (statusFilter !== 'all') {
            filtered = filtered.filter(loan => loan.status === statusFilter);
        }

        setFilteredLoans(filtered);
    }, [statusFilter, loans]);

    // Limpiar filtros
    const handleClearFilters = () => {
        setStatusFilter('all');
        setStartDate('');
        setEndDate('');
        fetchActiveLoans(); // Recargar sin filtros de fecha
    };

    // Buscar con filtros de fecha del backend
    const handleSearchWithDateFilter = () => {
        fetchActiveLoans();
    };

    // Obtener color del chip según estado
    const getStatusColor = (status) => {
        switch(status) {
            case 'activo':
                return 'success';
            case 'vencido':
                return 'error';
            case 'evaluación pendiente':
                return 'default';
            case 'multa pendiente':
                return 'warning';
            default:
                return 'default';
        }
    };

    // Obtener ícono según estado
    const getStatusIcon = (status) => {
        switch(status) {
            case 'activo':
                return <CheckCircleIcon />;
            case 'vencido':
                return <WarningIcon />;
            case 'evaluación pendiente':
                return <WarningIcon />;
            case 'multa pendiente':
                return <WarningIcon />;
            default:
                return null;
        }
    };

    // Obtener label en español para el estado
    const getStatusLabel = (status) => {
        const option = STATUS_OPTIONS.find(opt => opt.value === status);
        return option ? option.label : status;
    };

    // Calcular días de atraso
    const getDaysOverdue = (returnDate) => {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const dueDate = new Date(returnDate + 'T00:00:00');
        dueDate.setHours(0, 0, 0, 0);
        const diffTime = today - dueDate;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays;
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
                Préstamos Activos
            </Typography>

            {/* Sección de Filtros */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 'bold', mb: 2 }}>
                    Filtros
                </Typography>

                <Grid container spacing={2} alignItems="center">
                    <Grid size={{ xs: 12, md: 3 }}>
                        <FormControl fullWidth>
                            <InputLabel>Estado</InputLabel>
                            <Select
                                value={statusFilter}
                                label="Estado"
                                onChange={(e) => setStatusFilter(e.target.value)}
                            >
                                {STATUS_OPTIONS.map(option => (
                                    <MenuItem key={option.value} value={option.value}>
                                        {option.label}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>

                    {/* Filtro fecha inicio */}
                    <Grid size={{ xs: 12, md: 3 }}>
                        <Box
                            onClick={() => document.getElementById('start-date-picker-active').showPicker?.()}
                            sx={{ cursor: 'pointer' }}
                        >
                            <TextField
                                id="start-date-picker-active"
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
                    <Grid size={{ xs: 12, md: 3 }}>
                        <Box
                            onClick={() => document.getElementById('end-date-picker-active').showPicker?.()}
                            sx={{ cursor: 'pointer' }}
                        >
                            <TextField
                                id="end-date-picker-active"
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

                    <Grid size={{ xs: 12, md: 1.5 }}>
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

                    <Grid size={{ xs: 12, md: 1.5 }}>
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
                            Total Préstamos
                        </Typography>
                        <Typography variant="h6" sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                            {filteredLoans.length}
                        </Typography>
                    </Box>
                    <Box>
                        <Typography variant="body2" color="textSecondary">
                            Activos
                        </Typography>
                        <Typography variant="h6" sx={{ color: '#4caf50', fontWeight: 'bold' }}>
                            {filteredLoans.filter(l => l.status === 'activo').length}
                        </Typography>
                    </Box>
                    <Box>
                        <Typography variant="body2" color="textSecondary">
                            Vencidos
                        </Typography>
                        <Typography variant="h6" sx={{ color: '#f44336', fontWeight: 'bold' }}>
                            {filteredLoans.filter(l => l.status === 'vencido').length}
                        </Typography>
                    </Box>
                    <Box>
                        <Typography variant="body2" color="textSecondary">
                            Evaluación Pendiente
                        </Typography>
                        <Typography variant="h6" sx={{ color: '#666', fontWeight: 'bold' }}>
                            {filteredLoans.filter(l => l.status === 'evaluación pendiente').length}
                        </Typography>
                    </Box>
                    <Box>
                        <Typography variant="body2" color="textSecondary">
                            Multa Pendiente
                        </Typography>
                        <Typography variant="h6" sx={{ color: '#ff9800', fontWeight: 'bold' }}>
                            {filteredLoans.filter(l => l.status === 'multa pendiente').length}
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

            {/* Tabla de préstamos */}
            <TableContainer component={Paper}>
                <Table>
                    <TableHead sx={{ backgroundColor: '#0A142E' }}>
                        <TableRow>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>ID</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Cliente</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>RUT</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Herramienta</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Categoría</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Fecha Préstamo</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Fecha Retorno</TableCell>
                            <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Estado</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {filteredLoans.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={8} align="center">
                                    <Typography variant="body1" sx={{ py: 3, color: '#666' }}>
                                        No se encontraron préstamos activos con los filtros aplicados
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            filteredLoans.map((loan) => (
                                <TableRow
                                    key={loan.id}
                                    sx={{
                                        '&:hover': { backgroundColor: '#f5f5f5' }
                                    }}
                                >
                                    <TableCell>{loan.id}</TableCell>
                                    <TableCell>{loan.customer.name}</TableCell>
                                    <TableCell>{loan.customer.rut}</TableCell>
                                    <TableCell>{loan.tool.name}</TableCell>
                                    <TableCell>{loan.tool.category.name}</TableCell>
                                    <TableCell>{formatDate(loan.loanDate)}</TableCell>
                                    <TableCell>{formatDate(loan.returnDate)}</TableCell>
                                    <TableCell>
                                        <Chip
                                            icon={getStatusIcon(loan.status)}
                                            label={getStatusLabel(loan.status)}
                                            color={getStatusColor(loan.status)}
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

export default ActiveLoansList;