import { useState, useEffect } from "react";
import {
    Box,
    Card,
    CardContent,
    Typography,
    TextField,
    Button,
    Alert,
    CircularProgress,
    Grid,
    FormControl,
    Select,
    MenuItem,
    Table,
    TableContainer,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    Paper,
    Chip
} from "@mui/material";
import FilterIcon from "@mui/icons-material/Filter";
import ClearIcon from "@mui/icons-material/Clear";
import loanService from "../services/loan.service";

const LoansList = () => {
    const [loans, setLoans] = useState([]);
    const [filtered, setFiltered] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [statusFilter, setStatusFilter] = useState('');
    const [searchRut, setSearchRut] = useState('');
    const [searchDate, setSearchDate] = useState('');

    const statuses = ['activo', 'vencido', 'evaluación pendiente', 'multa pendiente', 'finalizado', 'finalizado con multa'];

    // Función para formatear RUT en tiempo real
    const formatRut = (value) => {
        const cleaned = value.replace(/[^0-9kK]/g, '');
        if (!cleaned) return '';

        // Limitar a máximo 9 caracteres (8 dígitos + 1 dígito verificador)
        const limited = cleaned.slice(0, 9);

        // Si solo hay un carácter, devolverlo tal cual
        if (limited.length === 1) return limited;

        // Separar cuerpo y dígito verificador
        const body = limited.slice(0, -1);
        const dv = limited.slice(-1).toUpperCase();

        // Formatear el cuerpo con puntos
        const formattedBody = body.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

        // Retornar con guion desde que hay al menos 2 caracteres
        return `${formattedBody}-${dv}`;
    };


    const init = () => {
        setLoading(true);
        loanService
            .getAll()
            .then((response) => {
                console.log("Mostrando préstamos:", response.data);

                const sortedLoans = response.data.sort((a, b) => a.id - b.id);

                setLoans(sortedLoans);
                setFiltered(sortedLoans);
                setError(null);
            })
            .catch((error) => {
                console.log("Error obteniendo préstamos:", error);
                setError("Error obteniendo préstamos");
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const handleStatusFilter = (status) => {
        setStatusFilter(status);
        setSearchRut('');
        setSearchDate('');
        setLoading(true);

        if (status === '') {
            init();
        } else {
            loanService
                .findLoanByStatus(status)
                .then((response) => {
                    console.log(`Mostrando préstamos con estado ${status}:`, response.data);

                    const sortedFilteredLoans = response.data.sort((a, b) => a.id - b.id);
                    setFiltered(sortedFilteredLoans);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por estado:", error);
                    setError("Error al cargar los préstamos filtrados por estado");
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    };

    // Buscar por RUT
    const handleSearchByRut = () => {
        if (!searchRut.trim()) {
            setError('Por favor ingresa un RUT para buscar');
            return;
        }

        setLoading(true);
        setStatusFilter('');
        setSearchDate('');

        // Enviar el RUT CON formato (tal como está en el estado)
        loanService
            .findLoanByCustomerRut(searchRut.trim())
            .then((response) => {
                console.log(`Mostrando préstamos del cliente con RUT ${searchRut}:`, response.data);

                const sortedLoans = response.data.sort((a, b) => a.id - b.id);
                setFiltered(sortedLoans);
                setError(null);
            })
            .catch((error) => {
                console.log("Error al buscar por RUT:", error);
                if (error.response?.status === 404) {
                    setError(`No se encontraron préstamos para el RUT: ${searchRut}`);
                } else {
                    setError('Error al buscar préstamos por RUT');
                }
                setFiltered([]);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    // Buscar por RUT y Estado
    const handleSearchByRutAndStatus = () => {
        if (!searchRut.trim() || !statusFilter) {
            setError('Por favor ingresa un RUT y selecciona un estado');
            return;
        }

        setLoading(true);
        setSearchDate('');

        // Enviar el RUT CON formato (tal como está en el estado)
        loanService
            .findLoanByCustomerRutAndStatus(searchRut.trim(), statusFilter)
            .then((response) => {
                console.log(`Mostrando préstamos del cliente RUT ${searchRut} con estado ${statusFilter}:`, response.data);

                const sortedLoans = response.data.sort((a, b) => a.id - b.id);
                setFiltered(sortedLoans);
                setError(null);
            })
            .catch((error) => {
                console.log("Error al buscar por RUT y estado:", error);
                setError('Error al buscar préstamos por cliente y estado');
                setFiltered([]);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    // Buscar por fecha
    const handleSearchByDate = () => {
        if (!searchDate.trim()) {
            setError('Por favor ingresa una fecha para buscar');
            return;
        }

        setLoading(true);
        setStatusFilter('');
        setSearchRut('');

        loanService
            .findLoanByReturnDate(searchDate)
            .then((response) => {
                console.log(`Mostrando préstamos con fecha de devolución ${searchDate}:`, response.data);

                const sortedLoans = response.data.sort((a, b) => a.id - b.id);
                setFiltered(sortedLoans);
                setError(null);
            })
            .catch((error) => {
                console.log("Error al buscar por fecha:", error);
                setError('Error al buscar préstamos por fecha');
                setFiltered([]);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const clearFilters = () => {
        setStatusFilter('');
        setSearchRut('');
        setSearchDate('');
        init();
    };

    useEffect(() => {
        init();
    }, []);

    const getStatusColor = (status) => {
        switch (status?.toLowerCase()) {
            case 'activo':
                return 'success';
            case 'vencido':
            case 'pendiente multa':
            case 'finalizado con multa':
                return 'warning';
            case 'finalizado':
                return 'info';
            case 'pendiente evaluación':
                return 'default';
            default:
                return 'default';
        }
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    if (error && loans.length === 0) {
        return (
            <Box sx={{ p: 3 }}>
                <Alert severity="error">{error}</Alert>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                Lista de Préstamos
            </Typography>

            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <FilterIcon sx={{ mr: 1, color: '#0A142E' }} />
                        <Typography variant="h6" sx={{ color: '#0A142E' }}>
                            Filtros
                        </Typography>
                    </Box>

                    <Grid container spacing={2} alignItems="flex-end">
                        {/* Filtro por estado */}
                        <Grid item xs={12} sm={6} md={3}>
                            <Box>
                                <Typography variant="caption" sx={{ mb: 0.5, display: 'block', color: '#666' }}>
                                    Estado
                                </Typography>
                                <FormControl fullWidth size="small">
                                    <Select
                                        value={statusFilter}
                                        onChange={(e) => handleStatusFilter(e.target.value)}
                                        displayEmpty
                                        sx={{
                                            '& .MuiSelect-select': {
                                                padding: '8.5px 14px',
                                                fontSize: '14px'
                                            }
                                        }}
                                    >
                                        <MenuItem value="">
                                            <em>Seleccionar Estado</em>
                                        </MenuItem>
                                        {statuses.map((status) => (
                                            <MenuItem key={status} value={status}>
                                                {status}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Box>
                        </Grid>

                        {/* Búsqueda por RUT */}
                        <Grid item xs={12} sm={6} md={3}>
                            <Box>
                                <Typography variant="caption" sx={{ mb: 0.5, display: 'block', color: '#666' }}>
                                    RUT del Cliente
                                </Typography>
                                <TextField
                                    fullWidth
                                    size="small"
                                    value={searchRut}
                                    onChange={(e) => setSearchRut(formatRut(e.target.value))}
                                    onKeyPress={(e) => e.key === 'Enter' && handleSearchByRut()}
                                    placeholder="Ej: 12.345.678-9"
                                />
                            </Box>
                        </Grid>

                        {/* Búsqueda por fecha */}
                        <Grid item xs={12} sm={6} md={3}>
                            <Box>
                                <Typography variant="caption" sx={{ mb: 0.5, display: 'block', color: '#666' }}>
                                    Fecha de Devolución
                                </Typography>
                                <Box
                                    onClick={() => document.getElementById('date-picker-input').showPicker?.()}
                                    sx={{ cursor: 'pointer' }}
                                >
                                    <TextField
                                        id="date-picker-input"
                                        fullWidth
                                        size="small"
                                        type="date"
                                        value={searchDate}
                                        onChange={(e) => setSearchDate(e.target.value)}
                                        onKeyPress={(e) => e.key === 'Enter' && handleSearchByDate()}
                                        InputLabelProps={{ shrink: true }}
                                        sx={{
                                            '& input': {
                                                cursor: 'pointer'
                                            }
                                        }}
                                    />
                                </Box>
                            </Box>
                        </Grid>

                        {/* Botón Limpiar */}
                        <Grid item xs={12} sm={6} md={3}>
                            <Button
                                variant="outlined"
                                size="small"
                                fullWidth
                                startIcon={<ClearIcon />}
                                onClick={clearFilters}
                                disabled={!statusFilter && !searchRut && !searchDate}
                                sx={{ height: '40px' }}
                            >
                                Limpiar
                            </Button>
                        </Grid>
                    </Grid>

                    {/* Botones de búsqueda */}
                    <Grid container spacing={2} sx={{ mt: 1 }}>
                        <Grid item xs={12} sm={4}>
                            <Button
                                variant="contained"
                                size="small"
                                fullWidth
                                onClick={handleSearchByRut}
                                disabled={!searchRut.trim()}
                            >
                                Buscar por RUT
                            </Button>
                        </Grid>
                        <Grid item xs={12} sm={4}>
                            <Button
                                variant="contained"
                                size="small"
                                fullWidth
                                onClick={handleSearchByDate}
                                disabled={!searchDate.trim()}
                            >
                                Buscar por Fecha
                            </Button>
                        </Grid>
                        <Grid item xs={12} sm={4}>
                            <Button
                                variant="contained"
                                size="small"
                                fullWidth
                                onClick={handleSearchByRutAndStatus}
                                disabled={!searchRut.trim() || !statusFilter}
                            >
                                RUT + Estado
                            </Button>
                        </Grid>
                    </Grid>

                    <Typography variant="body2" sx={{ mt: 2, color: '#666' }}>
                        Mostrando {filtered.length} préstamos
                        {statusFilter && ` con estado: ${statusFilter}`}
                        {searchRut && ` del cliente RUT: ${searchRut}`}
                        {searchDate && ` con fecha de devolución: ${searchDate}`}
                    </Typography>
                </CardContent>
            </Card>

            {error && (
                <Alert severity="warning" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            {filtered.length === 0 ? (
                <Alert severity="info">
                    {loans.length === 0
                        ? "No hay préstamos registrados"
                        : "No se encontraron préstamos con los filtros aplicados"
                    }
                </Alert>
            ) : (
                <TableContainer component={Paper} sx={{ mt: 2 }}>
                    <Table sx={{ minWidth: 650 }}>
                        <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
                            <TableRow>
                                <TableCell><strong>ID</strong></TableCell>
                                <TableCell><strong>Cliente</strong></TableCell>
                                <TableCell><strong>RUT</strong></TableCell>
                                <TableCell><strong>Herramienta</strong></TableCell>
                                <TableCell><strong>Fecha Préstamo</strong></TableCell>
                                <TableCell><strong>Fecha Devolución</strong></TableCell>
                                <TableCell><strong>Valor</strong></TableCell>
                                <TableCell><strong>Estado</strong></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filtered.map((loan) => (
                                <TableRow
                                    key={loan.id}
                                    sx={{ '&:hover': { backgroundColor: '#f9f9f9' } }}
                                >
                                    <TableCell>{loan.id}</TableCell>
                                    <TableCell>{loan.customer?.name || 'Sin nombre'}</TableCell>
                                    <TableCell>{loan.customer?.rut || 'Sin RUT'}</TableCell>
                                    <TableCell>{loan.tool?.name || 'Sin herramienta'}</TableCell>
                                    <TableCell>{loan.loanDate || 'N/A'}</TableCell>
                                    <TableCell>{loan.returnDate || 'N/A'}</TableCell>
                                    <TableCell>${loan.loanValue || 0}</TableCell>
                                    <TableCell>
                                        <Chip
                                            label={loan.status || 'Sin estado'}
                                            color={getStatusColor(loan.status)}
                                            size="small"
                                        />
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </Box>
    );
};

export default LoansList;