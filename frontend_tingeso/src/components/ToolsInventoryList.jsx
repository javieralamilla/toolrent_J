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

const ToolsInventoryList = () => {
    const [tools, setTools] = useState([]);
    const [filteredTools, setFilteredTools] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    //Estados para filtros
    const [searchValue, setSearchValue] = useState('');
    const [categoryFilter, setCategoryFilter] = useState('');

    const [availableCategories, setAvailableCategories] = useState([]);

    const init = () => {
        setLoading(true);
        toolService
            .getInventory()
            .then((response) => {
                console.log("Mostrando listado del inventario de las herramientas", response.data);
                
                // Ordenar las herramientas por ID de menor a mayor
                const sortedTools = response.data.sort((a, b) => a.id - b.id);
                
                setTools(sortedTools);
                setFilteredTools(sortedTools);

                // Extraer categorías únicas para el filtro
                const categories = [...new Set(sortedTools.map(tool => tool.category).filter(Boolean))];
                setAvailableCategories(categories);
                
                setError(null);
            })
            .catch((error) => {
                console.log(
                    "Se ha producido un error al intentar mostrar listado del inventario de las herramientas",
                    error
                );
                setError("Error al cargar el inventario de herramientas");
            })
            .finally(() => {
                setLoading(false);
            });
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
            .getInventoryByCategory(category)
            .then((response) => {
                console.log(`Mostrando inventario de herramientas con categoría: ${category}`, response.data);
                // Ordenar por ID las herramientas filtradas
                const sortedFilteredTools = response.data.sort((a, b) => a.id - b.id);
                setFilteredTools(sortedFilteredTools);
                setError(null);
            })
            .catch((error) => {
                console.log(
                    "Se ha producido un error al intentar mostrar el inventario de herramientas filtradas por categoría",
                    error
                );
                setError("Error al cargar el inventario de herramientas");
            })
            .finally(() => {
                setLoading(false);
            });
            
        }
    };

    //Función para buscar por ID o Nombre
    const handleSearch = () => {
        if (!searchValue.trim()) {
            setError("Por favor ingresa un ID o nombre válido");
            return;
        }
        
        setLoading(true);
        
        // Determinar si es un número (ID) o texto (nombre)
        const isNumeric = !isNaN(searchValue.trim()) && searchValue.trim() !== '';
        
        const searchPromise = isNumeric 
            ? toolService.findInventoryById(searchValue.trim())
            : toolService.findInventoryByName(searchValue.trim());
        
        searchPromise
            .then((response) => {
                console.log(`Mostrando herramienta con ${isNumeric ? 'ID' : 'nombre'}: ${searchValue}`, response.data);
                setFilteredTools([response.data]); // Convertir a array para consistencia
                setError(null);
            })
            .catch((error) => {
                console.log(
                    `Se ha producido un error al intentar mostrar el inventario de herramientas`,
                    error
                );
                if (error.response?.status === 404) {
                    setError(`No se encontró el inventario de herramientas con ${isNumeric ? 'ID' : 'nombre'}: ${searchValue}`);
                } else {
                    setError("Error al buscar el inventario de herramientas");
                }
                setFilteredTools([]);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    //Funcion para limpiar los filtros
    const clearFilters = () => {
        setCategoryFilter('');
        setSearchValue('');
        init();
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
                        <Grid size={{ xs: 12, sm: 6 }}>
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
                        <Grid size={{ xs: 12, sm: 8 }}>
                            <TextField
                                fullWidth
                                size="small"
                                value={searchValue}
                                onChange={(e) => setSearchValue(e.target.value)}
                                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                placeholder="Buscar por ID o nombre (ejemplo: 123 o martillo)"
                            />
                        </Grid>
                        
                        <Grid size={{ xs: 12, sm: 4 }}>
                            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                                <Button
                                    variant="contained"
                                    size="small"
                                    onClick={handleSearch}
                                    disabled={!searchValue.trim()}
                                >
                                    Buscar
                                </Button>
                                <Button
                                    variant="outlined"
                                    size="small"
                                    startIcon={<ClearIcon />}
                                    onClick={clearFilters}
                                    disabled={!categoryFilter && !searchValue}
                                >
                                    Limpiar
                                </Button>
                            </Box>
                        </Grid>
                    </Grid>
                    
                    {/* Indicador de resultados */}
                    <Typography variant="body2" sx={{ mt: 2, color: '#666' }}>
                        Mostrando {filteredTools.length} herramientas
                        {categoryFilter && ` en categoría: ${categoryFilter}`}
                        {searchValue && ` con búsqueda: ${searchValue}`}
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
                                <TableCell align="center"><strong>Total de herramientas</strong></TableCell>
                                <TableCell align="center"><strong>Stock actual</strong></TableCell>
                                <TableCell><strong>Valor de reposición</strong></TableCell>
                                <TableCell><strong>Tarifa de renta diaria</strong></TableCell>
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
                                    <TableCell>{tool.category || 'Sin categoría'}</TableCell>
                                    <TableCell align="center">
                                        <Typography 
                                            variant="body2" 
                                            fontWeight="medium"
                                            sx={{ 
                                                color: tool.totalTools !== undefined && tool.totalTools <= 5 
                                                    ? '#d32f2f' 
                                                    : tool.totalTools > 10 
                                                        ? '#2e7d32' 
                                                        : '#ed6c02'
                                            }}
                                        >
                                            {tool.totalTools !== undefined ? tool.totalTools : 'N/A'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell align="center">
                                        <Typography 
                                            variant="body2" 
                                            fontWeight="medium"
                                            sx={{ 
                                                color: tool.currentStock !== undefined && tool.currentStock <= 5 
                                                    ? '#d32f2f' 
                                                    : tool.currentStock > 10 
                                                        ? '#2e7d32' 
                                                        : '#ed6c02'
                                            }}
                                        >
                                            {tool.currentStock !== undefined ? tool.currentStock : 'N/A'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="medium" sx={{ color: '#1976d2' }}>
                                            {tool.replacementValue !== undefined 
                                                ? `$${Number(tool.replacementValue).toLocaleString('es-CO')}` 
                                                : 'N/A'
                                            }
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="medium" sx={{ color: '#1976d2' }}>
                                            {tool.dailyRentalRate !== undefined 
                                                ? `$${Number(tool.dailyRentalRate).toLocaleString('es-CO')}` 
                                                : 'N/A'
                                            }
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

export default ToolsInventoryList;