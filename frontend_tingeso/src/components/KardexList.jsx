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
    InputLabel,
    Select,
    MenuItem,
    Grid,
    Card,
    CardContent,
    Button,
    TextField
} from '@mui/material';
import { FilterList as FilterIcon, Clear as ClearIcon } from '@mui/icons-material';
import { useKeycloak } from "@react-keycloak/web";
import kardexService from "../services/kardex.service";

const KardexList = () => {
    const { keycloak } = useKeycloak();
    const [records, setRecords] = useState([]);
    const [filteredRecords, setFilteredRecords] = useState([]);
    const [availableTools, setAvailableTools] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Estados para los filtros
    const [toolFilter, setToolFilter] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    // Obtener el username del usuario logueado en Keycloak
    const currentUsername = keycloak.tokenParsed?.preferred_username || 'Usuario desconocido';

    const init = () => {
        setLoading(true);
        kardexService
            .getAll()
            .then((response) => {
                console.log("Mostrando listado de todos los movimientos", response.data);

                const sortedRecords = response.data.sort((a, b) => a.id - b.id);
                setRecords(sortedRecords);
                setFilteredRecords(sortedRecords);

                // Extraer herramientas únicas para el filtro
                const uniqueTools = [...new Map(
                    sortedRecords
                        .filter(record => record.tool)
                        .map(record => [record.tool.id, record.tool])
                ).values()];
                setAvailableTools(uniqueTools);

                setError(null);
            })
            .catch((error) => {
                console.log(
                    "Se ha producido un error al intentar mostrar listado de los movimientos",
                    error
                );
                setError("Error al cargar los registros de movimientos");
            })
            .finally(() => {
                setLoading(false);
            });
    };

    // Función para aplicar los filtros
    const applyFilters = () => {
        const hasToolFilter = toolFilter !== '';
        const hasDateFilter = startDate !== '' && endDate !== '';

        setLoading(true);

        // Caso 1: Ambos filtros activos (herramienta + rango de fechas)
        if (hasToolFilter && hasDateFilter) {
            kardexService
                .getToolMovementsByDateRange(toolFilter, startDate, endDate)
                .then((response) => {
                    console.log(`Mostrando registros de herramienta ${toolFilter} entre ${startDate} y ${endDate}`, response.data);
                    const sortedRecords = response.data.sort((a, b) => a.id - b.id);
                    setFilteredRecords(sortedRecords);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por herramienta y fechas", error);
                    setError("Error al cargar los registros filtrados");
                    setFilteredRecords([]);
                })
                .finally(() => {
                    setLoading(false);
                });
        }
        // Caso 2: Solo filtro de herramienta
        else if (hasToolFilter) {
            kardexService
                .getToolMovementHistory(toolFilter)
                .then((response) => {
                    console.log(`Mostrando registros de herramienta ${toolFilter}`, response.data);
                    const sortedRecords = response.data.sort((a, b) => a.id - b.id);
                    setFilteredRecords(sortedRecords);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por herramienta", error);
                    setError("Error al cargar los registros de la herramienta");
                    setFilteredRecords([]);
                })
                .finally(() => {
                    setLoading(false);
                });
        }
        // Caso 3: Solo filtro de fechas
        else if (hasDateFilter) {
            kardexService
                .getMovementsByDateRange(startDate, endDate)
                .then((response) => {
                    console.log(`Mostrando registros entre ${startDate} y ${endDate}`, response.data);
                    const sortedRecords = response.data.sort((a, b) => a.id - b.id);
                    setFilteredRecords(sortedRecords);
                    setError(null);
                })
                .catch((error) => {
                    console.log("Error al filtrar por fechas", error);
                    setError("Error al cargar los registros por fechas");
                    setFilteredRecords([]);
                })
                .finally(() => {
                    setLoading(false);
                });
        }
        // Caso 4: Sin filtros
        else {
            setFilteredRecords(records);
            setLoading(false);
        }
    };

    // Función para limpiar los filtros
    const clearFilters = () => {
        setToolFilter('');
        setStartDate('');
        setEndDate('');
        setFilteredRecords(records);
    };

    // Función para formatear la fecha
    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        // Agregar 'T00:00:00' para forzar interpretación local en lugar de UTC
        const date = new Date(dateString + 'T00:00:00');
        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    };

    // Función para obtener el color según el tipo de movimiento
    const getTypeColor = (type) => {
        const colors = {
            'ingreso': '#2e7d32',
            'prestamo': '#1976d2',
            'devolucion': '#ed6c02',
            'baja': '#d32f2f',
            'reparacion': '#9c27b0'
        };
        return colors[type?.toLowerCase()] || '#757575';
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

    if (error) {
        return (
            <Box sx={{ p: 3 }}>
                <Alert severity="error">{error}</Alert>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                Lista de Movimientos
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

                    <Grid container spacing={2} alignItems="flex-end">
                        {/* Filtro por herramienta */}
                        <Grid item xs={12} sm={6} md={4}>
                            <Box>
                                <Typography variant="caption" sx={{ mb: 0.5, display: 'block', color: '#666' }}>
                                    Herramienta
                                </Typography>
                                <FormControl fullWidth size="small">
                                    <Select
                                        value={toolFilter}
                                        onChange={(e) => setToolFilter(e.target.value)}
                                        displayEmpty
                                        renderValue={(selected) => {
                                            if (!selected) {
                                                return <span>Todas las herramientas</span>;
                                            }
                                            const tool = availableTools.find(t => t.id === selected);
                                            return tool ? `${tool.name} (ID: ${tool.id})` : '';
                                        }}
                                    >
                                        <MenuItem value="">
                                            Todas las herramientas
                                        </MenuItem>
                                        {availableTools.map((tool) => (
                                            <MenuItem key={tool.id} value={tool.id}>
                                                {tool.name} (ID: {tool.id})
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Box>
                        </Grid>

                        {/* Filtro fecha inicio */}
                        <Grid item xs={12} sm={6} md={3}>
                            <Box>
                                <Typography variant="caption" sx={{ mb: 0.5, display: 'block', color: '#666' }}>
                                    Fecha Inicio
                                </Typography>
                                <Box
                                    onClick={() => document.getElementById('start-date-picker').showPicker?.()}
                                    sx={{ cursor: 'pointer' }}
                                >
                                    <TextField
                                        id="start-date-picker"
                                        fullWidth
                                        size="small"
                                        type="date"
                                        value={startDate}
                                        onChange={(e) => setStartDate(e.target.value)}
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        sx={{
                                            '& input': {
                                                cursor: 'pointer'
                                            }
                                        }}
                                    />
                                </Box>
                            </Box>
                        </Grid>

                        {/* Filtro fecha fin */}
                        <Grid item xs={12} sm={6} md={3}>
                            <Box>
                                <Typography variant="caption" sx={{ mb: 0.5, display: 'block', color: '#666' }}>
                                    Fecha Fin
                                </Typography>
                                <Box
                                    onClick={() => document.getElementById('end-date-picker').showPicker?.()}
                                    sx={{ cursor: 'pointer' }}
                                >
                                    <TextField
                                        id="end-date-picker"
                                        fullWidth
                                        size="small"
                                        type="date"
                                        value={endDate}
                                        onChange={(e) => setEndDate(e.target.value)}
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        sx={{
                                            '& input': {
                                                cursor: 'pointer'
                                            }
                                        }}
                                    />
                                </Box>
                            </Box>
                        </Grid>

                        {/* Botones */}
                        <Grid item xs={12} sm={6} md={2}>
                            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                                <Button
                                    variant="contained"
                                    size="small"
                                    onClick={applyFilters}
                                    disabled={!toolFilter && !startDate && !endDate}
                                    sx={{ flex: 1 }}
                                >
                                    Filtrar
                                </Button>
                                <Button
                                    variant="outlined"
                                    size="small"
                                    startIcon={<ClearIcon />}
                                    onClick={clearFilters}
                                    disabled={!toolFilter && !startDate && !endDate}
                                    sx={{ flex: 1 }}
                                >
                                    Limpiar
                                </Button>
                            </Box>
                        </Grid>
                    </Grid>

                    {/* Indicador de resultados */}
                    <Typography variant="body2" sx={{ mt: 2, color: '#666' }}>
                        Mostrando {filteredRecords.length} movimiento(s)
                        {toolFilter && ` • Herramienta ID: ${toolFilter}`}
                        {startDate && endDate && ` • Desde: ${startDate} hasta: ${endDate}`}
                    </Typography>
                </CardContent>
            </Card>

            {filteredRecords.length === 0 ? (
                <Alert severity="info">
                    {records.length === 0
                        ? "No hay movimientos registrados"
                        : "No se encontraron movimientos con los filtros aplicados"
                    }
                </Alert>
            ) : (
                <TableContainer component={Paper} sx={{ mt: 2 }}>
                    <Table sx={{ minWidth: 650 }}>
                        <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
                            <TableRow>
                                <TableCell><strong>ID</strong></TableCell>
                                <TableCell><strong>Tipo</strong></TableCell>
                                <TableCell><strong>Fecha</strong></TableCell>
                                <TableCell><strong>Usuario Responsable</strong></TableCell>
                                <TableCell><strong>Herramienta</strong></TableCell>
                                <TableCell align="center"><strong>Cantidad Afectada</strong></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filteredRecords.map((record) => (
                                <TableRow
                                    key={record.id}
                                    sx={{ '&:hover': { backgroundColor: '#f9f9f9' } }}
                                >
                                    <TableCell>{record.id}</TableCell>
                                    <TableCell>
                                        <Typography
                                            variant="body2"
                                            fontWeight="medium"
                                            sx={{
                                                color: getTypeColor(record.type),
                                                textTransform: 'capitalize'
                                            }}
                                        >
                                            {record.type || 'N/A'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>{formatDate(record.date)}</TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="medium">
                                            {record.username || 'N/A'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Typography variant="body2">
                                            {record.tool?.name || 'N/A'}
                                        </Typography>
                                        <Typography variant="caption" color="textSecondary">
                                            ID: {record.tool?.id || 'N/A'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell align="center">
                                        <Typography
                                            variant="body2"
                                            fontWeight="medium"
                                            sx={{
                                                color: record.affectedAmount > 0 ? '#2e7d32' : '#d32f2f'
                                            }}
                                        >
                                            {record.affectedAmount || 0}
                                        </Typography>
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

export default KardexList;