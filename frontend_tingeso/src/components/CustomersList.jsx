import { useEffect, useState } from "react";
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
import customerService from "../services/customer.service";

const CustomersList = () => {
    const [customers, setCustomers] = useState([]);
    const [filtered, setFiltered] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    //Estados para filtros
    const [statusFilter, setStatusFilter] = useState('');
    const [searchRut, setSearchRut] = useState('');

    //Estados predefinidos del sistema
    const statuses = ['activo', 'restringido'];

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
        customerService
            .getAll()
            .then((response) => {
                console.log("Mostrando clientes:", response.data);

                //Ordenar clientes por ID ascendentemente
                const sortedCustomers = response.data.sort((a, b) => a.id - b.id);

                setCustomers(sortedCustomers);
                setFiltered(sortedCustomers);

                setError(null);
            })
            .catch((error) => {
                console.log("Error obteniendo clientes:", error);
                setError("Error obteniendo clientes");
            })
            .finally(() => {
                setLoading(false);
            });
    };

    //Función para filtrar clientes por estado
    const handleStatusFilter = (status) => {
        setStatusFilter(status);
        setSearchRut(''); // Limpiar búsqueda por RUT
        setLoading(true);

        //Si no hay filtros, se muestran todos los clientes
        if (status === '') {
            init();
        } else {
            //Se llama al back para filtrar por estado
            customerService
                .getByStatus(status)
                .then((response) => {
                    console.log(`Mostrando clientes con estado ${status}:`, response.data);

                    //Ordenar clientes por ID ascendentemente
                    const sortedFilteredCustomers = response.data.sort((a, b) => a.id - b.id);
                    setFiltered(sortedFilteredCustomers);
                    setError(null);
                })
                .catch((error) => {
                    console.log(
                        "Se ha producido un error al intentar mostrar listado de clientes filtradas por estado",
                        error
                    );
                    setError("Error al cargar los clientes filtrados por estado");
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    };

    //Función para filtrar clientes por RUT
    const handleSearch = () => {
        //trim elimina espacios en blanco al inicio y al final
        if (!searchRut.trim()) {
            setError("Por favor ingresa un rut para buscar");
            return;
        }

        setLoading(true);
        const rut = searchRut.trim();
        setStatusFilter(''); // Limpiar filtro de estado

        //Buscar por rut (enviando con formato)
        customerService
            .getByRut(rut)
            .then((response) => {
                console.log(`Mostrando cliente con RUT: ${rut}`, response.data);
                setFiltered([response.data]);
                setError(null);
            })
            .catch((error) => {
                console.log("Error al buscar por RUT", error);
                if (error.response?.status === 404) {
                    setError(`No se encontró cliente con RUT: ${rut}`);
                } else {
                    setError("Error al buscar el cliente por RUT");
                }
                setFiltered([]);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    //Función para limpiar los filtros
    const clearFilters = () => {
        setStatusFilter('');
        setSearchRut('');
        init();
    };

    useEffect(() => {
        init();
    }, []);

    const getStatusColor = (status) => {
        switch (status?.toLowerCase()) {
            case 'activo':
                return 'success';
            case 'restringido':
                return 'warning';
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

    if (error && customers.length === 0) {
        return (
            <Box sx={{ p: 3 }}>
                <Alert severity="error">{error}</Alert>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                Lista de Clientes
            </Typography>

            {/* Filtros */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <FilterIcon sx={{ mr: 1, color: '#0A142E' }} />
                        <Typography variant="h6" sx={{ color: '#0A142E' }}>
                            Filtros
                        </Typography>
                    </Box>

                    <Grid container spacing={2} alignItems="center">
                        {/* Primera fila: Filtros principales */}
                        <Grid item xs={12} sm={6}>
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
                                        <em>Filtrar por Estado</em>
                                    </MenuItem>
                                    {statuses.map((status) => (
                                        <MenuItem key={status} value={status}>
                                            {status.replace('_', ' ')}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        {/* Segunda fila: Búsqueda y botones */}
                        <Grid item xs={12} sm={8}>
                            <TextField
                                fullWidth
                                size="small"
                                value={searchRut}
                                onChange={(e) => setSearchRut(formatRut(e.target.value))}
                                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                placeholder="Buscar por rut (ej: 12.345.678-9)..."
                            />
                        </Grid>

                        <Grid item xs={12} sm={4}>
                            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                                <Button
                                    variant="contained"
                                    size="small"
                                    onClick={handleSearch}
                                    disabled={!searchRut.trim()}
                                >
                                    Buscar
                                </Button>
                                <Button
                                    variant="outlined"
                                    size="small"
                                    startIcon={<ClearIcon />}
                                    onClick={clearFilters}
                                    disabled={!statusFilter && !searchRut}
                                >
                                    Limpiar
                                </Button>
                            </Box>
                        </Grid>
                    </Grid>

                    {/* Indicador de resultados */}
                    <Typography variant="body2" sx={{ mt: 2, color: '#666' }}>
                        Mostrando {filtered.length} clientes
                        {statusFilter && ` con estado: ${statusFilter.replace('_', ' ')}`}
                        {searchRut && ` con búsqueda: ${searchRut}`}
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
                    {customers.length === 0
                        ? "No hay clientes registrados"
                        : "No se encontraron clientes con los filtros aplicados"
                    }
                </Alert>
            ) : (
                <TableContainer component={Paper} sx={{ mt: 2 }}>
                    <Table sx={{ minWidth: 650 }}>
                        <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
                            <TableRow>
                                <TableCell><strong>ID</strong></TableCell>
                                <TableCell><strong>Nombre</strong></TableCell>
                                <TableCell><strong>Rut</strong></TableCell>
                                <TableCell><strong>Email</strong></TableCell>
                                <TableCell><strong>Teléfono</strong></TableCell>
                                <TableCell><strong>Estado</strong></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filtered.map((customer) => (
                                <TableRow
                                    key={customer.id}
                                    sx={{ '&:hover': { backgroundColor: '#f9f9f9' } }}
                                >
                                    <TableCell>{customer.id}</TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="medium">
                                            {customer.name || 'Sin nombre'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>{customer.rut || 'Sin RUT'}</TableCell>
                                    <TableCell>{customer.email || 'Sin email'}</TableCell>
                                    <TableCell>{customer.phoneNumber  || 'Sin teléfono'}</TableCell>
                                    <TableCell>
                                        <Chip
                                            label={customer.status || 'Sin estado'}
                                            color={getStatusColor(customer.status)}
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

export default CustomersList;