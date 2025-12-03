import { useEffect, useState } from "react";
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Typography,
    Box,
    CircularProgress,
    Alert,
    FormControl,
    Select,
    MenuItem,
    Card,
    CardContent,
    Button,
    TextField
} from '@mui/material';
import { FilterList as FilterIcon, Clear as ClearIcon } from '@mui/icons-material';
import fineService from "../services/fine.service";

const FineList = () => {
    const [fines, setFines] = useState([]);
    const [filteredFines, setFilteredFines] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Estados para los filtros
    const [statusFilter, setStatusFilter] = useState('');
    const [typeFilter, setTypeFilter] = useState('');
    const [rutCustomerFilter, setRutCustomerFilter] = useState('');

    // Opciones predefinidas del sistema
    const statusOptions = [
        { value: 'pagada', label: 'Pagada' },
        { value: 'no pagada', label: 'No Pagada' }
    ];

    const typeOptions = [
        { value: 'atraso', label: 'Atraso' },
        { value: 'daño leve', label: 'Daño Leve' },
        { value: 'daño irreparable', label: 'Daño Irreparable' }
    ];

    // Función para formatear RUT - limpia y formatea correctamente
    const formatRut = (value) => {
        if (!value) return '';

        // Limpiar el RUT: eliminar puntos, guiones y espacios, dejar solo números y K
        const cleaned = value.toString().replace(/[^0-9kK]/g, '');
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

        // Retornar con guion
        return `${formattedBody}-${dv}`;
    };

    // Obtener clientes únicos de las multas (ya no se usa, pero lo dejo por si acaso)
    const availableCustomers = Array.from(
        new Map(
            fines
                .filter(fine => fine.customer)
                .map(fine => [fine.customer.rut, fine.customer])
        ).values()
    );

    // Función para manejar el cambio en el campo RUT
    const handleRutChange = (e) => {
        const value = e.target.value;
        setRutCustomerFilter(formatRut(value));
    };

    const init = () => {
        setLoading(true);
        fineService
            .getAll()
            .then((response) => {
                console.log("Mostrando listado de todas las multas", response.data);
                const sortedFines = response.data.sort((a, b) => a.id - b.id);
                setFines(sortedFines);
                setFilteredFines(sortedFines);
                setError(null);
            })
            .catch((error) => {
                console.log("Error al intentar mostrar listado de las multas", error);
                setError("Error al cargar los registros de las multas");
            })
            .finally(() => {
                setLoading(false);
            });
    };

    // Función para aplicar los filtros
    const applyFilters = () => {
        const hasStatusFilter = statusFilter !== '';
        const hasTypeFilter = typeFilter !== '';
        const hasRutFilter = rutCustomerFilter !== '';

        setLoading(true);

        // Caso 1: Tres filtros activos (RUT + estado + tipo)
        if (hasRutFilter && hasStatusFilter && hasTypeFilter) {
            fineService
                .findFineByCustomerRutAndStatusAndType(rutCustomerFilter, statusFilter, typeFilter)
                .then((response) => {
                    console.log(`Multas del cliente ${rutCustomerFilter}, estado ${statusFilter}, tipo ${typeFilter}`, response.data);
                    const sortedFines = response.data.sort((a, b) => a.id - b.id);
                    setFilteredFines(sortedFines);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por rut, estado y tipo", error);
                    setError("No se encontraron multas con los filtros aplicados");
                    setFilteredFines([]);
                })
                .finally(() => setLoading(false));
        }
        // Caso 2: Dos filtros activos (RUT + estado)
        else if (hasRutFilter && hasStatusFilter) {
            fineService
                .getFineByCustomerRutAndStatus(rutCustomerFilter, statusFilter)
                .then((response) => {
                    console.log(`Multas del cliente ${rutCustomerFilter} con estado ${statusFilter}`, response.data);
                    const sortedFines = response.data.sort((a, b) => a.id - b.id);
                    setFilteredFines(sortedFines);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por RUT y estado", error);
                    setError("No se encontraron multas con los filtros aplicados");
                    setFilteredFines([]);
                })
                .finally(() => setLoading(false));
        }
        // Caso 3: Dos filtros activos (RUT + tipo)
        else if (hasRutFilter && hasTypeFilter) {
            fineService
                .findFineByCustomerRutAndType(rutCustomerFilter, typeFilter)
                .then((response) => {
                    console.log(`Multas del cliente ${rutCustomerFilter} con tipo ${typeFilter}`, response.data);
                    const sortedFines = response.data.sort((a, b) => a.id - b.id);
                    setFilteredFines(sortedFines);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por RUT y tipo", error);
                    setError("No se encontraron multas con los filtros aplicados");
                    setFilteredFines([]);
                })
                .finally(() => setLoading(false));
        }
        // Caso 4: Un filtro activo (RUT)
        else if (hasRutFilter) {
            fineService
                .getFineByCustomerRut(rutCustomerFilter)
                .then((response) => {
                    console.log(`Multas del cliente ${rutCustomerFilter}`, response.data);
                    const sortedFines = response.data.sort((a, b) => a.id - b.id);
                    setFilteredFines(sortedFines);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por RUT", error);
                    setError("No se encontraron multas con los filtros aplicados");
                    setFilteredFines([]);
                })
                .finally(() => setLoading(false));
        }
        // Caso 5: Un filtro activo (estado)
        else if (hasStatusFilter) {
            fineService
                .getByStatus(statusFilter)
                .then((response) => {
                    console.log(`Multas con estado ${statusFilter}`, response.data);
                    const sortedFines = response.data.sort((a, b) => a.id - b.id);
                    setFilteredFines(sortedFines);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por estado", error);
                    setError("No se encontraron multas con los filtros aplicados");
                    setFilteredFines([]);
                })
                .finally(() => setLoading(false));
        }
        // Caso 6: Un filtro activo (tipo)
        else if (hasTypeFilter) {
            fineService
                .getFinesByType(typeFilter)
                .then((response) => {
                    console.log(`Multas con tipo ${typeFilter}`, response.data);
                    const sortedFines = response.data.sort((a, b) => a.id - b.id);
                    setFilteredFines(sortedFines);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por tipo", error);
                    setError("No se encontraron multas con los filtros aplicados");
                    setFilteredFines([]);
                })
                .finally(() => setLoading(false));
        }
        // Caso 7: Sin filtros
        else {
            setFilteredFines(fines);
            setLoading(false);
        }
    };

    // Función para limpiar los filtros
    const clearFilters = () => {
        setRutCustomerFilter('');
        setStatusFilter('');
        setTypeFilter('');
        setFilteredFines(fines);
    };

    // Función para obtener el color según el tipo de multa
    const getTypeColor = (type) => {
        const colors = {
            'atraso': '#ffeb3b',
            'daño leve': '#ff9800',
            'daño irreparable': '#f44336'
        };
        return colors[type?.toLowerCase()] || '#9e9e9e';
    };

    // Función para obtener el color según el estado de la multa
    const getStatusColor = (status) => {
        const colors = {
            'pagada': '#4caf50',
            'no pagada': '#f44336'
        };
        return colors[status?.toLowerCase()] || '#9e9e9e';
    };

    // Función para formatear moneda
    const formatCurrency = (value) => {
        return new Intl.NumberFormat('es-CL', {
            style: 'currency',
            currency: 'CLP'
        }).format(value);
    };

    useEffect(() => {
        init();
    }, []);

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    if (error && fines.length === 0) {
        return (
            <Box sx={{ p: 3 }}>
                <Alert severity="error">{error}</Alert>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                Lista de Multas
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

                    <Box sx={{
                        display: 'flex',
                        gap: 2,
                        alignItems: 'center',
                        flexWrap: 'wrap'
                    }}>
                        {/* Filtro por RUT - TextField simple con formateo */}
                        <Box sx={{ flex: '2 1 400px', minWidth: '300px' }}>
                            <TextField
                                fullWidth
                                size="small"
                                label="Buscar por RUT"
                                value={rutCustomerFilter}
                                onChange={handleRutChange}
                                onKeyPress={(e) => e.key === 'Enter' && applyFilters()}
                                placeholder="Ej: 12.345.678-9"
                                variant="outlined"
                            />
                        </Box>

                        {/* Filtro por estado - flex: 1 más pequeño */}
                        <Box sx={{ flex: '1 1 150px', minWidth: '120px' }}>
                            <FormControl fullWidth size="small">
                                <Select
                                    value={statusFilter}
                                    onChange={(e) => setStatusFilter(e.target.value)}
                                    displayEmpty
                                    renderValue={(selected) => {
                                        if (!selected) {
                                            return <span style={{ color: '#999' }}>Estados</span>;
                                        }
                                        const status = statusOptions.find(s => s.value === selected);
                                        return status ? status.label : selected;
                                    }}
                                >
                                    <MenuItem value="">
                                        Todos los estados
                                    </MenuItem>
                                    {statusOptions.map((status) => (
                                        <MenuItem key={status.value} value={status.value}>
                                            {status.label}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Box>

                        {/* Filtro por tipo - flex: 1 más pequeño */}
                        <Box sx={{ flex: '1 1 150px', minWidth: '120px' }}>
                            <FormControl fullWidth size="small">
                                <Select
                                    value={typeFilter}
                                    onChange={(e) => setTypeFilter(e.target.value)}
                                    displayEmpty
                                    renderValue={(selected) => {
                                        if (!selected) {
                                            return <span style={{ color: '#999' }}>Tipos</span>;
                                        }
                                        const type = typeOptions.find(t => t.value === selected);
                                        return type ? type.label : selected;
                                    }}
                                >
                                    <MenuItem value="">
                                        Todos los tipos
                                    </MenuItem>
                                    {typeOptions.map((type) => (
                                        <MenuItem key={type.value} value={type.value}>
                                            {type.label}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Box>

                        {/* Botones - flex: 1 */}
                        <Box sx={{ flex: '1 1 200px', minWidth: '180px', display: 'flex', gap: 1 }}>
                            <Button
                                variant="contained"
                                size="small"
                                onClick={applyFilters}
                                disabled={!rutCustomerFilter && !statusFilter && !typeFilter}
                                sx={{ flex: 1 }}
                            >
                                Filtrar
                            </Button>
                            <Button
                                variant="outlined"
                                size="small"
                                startIcon={<ClearIcon />}
                                onClick={clearFilters}
                                disabled={!rutCustomerFilter && !statusFilter && !typeFilter}
                                sx={{ flex: 1 }}
                            >
                                Limpiar
                            </Button>
                        </Box>
                    </Box>


                    {/* Indicador de resultados */}
                    <Typography variant="body2" sx={{ mt: 2, color: '#666' }}>
                        Mostrando {filteredFines.length} multa(s)
                        {rutCustomerFilter && ` • Cliente RUT: ${formatRut(rutCustomerFilter)}`}
                        {statusFilter && ` • Estado: ${statusOptions.find(s => s.value === statusFilter)?.label}`}
                        {typeFilter && ` • Tipo: ${typeOptions.find(t => t.value === typeFilter)?.label}`}
                    </Typography>
                </CardContent>
            </Card>

            {error && (
                <Alert severity="warning" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            {filteredFines.length === 0 ? (
                <Alert severity="info">
                    {fines.length === 0
                        ? "No hay multas registradas"
                        : "No se encontraron multas con los filtros aplicados"
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
                                <TableCell><strong>ID Préstamo</strong></TableCell>
                                <TableCell><strong>Tipo</strong></TableCell>
                                <TableCell><strong>Valor</strong></TableCell>
                                <TableCell align="center"><strong>Estado</strong></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filteredFines.map((fine) => (
                                <TableRow
                                    key={fine.id}
                                    sx={{ '&:hover': { backgroundColor: '#f9f9f9' } }}
                                >
                                    <TableCell>{fine.id}</TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="medium">
                                            {fine.customer?.name || 'N/A'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Typography variant="body2">
                                            {fine.customer?.rut ? formatRut(fine.customer.rut) : 'N/A'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Typography variant="body2">
                                            {fine.loan?.id || 'N/A'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Typography
                                            variant="body2"
                                            fontWeight="medium"
                                            sx={{
                                                color: getTypeColor(fine.type),
                                                textTransform: 'capitalize'
                                            }}
                                        >
                                            {fine.type || 'N/A'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="medium">
                                            {formatCurrency(fine.fineValue || 0)}
                                        </Typography>
                                    </TableCell>
                                    <TableCell align="center">
                                        <Box
                                            sx={{
                                                display: 'inline-block',
                                                px: 2,
                                                py: 0.5,
                                                borderRadius: 1,
                                                backgroundColor: getStatusColor(fine.status),
                                                color: 'white'
                                            }}
                                        >
                                            <Typography
                                                variant="body2"
                                                fontWeight="medium"
                                                sx={{ textTransform: 'capitalize' }}
                                            >
                                                {fine.status || 'N/A'}
                                            </Typography>
                                        </Box>
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

export default FineList;