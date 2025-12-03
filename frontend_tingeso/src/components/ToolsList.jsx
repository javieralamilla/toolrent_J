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
    Chip,
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
import toolService from "../services/tool.service";

const ToolsList = () => {
    const [tools, setTools] = useState([]);
    const [filteredTools, setFilteredTools] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    //Estados para filtros
    const [statusFilter, setStatusFilter] = useState('');
    const [categoryFilter, setCategoryFilter] = useState('');
    const [searchId, setSearchId] = useState('');

    //Estados predefinidos del sistema - actualizados sin acentos
    const statuses = ['disponible', 'prestada', 'en reparacion', 'dada de baja'];
    const [availableCategories, setAvailableCategories] = useState([]);

    const init = () => {
        setLoading(true);
        toolService
            .getAll()
            .then((response) => {
                console.log("Mostrando listado de todas las herramientas", response.data);
                
                // Ordenar las herramientas por ID de menor a mayor
                const sortedTools = response.data.sort((a, b) => a.id - b.id);
                
                setTools(sortedTools);
                setFilteredTools(sortedTools);

                // Extraer categorías únicas para el filtro
                const categories = [...new Set(sortedTools.map(tool => tool.category?.name || tool.category).filter(Boolean))];
                setAvailableCategories(categories);
                
                // Extraer estados únicos para verificar formato correcto
                const backendStatuses = [...new Set(sortedTools.map(tool => tool.status).filter(Boolean))];
                console.log("Estados encontrados en el backend:", backendStatuses);
                
                setError(null);
            })
            .catch((error) => {
                console.log(
                    "Se ha producido un error al intentar mostrar listado de todas las herramientas",
                    error
                );
                setError("Error al cargar las herramientas");
            })
            .finally(() => {
                setLoading(false);
            });
    };

    //Funcion para filtrar por estado 
    const handleStatusFilter = (status) => {
        setStatusFilter(status);
        setLoading(true);

        if (status === '') {
            //Si no hay filtro, se cargan todas las herramientas
            init()
        } else {
            //Se llama al backend para filtrar por estado
            toolService
            .getByStatus(status)
            .then((response) => {
                console.log(`Mostrando listado de herramientas con estado: ${status}`, response.data);
                // Ordenar por ID las herramientas filtradas
                const sortedFilteredTools = response.data.sort((a, b) => a.id - b.id);
                setFilteredTools(sortedFilteredTools);
                setError(null);
            })
            .catch((error) => {
                console.log(
                    "Se ha producido un error al intentar mostrar listado de herramientas filtradas por estado",
                    error
                );
                setError("Error al cargar las herramientas");
            })
            .finally(() => {
                setLoading(false);
            });
        }
    };

    //Funcion para filtrar por categoria
    const handleCategoryFilter = (category) => {
        setCategoryFilter(category);
        setLoading(true);   
        if (category === '') {
            //Si no hay filtro, se cargan todas las herramientas
            init()
        } else {
            //Se llama al backend para filtrar por categoria
            toolService
            .getByCategory(category)
            .then((response) => {
                console.log(`Mostrando listado de herramientas con categoría: ${category}`, response.data);
                // Ordenar por ID las herramientas filtradas
                const sortedFilteredTools = response.data.sort((a, b) => a.id - b.id);
                setFilteredTools(sortedFilteredTools);
                setError(null);
            })
            .catch((error) => {
                console.log(
                    "Se ha producido un error al intentar mostrar listado de herramientas filtradas por categoría",
                    error
                );
                setError("Error al cargar las herramientas");
            })
            .finally(() => {
                setLoading(false);
            });
            
        }
    };

    //Función para buscar por ID
    const handleSearch = () => {
        if (!searchId.trim()) {
            setError("Por favor ingresa un ID para buscar");
            return;
        }
        
        setLoading(true);
        const searchValue = searchId.trim();
        
        // Verificar que sea numérico (solo ID)
        const isNumeric = /^\d+$/.test(searchValue);
        
        if (!isNumeric) {
            setError("Por favor ingresa un ID válido (solo números)");
            setLoading(false);
            return;
        }

        // Buscar por ID
        toolService
            .getById(searchValue)
            .then((response) => {
                console.log(`Mostrando herramienta con ID: ${searchValue}`, response.data);
                setFilteredTools([response.data]); // Convertir a array para consistencia
                setError(null);
            })
            .catch((error) => {
                console.log("Error al buscar por ID", error);
                if (error.response?.status === 404) {
                    setError(`No se encontró herramienta con ID: ${searchValue}`);
                } else {
                    setError("Error al buscar la herramienta por ID");
                }
                setFilteredTools([]);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    //Funcion para limpiar los filtros
    const clearFilters = () => {
        setStatusFilter('');
        setCategoryFilter('');
        setSearchId('');
        init();
    };

    useEffect(() => {
        init();
    }, []);

    const getStatusColor = (status) => {
        switch (status?.toLowerCase()) {
            case 'disponible':
                return 'success';
            case 'prestada':
                return 'warning';
            case 'en reparación':
                return 'success';
            case 'dada de baja':
                return 'error';
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
                Lista de Herramientas
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
                        
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth size="small">
                                <Select
                                    value={categoryFilter}
                                    onChange={(e) => handleCategoryFilter(e.target.value)}
                                    displayEmpty
                                    sx={{ 
                                        '& .MuiSelect-select': { 
                                            padding: '8.5px 14px',
                                            fontSize: '14px'
                                        }
                                    }}
                                >
                                    <MenuItem value="">
                                        <em>Filtrar por Categoría</em>
                                    </MenuItem>
                                    {availableCategories.map((category) => (
                                        <MenuItem key={category} value={category}>
                                            {category}
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
                                value={searchId}
                                onChange={(e) => setSearchId(e.target.value)}
                                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                placeholder="Buscar por ID (ej: 123)..."
                            />
                        </Grid>
                        
                        <Grid item xs={12} sm={4}>
                            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                                <Button
                                    variant="contained"
                                    size="small"
                                    onClick={handleSearch}
                                    disabled={!searchId.trim()}
                                >
                                    Buscar
                                </Button>
                                <Button
                                    variant="outlined"
                                    size="small"
                                    startIcon={<ClearIcon />}
                                    onClick={clearFilters}
                                    disabled={!statusFilter && !categoryFilter && !searchId}
                                >
                                    Limpiar
                                </Button>
                            </Box>
                        </Grid>
                    </Grid>
                    
                    {/* Indicador de resultados */}
                    <Typography variant="body2" sx={{ mt: 2, color: '#666' }}>
                        Mostrando {filteredTools.length} herramientas
                        {statusFilter && ` con estado: ${statusFilter.replace('_', ' ')}`}
                        {categoryFilter && ` en categoría: ${categoryFilter}`}
                        {searchId && ` con búsqueda: ${searchId}`}
                    </Typography>
                </CardContent>
            </Card>

            {filteredTools.length === 0 ? (
                <Alert severity="info">
                    {tools.length === 0 
                        ? "No hay herramientas registradas" 
                        : "No se encontraron herramientas con los filtros aplicados"
                    }
                </Alert>
            ) : (
                <TableContainer component={Paper} sx={{ mt: 2 }}>
                    <Table sx={{ minWidth: 650 }}>
                        <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
                            <TableRow>
                                <TableCell><strong>ID</strong></TableCell>
                                <TableCell><strong>Nombre</strong></TableCell>
                                <TableCell><strong>Categoría</strong></TableCell>
                                <TableCell><strong>Estado</strong></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {filteredTools.map((tool) => (
                                <TableRow
                                    key={tool.id}
                                    sx={{ '&:hover': { backgroundColor: '#f9f9f9' } }}
                                >
                                    <TableCell>{tool.id}</TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="medium">
                                            {tool.name || 'Sin nombre'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>{tool.category?.name || tool.category || 'Sin categoría'}</TableCell>
                                    <TableCell>
                                        <Chip
                                            label={tool.status || 'Sin estado'}
                                            color={getStatusColor(tool.status)}
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

export default ToolsList;